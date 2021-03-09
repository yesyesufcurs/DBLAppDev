# Necessary imports to run Flask normally
from flask import Flask, request, jsonify, Response
import sqlite3
from flask_cors import CORS
import json
import os

# Creation of Flaks App
app = Flask(__name__)
app.config['MAX_CONTENT_LENGTH'] = 10 * 1024 * 1024
CORS(app)

# Path to db. Change accordingly
db_file = os.getcwd() + "/backendserver/app.db"

def create_connection(db_file):
    conn = sqlite3.connect(db_file)
    conn.row_factory = sqlite3.Row
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
        connection.row_factory = sqlite3.Row
        cursor = connection.cursor()
        
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT * FROM user"
    cursor.execute(query)
    rows = cursor.fetchall()
    json_result = []
    for row in rows:
        drow = dict(zip(row.keys(), row))
        json_result.append(drow)
    return jsonify(json_result)

@app.route("/showAllExpenseGroups")
def showAllExpenseGroups():

    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        connection.row_factory = sqlite3.Row
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT * FROM expense_group"
    cursor.execute(query)
    rows = cursor.fetchall()
    json_result = []
    for row in rows:
        drow = dict(zip(row.keys(), row))
        json_result.append(drow)
    return jsonify(json_result)

@app.route("/showAllExpenses")
def showAllExpenses():

    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        connection.row_factory = sqlite3.Row
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT id, user_id, title, amount, content, expense_group_id FROM expense"
    cursor.execute(query)
    rows = cursor.fetchall()
    json_result = []
    for row in rows:
        drow = dict(zip(row.keys(), row))
        json_result.append(drow)
    return jsonify(json_result)

@app.route("/showExpensePicture/<int:id>")
def showExpensePicture(id):
    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        connection.row_factory = sqlite3.Row
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = f"SELECT picture FROM expense WHERE id = {id}"
    cursor.execute(query)
    binPic = cursor.fetchone()[0]
    picture = base64.b64encode(binPic).decode("utf-8")
    return render_template("picture.html", obj=binPic, image=picture)

@app.route("/showAllAccuredExpenses")
def showAllAccuredExpenses():

    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        connection.row_factory = sqlite3.Row
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT * FROM accured_expenses"
    cursor.execute(query)
    rows = cursor.fetchall()
    json_result = []
    for row in rows:
        drow = dict(zip(row.keys(), row))
        json_result.append(drow)
    return jsonify(json_result)

@app.route("/showAllExpenseGroupMembers")
def showAllExpenseGroupMembers():

    # create a database connection
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        connection.row_factory = sqlite3.Row
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    query = "SELECT * FROM expense_group_members"
    cursor.execute(query)
    rows = cursor.fetchall()
    json_result = []
    for row in rows:
        drow = dict(zip(row.keys(), row))
        json_result.append(drow)
    return jsonify(json_result)