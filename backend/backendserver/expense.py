from backend.backendserver import expense_group
from flask import Flask, request, jsonify, Response
from backendserver import app, db_file, create_connection
from backendserver.abstractAPI import AbstractAPI
import werkzeug
import re
import sqlite3
import json
import hashlib
import os

# @app.router("/createExpense")
# def createExpense():
#     '''
#     Create expense WITHOUT UPLOADING PICTURE!
#     '''
#     class CreateExpense(AbstractAPI):
#         def api_operation(self, user_id, conn):
#             cursor = conn.cursor()
#             expense_title, amount, picture, content, expense_group_id = None, None, None, None, None
#             try:
#                 expense_title = request.headers.get('title')
#                 amount = request.headers.get('amount')
#                 # Get picture
#                 content = request.headers.get('content')
#                 expense_group_id = request.header.get('expense_group_id')
#             except Exception as e:
#                 return jsonify(error=412, text="Expense group name missing")

#             # Determine how to send accured expenses!

@app.router("/getUsersExpenses")
def getUsersExpenses():
    '''
    Returns all expenses created by this user
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

@app.router("/getUserOwedExpenses")
def getUserOwedExpenses():
    '''
    Returns all expenses where the user owes someone money.
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

@app.router("/getExpenseDetails")
def getExpenseDetails():
    '''
    Returns expense details given expense_id
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

@app.router("/getOwedExpenses")
def getOwedExpenses():
    '''
    Returns how much each person owes the expense creator given expense_id.
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