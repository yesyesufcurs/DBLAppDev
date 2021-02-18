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

@app.router("/createExpense")
def createExpense():
    '''
    Create expense WITHOUT UPLOADING PICTURE!
    '''
    class CreateExpense(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_title, amount, picture, content, expense_group_id = None, None, None, None, None
            try:
                expense_title = request.headers.get('title')
                amount = request.headers.get('amount')
                # Get picture
                content = request.headers.get('content')
                expense_group_id = request.header.get('expense_group_id')
            except Exception as e:
                return jsonify(error=412, text="Expense group name missing")

            # Determine how to send accured expenses!
