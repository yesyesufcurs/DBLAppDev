from flask import Flask, request, jsonify, Response
from backendserver import app, db_file, create_connection
from backendserver.abstractAPI import AbstractAPI
from backendserver.permissionChecks import number_expense_group_members, isModerator, isMember
import sqlite3
import json
import hashlib
import os
import random
import time

@app.route("/getExpenseGroup")
def getExpenseGroup():
    '''
    Returns JSON containing the expense group name and moderator_id
    given a moderator
    expects headers:
    expense_group_id
    '''
    class GetExpenseGroup(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            query = '''SELECT expense_group.id, expense_group.name, moderator_id
            FROM expense_group_members, expense_group 
            WHERE user_id = ? AND 
            expense_group_members.expense_group_id = expense_group.id'''
            try:
                cursor.execute(query, (user_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot retrieve expense groups of this user"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)

    return GetExpenseGroup.template_method(GetExpenseGroup, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/getExpenseGroups")
def getExpenseGroups():
    '''
    Returns JSON containing all expense group a user is part of.
    Each entry is of the form expense_group_id, expense_group_name, moderator.
    '''
    class GetExpenseGroups(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_group_id = 0
            # Get headers
            try:
                expense_group_id = request.headers.get('expense_group_id')
            except Exception as e:
                return jsonify(error=412, text="Expense group id missing"), 412
            query = '''SELECT name, moderator_id
            FROM expense_group 
            WHERE id = ?'''
            try:
                cursor.execute(query, (expense_group_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense group info."), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)

    return GetExpenseGroups.template_method(GetExpenseGroups, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/getAllExpenseGroups")
def getAllExpenseGroups():
    '''
    Returns JSON containing all expense group available.
    Each entry is of the form expense_group_id, expense_group_name, moderator.
    '''
    class GetAllExpenseGroups(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            query = '''SELECT id, name, moderator_id
            FROM expense_group'''
            try:
                cursor.execute(query)
            except Exception as e:
                return jsonify(error=412, text="Cannot retrieve expense groups"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return GetAllExpenseGroups.template_method(GetAllExpenseGroups, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/createExpenseGroup")
def createExpenseGroup():
    '''
    Creates new expense group.
    Expects headers:
    expense_group_name: name of expense group
    Returns:
    expense_group_id.
    '''
    class CreateExpenseGroup(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_group_name = ""
            expense_group_id = 0
            # Get headers
            try:
                expense_group_name = request.headers.get('expense_group_name')
            except Exception as e:
                return jsonify(error=412, text="Expense group name missing"), 412
            # Add expense group to db
            query = '''INSERT INTO expense_group(id, name, moderator_id) VALUES (? ,?, ?)'''
            expense_group_id = generate_expense_group_id(cursor)
            try:
                cursor.execute(
                    query, (expense_group_id, expense_group_name, user_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot add expense group"), 412
            # Add moderator to db
            query = '''INSERT INTO expense_group_members VALUES (?, ?)'''
            try:
                cursor.execute(query, (expense_group_id, user_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot add moderator to expense group"), 412
            conn.commit()
            return jsonify(expense_group_id)

    return CreateExpenseGroup.template_method(CreateExpenseGroup, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/getExpenseGroupMembers")
def getExpenseGroupMembers():
    '''
    Returns members of expense group by id.
    Expects headers:
    expense_group_id: id of expense group
    Returns:
    json containing usernames of users in group.
    '''
    class GetExpenseGroupMembers(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_group_id = 0
            # Get headers
            try:
                expense_group_id = request.headers.get('expense_group_id')
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense group id"), 412
            # Check if user has permissions to see members
            try:
                if not(isMember(user_id, expense_group_id, cursor)):
                    return jsonify(error=412, text="User must be member of the expense group to see this."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412
            # Get expense group members
            query = '''SELECT * FROM expense_group_members WHERE expense_group_id = ?'''
            try:
                cursor.execute(query, (expense_group_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot retrieve expense group members"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return GetExpenseGroupMembers.template_method(GetExpenseGroupMembers, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/addToExpenseGroup")
def addToExpenseGroup():
    '''
    Adds user to expense group'
    Expects headers:
    user_id: user_id of person to be added
    expense_group_id: id of expense_group
    Returns "Added successfully" if added succefully.
    '''
    class AddToExpenseGroup(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_group_id, user = "", ""
            number_of_members = 0
            hasPermission = False
            # Get headers
            try:
                user = request.headers.get('user_id')
                expense_group_id = request.headers.get('expense_group_id')
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense group id or user id."), 412
            # Check permission to add person to expense group.
            try:
                # User may add themselves, or must be moderator.
                hasPermission = isModerator(
                    user_id, expense_group_id, cursor) or user == user_id
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions."), 412
            if not(hasPermission):
                return jsonify(error=412, text="Insufficient permissions to perform this action"), 412
            # Add person to expense group.
            try:
                number_of_members = number_expense_group_members(
                    expense_group_id, cursor)
            except Exception as e:
                return jsonify(error=412, text="Cannot get number of expense group members."), 412
            # Check requirement of at most 50 members.
            if number_of_members >= 50:
                return jsonify(error=412, text="Expense group full, can only have at most 50 members."), 412
            query = '''
            INSERT INTO expense_group_members(expense_group_id, user_id) VALUES (?, ?)'''
            try:
                cursor.execute(query, (expense_group_id, user))
            except Exception as e:
                return jsonify(error=412, text="Cannot add user to expense group"), 412
            conn.commit()
            return jsonify("Added successfully")
    return AddToExpenseGroup.template_method(AddToExpenseGroup, request.headers["api_key"] if "api_key" in request.headers else None)


def generate_expense_group_id(cursor):
    '''
    Generates random expense_group_id, while guaranteeing uniqueness.
    '''
    unique_id_found = False
    timestamp = int(time.time())
    random.seed(timestamp)
    id = 0
    while not(unique_id_found):
        id = random.randint(0, 1000000)
        cursor.execute("SELECT * FROM expense_group WHERE id = ?", (id,))
        unique_id_found = len(cursor.fetchall()) == 0
    return id

