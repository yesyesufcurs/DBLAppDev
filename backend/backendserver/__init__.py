
# Necessary imports to run Flask normally
from flask import Flask, request, jsonify, Response
import sqlite3
from flask_cors import CORS
import json

# Creation of Flaks App
app = Flask(__name__)
CORS(app)

# Path to db. Change accordingly
db_file = "/home/vincent/Documents/DBLAppDev/backend/backendserver/app.db"

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

def verify_API_key(api_key):
    cursor, connection = None, None

    connection = create_connection(db_file)
    cursor = connection.cursor()
    
    query = "SELECT id FROM user WHERE api_key = ?"
    cursor.execute(query, (api_key, ))
    result = cursor.fetchone()
    if result == None:
        raise Exception("API Key not valid")
    return result
