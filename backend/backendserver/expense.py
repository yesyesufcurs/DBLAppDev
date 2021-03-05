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
import base64

@app.route("/createExpense", methods=["POST"])
def createExpense():
    '''
    Create expense WITHOUT UPLOADING PICTURE!
    Expects headers:
    title, amount, description, expense_group_id
    Returns:
    expense_id
    '''
    class CreateExpense(AbstractAPI):
        def api_operation(self, user_id, conn):
            return "Image received"
            cursor = conn.cursor()
            expense_title, amount, picture, content, expense_group_id, expense_id = None, None, None, None, None, None
            # Get data from request
            try:
                expense_title = request.headers.get('title')
                amount = request.headers.get('amount')
                picture = request.get_json()['picture']
                content = request.headers.get('description')
                expense_group_id = request.headers.get('expense_group_id')
            except Exception as e:
                return jsonify(error=412, text="Expense group details missing."), 412
            if not(1 <= len(expense_title) < 100):
                return jsonify(error=412, text="Title should be non-empty and shorter than 100 characters."), 412
            if not(float(amount) < 100000):
                return jsonify(error=412, text="Expense amount should be lower than 100000"), 412
            # Convert base64 string to bytes
            bytePicture = base64.b64decode(picture)


            query = '''
            INSERT INTO expense(user_id, title, amount, picture, content, expense_group_id)
            VALUES (?, ?, ?, ?, ?, ?)
            '''
            try:
                cursor.execute(query, (user_id, expense_title, amount, bytePicture, content, expense_group_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot add expense to database"), 412
            query = '''SELECT last_insert_rowid()'''
            try:
                cursor.execute(query)
                expense_id = cursor.fetchone()[0]
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense_id"),412
            conn.commit()
            return jsonify(expense_id)
    return CreateExpense.template_method(CreateExpense, request.headers["api_key"] if "api_key" in request.headers else None)
@app.route("/createExpenseIOU/<iouJson>")
def createExpenseIOU(iouJson):
    '''
    Add how much each person owes the creator of an expense after creating an expense
    Expects json string in following form:
    {userid1:amount1, userid2:amount2, ...} id's should be in quotes!
    Expects headers:
    expense_id: id of expense where IOU has to be added to
    returns: "Added succesfully" if succesful.
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
            conn.commit()
            return jsonify("Added successfully")
    return CreateExpenseIOU.template_method(CreateExpenseIOU, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/getExpenseGroupExpenses")
def getExpenseGroupExpenses():
    ''' 
    Returns all expenses that are in an expense group.
    Expects: expense_group_id
    Returns: expense_id, user_id, title, amount, content, expense_group_id of expense
    '''
    class ExpenseGroupExpenses(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_group_id = ""
            try:
                expense_group_id = request.headers.get('expense_group_id')
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense_group_id"), 412
            query = ''' SELECT id, user_id, title, amount, content, expense_group_id
            FROM expense
            WHERE expense_group_id = ?'''
            try:
                cursor.execute(query, (expense_group_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense_group_expenses"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return ExpenseGroupExpenses.template_method(ExpenseGroupExpenses, request.headers["api_key"] if "api_key" in request.headers else None)


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
            return self.generateJson(self, result)
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
            return self.generateJson(self, result)
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
                expense_id = request.headers['expense_id']
            except Exception as e:
                return jsonify(error=412, text="Missing expense_id"), 412
            cursor = conn.cursor()
            query = ''' SELECT * FROM expense WHERE id = ?'''
            try:
                cursor.execute(query, (expense_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense details"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return GetExpenseDetails.template_method(GetExpenseDetails, request.headers["api_key"] if "api_key" in request.headers else None)

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
            try:
                expense_id = request.headers.get('expense_id')
            except Exception as e:
                return jsonify(error=412, text="Missing expense_id"), 412
            cursor = conn.cursor()
            query = ''' SELECT e.user_id, a.expense_id, a.user_id, a.amount, a.paid
            FROM expense AS e, accured_expenses AS a
            WHERE e.id = a.expense_id AND expense_id = ?'''
            try:
                cursor.execute(query, (expense_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense details"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return GetOwedExpenses.template_method(GetOwedExpenses, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/setUserPaidExpense")
def setUserPaidExpense():
    '''
    Toggles paid value in owedExpenses given expense_id and user_id
    Expects headers:
    expense_id
    user_id
    Returns:
    "Set successfully" if successful.
    '''
    class SetUserPaidExpense(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor= conn.cursor()
            expense_id = ""
            user = ""
            currentValue, newValue = -1, -1
            try:
                expense_id = request.headers.get('expense_id')
                user = request.headers.get('user_id')
            except Exception as e:
                return jsonify(error=412, text="Missing expense_id"), 412
            query = """
            SELECT paid FROM accured_expenses
            WHERE user_id = ? AND expense_id = ?
            """
            
            try:
                cursor.execute(query, (user, expense_id))
                currentValue = cursor.fetchone()[0]
                newValue = not(currentValue)
            except Exception as e:
                return jsonify(error=412, text="Cannot get current status of accured expense."), 412
            
            query = f'''
            UPDATE accured_expenses
            SET paid = {str(newValue)}
            WHERE user_id = ? AND expense_id = ? '''
            try:
                cursor.execute(query, (user, expense_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot set paid value to true"), 412
            conn.commit()
            return jsonify("Set successfully")
    return SetUserPaidExpense.template_method(SetUserPaidExpense, request.headers["api_key"] if "api_key" in request.headers else None)