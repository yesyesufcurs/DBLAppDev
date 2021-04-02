from abc import abstractmethod
from flask import Flask, jsonify, Response
from backendserver import app, db_file, create_connection
import re
import sqlite3
import json
import hashlib
import os
import time
import inspect

# Last API Request
lastAPIRequestHeaders = None
lastAPIRequestName = None
lastAPIRequestTime = 0.0
# Last API Request response
lastAPIRequestResponse = None
lastAPIRequestUserID = None

class AbstractAPI(object):
    '''
    AbstractAPI class using template method design pattern.
    Contains the general structure of an api key using base_methods

    To use this class, create new class inheriting AbstractAPI and override the api_operation method.
    '''
    user_id = ""
    cursor, conn = None, None

    def template_method(self, headers):
        # Get global variables
        global lastAPIRequestName, lastAPIRequestTime, lastAPIRequestResponse, lastAPIRequestUserID, lastAPIRequestHeaders
        # Get apiRequestName
        apiRequestName = inspect.stack()[1][3]
        # Get API key from headers
        if not ("api_key" in headers):
            return jsonify(error=412, text="API key missing"), 412


        # Check API key for validity and user
        try:
            user_id = self.verify_api_key(self, headers['api_key'])
        except Exception as e:
            return jsonify(error=412, text="API key invalid"), 412

        # Check for Android Voley bug
        if (user_id == lastAPIRequestUserID and time.time() - lastAPIRequestTime < 2 and apiRequestName == lastAPIRequestName and lastAPIRequestHeaders == headers):
            return lastAPIRequestResponse

        # Establish general database connection
        try:
            conn = create_connection(db_file)
            cursor = conn.cursor()
        except Exception as e:
            return jsonify(error=500, text="could not connect to database"), 500

        # do operation
        lastAPIRequestResponse = self.api_operation(self, user_id, conn)
        # Set other values
        lastAPIRequestUserID = user_id
        lastAPIRequestName = apiRequestName
        lastAPIRequestTime = time.time()
        lastAPIRequestHeaders = headers
        return lastAPIRequestResponse

    def verify_api_key(self, api_key):
        cursor, connection = None, None

        connection = create_connection(db_file)
        cursor = connection.cursor()

        query = "SELECT id FROM user WHERE api_key = ?"
        cursor.execute(query, (api_key, ))
        result = cursor.fetchone()
        if result == None:
            raise Exception("API Key not valid")
        return result[0]

    def generateJson(self, rows):
        json_result = []
        for row in rows:
            drow = dict(zip(row.keys(), row))
            for i in drow.keys():
                drow[i] = str(drow[i])
            json_result.append(drow)
        return jsonify(json_result)

    @abstractmethod
    def api_operation(self, user_id, cursor):
        pass
