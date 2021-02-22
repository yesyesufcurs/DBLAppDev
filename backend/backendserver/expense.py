from flask import Flask, request, jsonify, Response
from backendserver import app, db_file, create_connection
from backendserver.abstractAPI import AbstractAPI
import backendserver.expense_group
import werkzeug
import re
import sqlite3
import json
import hashlib
import os

@app.route("/createExpense")
def createExpense():
    '''
    Create expense WITHOUT UPLOADING PICTURE!
    Expects headers:
    title, amount, content, expense_group_id
    Returns:
    expense_id
    '''
    class CreateExpense(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_title, amount, picture, content, expense_group_id, expense_id = None, None, None, None, None, None
            try:
                expense_title = request.headers.get('title')
                amount = request.headers.get('amount')
                # Get picture
                content = request.headers.get('content')
                expense_group_id = request.header.get('expense_group_id')
            except Exception as e:
                return jsonify(error=412, text="Expense group name missing")

            # Determine how to send accured expenses!
            query = '''
            INSERT INTO expense(user_id, title, amount, content, expense_group_id)
            VALUES (?, ?, ?, ?, ?)
            '''
            try:
                cursor.execute(query, (user_id, expense_title, amount, content, expense_group_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot add expense to database")
            query = '''SELECT last_insert_rowid()'''
            try:
                cursor.execute(query)
                expense_id = cursor.fetchone()[0]
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense_id"),412
            return jsonify(expense_id)

@app.route("/createExpenseIOU/<iouJson>")
def createExpenseIOU(iouJson):
    '''
    Add how much each person owes the creator of an expense after creating an expense
    Expects json string in following form:
    {userid1:amount1, userid2:amount2, ...}
    Expects headers:
    expense_id: id of expense where IOU has to be added to
    returns: Added succesfully if succesful.
    '''
    class CreateExpenseIOU(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            iou = json.loads(iouJson)
            expense_id = ""
            query = ''' INSERT INTO accured_expenses VALUES (?, ?, ?, ?) '''
            try:
                expense_id = request.headers.get('expense_id')
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense id"), 412
            for key in iou:
                try:
                    cursor.execute(query, (expense_id, key, iou[key], False))
                except Exception as e:
                    return jsonify(error=412, text="Cannot add transaction"), 412
            return jsonify("Added successfully")
    return CreateExpenseIOU.template_method(CreateExpenseIOU, request.headers["api_key"] if "api_key" in request.headers else None)




@app.route("/getUsersExpenses")
def getUsersExpenses():
    '''
    Returns all expenses created by user that makes the request
    Expects: None
    Returns: expense_id, user_id, title, amount, content, expense_group_id of expense
    '''
    class GetUsersExpenses(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            query = ''' SELECT id, user_id, title, amount, content, expense_group_id
            FROM expense
            WHERE user_id = ?'''
            try:
                cursor.execute(query, (user_id, ))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expenses"), 412
            result = cursor.fetchall()
            return jsonify(result)
    return GetUsersExpenses.template_method(GetUsersExpenses, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/getUserOwedExpenses")
def getUserOwedExpenses():
    '''
    Returns all expenses where the user owes someone money.
    Expects: None
    Returns: expense_id, user_id, title, amount, content, amount, paid
    '''
    class GetUserOwedExpenses(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            query = ''' SELECT e.id, e.user_id, e.title, e.amount, e.content, e.expense_group_id, a.amount, a.paid
            FROM expense AS e, accured_expenses AS a
            WHERE e.id = a.expense_id AND a.user_id = ?'''
            try:
                cursor.execute(query, (user_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get owed expenses for user"), 412
            result = cursor.fetchall()
            return jsonify(result)
    return GetUserOwedExpenses.template_method(GetUserOwedExpenses, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/getExpenseDetails")
def getExpenseDetails():
    '''
    Returns expense details given expense_id
    Expects headers:
    expense_id: id of expense
    Returns: expense_id, user_id, title, amount, content, expense_group_id of expense
    '''
    class GetExpenseDetails(AbstractAPI):
        def api_operation(self, user_id, conn):
            expense_id = ""
            try:
                expense_id = request.headers.get('expense_id')
            except Exception as e:
                return jsonify(error=412, text="Missing expense_id")
            cursor = conn.cursor()
            query = ''' SELECT * FROM expense WHERE expense_id = ?'''
            try:
                cursor.execute(query, (expense_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense details"), 412
            result = cursor.fetchone()
            return jsonify(result)

@app.route("/getOwedExpenses")
def getOwedExpenses():
    '''
    Returns how much each person owes the expense creator given expense_id.
    Expects headers:
    expense_id
    Returns:
    user_id of creator, expense_id, user_id of the ower, amount, paid
    '''
    class GetOwedExpenses(AbstractAPI):
        def api_operation(self, user_id, conn):
            expense_id = ""
            user_id = ""
            try:
                expense_id = request.headers.get('expense_id')
            except Exception as e:
                return jsonify(error=412, text="Missing expense_id")
            cursor = conn.cursor()
            query = ''' SELECT e.user_id, a.expense_id, a.user_id, a.amount, a.paid
            FROM expense AS e, accured_expenses AS a
            WHERE e.id = a.expense_id AND expense_id = ?'''
            try:
                cursor.execute(query, (expense_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense details"), 412
            result = cursor.fetchone()
            return jsonify(result)