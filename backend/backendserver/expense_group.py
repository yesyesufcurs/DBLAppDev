from flask import Flask, request, jsonify, Response
from backendserver import app, db_file, create_connection
from backendserver.abstractAPI import AbstractAPI
import re
import sqlite3
import json
import hashlib
import os

@app.route("/getExpenseGroups")
def getExpenseGroups():
    '''
    Returns JSON containing all expense group a user is part of.
    Each entry is of the form expense_group_id, expense_group_name, moderator.
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
                return jsonify(error=412, text="Cannot retrieve expense groups of this user"),412
            result = cursor.fetchall()
            return jsonify(result)
    
    return GetExpenseGroup.template_method(GetExpenseGroup, request.headers["api_key"] if "api_key" in request.headers else None)

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
                return jsonify(error=412, text="Cannot retrieve expense groups"),412
            result = cursor.fetchall()
            return jsonify(result)
    return GetAllExpenseGroups.template_method(GetAllExpenseGroups, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/createExpenseGroup")
def createExpenseGroup():
    class CreateExpenseGroup(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_group_name = ""
            expense_group_id = 0
            try:
                expense_group_name = request.headers.get('expense_group_name')
            except Exception as e:
                return jsonify(error=412, text="Expense group name missing"),412
            query = '''INSERT INTO expense_group(name, moderator_id) VALUES (?, ?)'''
            try:
                cursor.execute(query, (expense_group_name, user_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot complete operation"),412
            query = '''SELECT last_insert_rowid()'''
            try:
                cursor.execute(query)
                expense_group_id = cursor.fetchone()[0]
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense_group_id"),412
            query = '''INSERT INTO expense_group_members VALUES (?, ?)'''
            try:
                cursor.execute(query, (expense_group_id, user_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot add moderator to expense group"),412
            conn.commit()
            return jsonify("Expense group has been created successfully")

    return CreateExpenseGroup.template_method(CreateExpenseGroup, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/getExpenseGroupMembers")
def getExpenseGroupMembers():
    class GetExpenseGroupMembers(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_group_id = 0
            try:
                expense_group_id = request.headers.get('expense_group_id')
            except Exception as e:
                return jsonify(error=412)
            query = '''SELECT * FROM expense_group_members WHERE expense_group_id = ?'''
            try:
                cursor.execute(query, (expense_group_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot retrieve expense group members"),412
            result = cursor.fetchall()
            return jsonify(result)
    return GetExpenseGroupMembers.template_method(GetExpenseGroupMembers, request.headers["api_key"] if "api_key" in request.headers else None)

