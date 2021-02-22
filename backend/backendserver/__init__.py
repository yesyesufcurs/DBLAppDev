
# Necessary imports to run Flask normally
from flask import Flask, request, jsonify, Response
import sqlite3
from flask_cors import CORS
import json

# Creation of Flaks App
app = Flask(__name__)
app.config['MAX_CONTENT_LENGTH'] = 10 * 1024 * 1024
CORS(app)

# Path to db. Change accordingly
# db_file = "/home/vincent/Documents/DBLAppDev/backend/backendserver/app.db"
db_file = "/mnt/c/Users/Vincent/AndroidStudioProjects/App/backend/backendserver/app.db"

def create_connection(db_file):
    conn = None
    conn = sqlite3.connect(db_file)
    return conn

# import functionallity of backend
import backendserver.login
import backendserver.expense
import backendserver.expense_group

 
# Default homepage
@app.route("/")
def home():
    return "Backend running normally"

### WARNING: Methods underneath are for internal use, do not push to production
@app.route("/showAllUsers")
def showAllUsers():

    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT * FROM user"
    cursor.execute(query)
    result = cursor.fetchall()
    return jsonify(result)

@app.route("/showAllExpenseGroups")
def showAllExpenseGroups():

    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT * FROM expense_group"
    cursor.execute(query)
    result = cursor.fetchall()
    return jsonify(result)

@app.route("/showAllExpenses")
def showAllExpenses():

    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT * FROM expense"
    cursor.execute(query)
    result = cursor.fetchall()
    return jsonify(result)

@app.route("/showAllAccuredExpenses")
def showAllAccuredExpenses():

    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT * FROM accured_expenses"
    cursor.execute(query)
    result = cursor.fetchall()
    return jsonify(result)

@app.route("/showAllExpenseGroupMembers")
def showAllExpenseGroupMembers():

    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT * FROM expense_group_members"
    cursor.execute(query)
    result = cursor.fetchall()
    return jsonify(result)