
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
    try:
        conn = sqlite3.connect(db_file)
    except Error as e:
        print(e)

    return conn

# import functionallity of backend
import backendserver.login



 
# Default homepage
@app.route("/")
def home():
    return "Backend running normally"



def select_all_tasks(conn):
    """
    Query all rows in the tasks table
    :param conn: the Connection object
    :return:
    """
    cur = conn.cursor()
    cur.execute("SELECT * FROM user")

    rows = cur.fetchall()

    return jsonify(rows)


@app.route("/showAllUsers")
def main():

    # create a database connection
    conn = create_connection(db_file)
    with conn:
        
        print("2. Query all tasks")
        return select_all_tasks(conn)

