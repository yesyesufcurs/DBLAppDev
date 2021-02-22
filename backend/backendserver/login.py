from flask import Flask, request, jsonify, Response
from backendserver import app, db_file, create_connection
import re
import sqlite3
import json
import hashlib
import os


@app.route("/register")
def register():
    """
    Registers a user to the application
    Expected headers:
    username: username
    password: password
    email: valid email address
    @pre username != None && password != None && email != None &&
         valid_username(username) && valid_email(email)
    @modifies database
    @returns api_key if successful
    """
    username, password, email, api_key = "", "", "", ""
    try:
        username = request.headers["username"]
        password = request.headers["password"]
        email = request.headers["email"]
    except Exception as e:
        return jsonify(error=412, text="username/password/email header missing"), 412
    
    if not len(username) <= 30:
        return jsonify(error=412, text="username must be shorter than 30 characters"), 412

    if not len(password) >= 6:
        return jsonify(error=412, text="password must be at least 6 characters"), 412 

    
    if not valid_email(email):
        return jsonify(error=412, text="email is not valid"), 412
    
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500
    
    if not valid_username(username, cursor):
        return jsonify(error=412, text="username already exists"), 412
    else:
        query = '''INSERT INTO user(id, password, email, api_key)
        VALUES(?,?,?,?)'''
        api_key = key_gen(username)
        cursor.execute(query, (username, obfuscate(username, password), email, api_key))
        connection.commit()

    return jsonify(api_key)

@app.route("/login")
def login():
    '''
    Login user given username and password
    Expected headers:
    username: username of the user
    password: password of the user
    returns:
    api_key
    '''
    username, password, api_key = "", "", ""
    try:
        username = request.headers["username"]
        password = request.headers["password"]
    except Exception as e:
        return jsonify(error=412, text="username/password header missing"), 412
    
    cursor, connection = None, None
    
    try:
        connection = create_connection(db_file)
        cursor = connection.cursor()
    except Exception as e:
        return jsonify(error=500, text="could not connect to database"), 500

    try:
        api_key = verify_login(username, password, cursor)
    except Exception as e:
        return jsonify(error=412, text="username or password incorrect"), 412
    
    return jsonify(api_key)
    

def valid_username(username, cursor):
    query = "SELECT id FROM user WHERE id = ?"
    cursor.execute(query, (username,))
    result = cursor.fetchone()
    if result:
        return False
    else:
        return True

# Source: https://emailregex.com/
def valid_email(email):
    return re.match(r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)", email)

# General way to obfuscate passwords
# = SHA256(SHA256(username + password + password + username))
def obfuscate(username, password):
    stringToHash = username + password + password + username
    stringToHash = hashlib.sha256(stringToHash.encode('utf-8')).hexdigest()
    return hashlib.sha256(stringToHash.encode('utf-8')).hexdigest()

# General way to generate api_key
# = SHA256(username + os.uranom(64).hex())
def key_gen(username):
    stringToHash = username + os.urandom(64).hex()
    return hashlib.sha256(stringToHash.encode('utf-8')).hexdigest()

def verify_login(username, password, cursor):
    query = "SELECT password, api_key FROM user WHERE id = ?"
    cursor.execute(query, (username, ))
    result = cursor.fetchone()
    if result == None:
        raise Exception("Username not found")
    if result[0] != obfuscate(username, password):
        raise Exception("Password not found")
    return result[1]

