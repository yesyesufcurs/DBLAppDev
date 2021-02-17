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
    class GetExpenseGroup(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            query = '''SELECT expense_group.id, expense_group.name 
            FROM expense_group_members, expense_group 
            WHERE user_id = ? AND 
            expense_group_members.expense_group_id = expense_group.id'''
            try:
                cursor.execute(query, (user_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot retrieve expense groups of this user")
            result = cursor.fetchall()
            return jsonify(result)
    
    return GetExpenseGroup.template_method(GetExpenseGroup, request.headers["api_key"] if "api_key" in request.headers else None)

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
                return jsonify(error=412, text="Expense group name missing")
            query = '''INSERT INTO expense_group(name, moderator_id) VALUES (?, ?)'''
            try:
                cursor.execute(query, (expense_group_name, user_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot complete operation")
            query = '''SELECT last_insert_rowid()'''
            try:
                cursor.execute(query)
                expense_group_id = cursor.fetchone()[0]
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense_group_id")
            query = '''INSERT INTO expense_group_members VALUES (?, ?)'''
            try:
                cursor.execute(query, (expense_group_id, user_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot add moderator to expense group")
            conn.commit()
            return jsonify("Expense group has been created successfully")

    return CreateExpenseGroup.template_method(CreateExpenseGroup, request.headers["api_key"] if "api_key" in request.headers else None)



