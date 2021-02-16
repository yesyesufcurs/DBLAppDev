from flask import Flask, request, jsonify, Response
from backendserver import app, db_file, create_connection
import re
import sqlite3
import json
import hashlib
import os

@app.route("/getExpenseGroups")
def getExpenseGroups():
    user_id, api_key ="", ""
    try:
        api_key = request.headers["api_key"]
    except Exception as e:
        return jsonify(error=412, text=e.message)
    
    try:
        user_id = verify_API_key(api_key)
    except Exception as e:
        return jsonify(error=412, text="API key invalid")
    
    query = f"SELECT expense_group_id FROM expense_group_members WHERE user_id = {user_id}"

    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    cursor.execute(query)
    result = cursor.fetchall()
    return jsonify(result)


