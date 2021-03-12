from flask import Flask, request, jsonify, Response, send_file
from backendserver import app, db_file, create_connection
from backendserver.abstractAPI import AbstractAPI
import backendserver.expense_group
from backendserver.permissionChecks import isModerator, isMember, isExpenseCreator, getExpenseGroup
import werkzeug
import re
import sqlite3
import json
import hashlib
import os
import base64
import time
from io import BytesIO


@app.route("/createExpense", methods=["POST"])
def createExpense():
    '''
    Creates new expense and adds it do expense group
    Expects headers:
    title, amount, picture, description, expense_group_id
    Optional headers:
    user_id (only needed if caller is not the expense creator)
    Returns:
    expense_id
    '''
    class CreateExpense(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_title, amount, picture, content, expense_group_id, expense_id, user = None, None, None, None, None, None, None

            # Get data from request
            try:
                expense_title = request.headers.get('title')
                amount = request.headers.get('amount')
                # Picture is optional
                picture = request.form['picture'] if "picture" in request.form else None
                content = request.headers.get('description')
                expense_group_id = request.headers.get('expense_group_id')
                # User is optional
                user = request.headers['user_id'] if 'user_id' in request.headers else None
            except Exception as e:
                return jsonify(error=412, text="Expense group details missing."), 412

            # Check requirements for new expense.
            if not(1 <= len(expense_title) < 100):
                return jsonify(error=412, text="Title should be non-empty and shorter than 100 characters."), 412
            if not(float(amount) < 100000):
                return jsonify(error=412, text="Expense amount should be lower than 100000"), 412

            # Check if user has permissions to add the expense to the expense group
            try:
                # If caller is also user to be added
                if user == None or user == user_id:
                    if not(isMember(user_id, expense_group_id, cursor)):
                        return jsonify(error=412, text="User must be member of the expense group to add an expense."), 412
                # If caller is not the user to be added
                else:
                    # User to be added should be part of the expense group
                    if not(isMember(user, expense_group_id, cursor)):
                        return jsonify(error=412, text="User must be member of the expense group to add an expense."), 412
                    # The person trying to add the expense should be a moderator
                    if (not(isModerator(user_id, expense_group_id, cursor))):
                        return jsonify(error=412, text="Caller must be moderator to create expense for someone else."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412

            # Convert base64 string to bytes
            if picture != None:
                bytePicture = base64.b64decode(picture)
                detect_document(bytePicture)
            else:
                bytePicture = None
            

            # Execute query to add expense
            query = '''
            INSERT INTO expense(user_id, title, amount, picture, content, expense_group_id)
            VALUES (?, ?, ?, ?, ?, ?)
            '''
            try:
                if user == None:
                    cursor.execute(query, (user_id, expense_title,
                     amount, bytePicture, content, expense_group_id))
                else:
                    cursor.execute(query, (user, expense_title,
                     amount, bytePicture, content, expense_group_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot add expense to database"), 412
            # Retrieve expense id
            query = '''SELECT last_insert_rowid()'''
            try:
                cursor.execute(query)
                expense_id = cursor.fetchone()[0]
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense_id"), 412
            conn.commit()
            # Return expense id
            return jsonify(expense_id)
    return CreateExpense.template_method(CreateExpense, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/modifyExpense", methods=["POST"])
def modifyExpense():
    '''
    Modifies existing expense 
    Expects headers:
    title, amount, description, expense_group_id, expense_id
    Optional headers:
    picture (only needed if changed)
    Returns:
    Changed successfully
    '''
    class ModifyExpense(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_title, amount, picture, content, expense_group_id, expense_id = None, None, None, None, None, None
            # Get data from request
            try:
                expense_title = request.headers.get('title')
                amount = request.headers.get('amount')
                # Picture is optional
                picture = request.form['picture'] if "picture" in request.form else None
                content = request.headers.get('description')
                expense_group_id = request.headers.get('expense_group_id')
                expense_id = request.headers.get('expense_id')
            except Exception as e:
                return jsonify(error=412, text="Expense group details missing."), 412
            # Check if user has permission to alter the expense
            try:
                if not(isExpenseCreator(user_id, expense_id, cursor) or isModerator(user_id, expense_group_id, cursor)):
                    return jsonify(error=412, text="User is not a moderator or creator of expense."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412
            # Convert base64 string to bytes
            if picture != None:
                bytePicture = base64.b64decode(picture)
            else:
                bytePicture = None

            query = ""
            conditions = None
            if picture == None:
                query = """
                UPDATE expense
                SET title = ?, amount = ?, content = ?, expense_group_id = ?
                WHERE id = ? 
                """
                conditions = (expense_title, amount, content, expense_group_id, expense_id)
            else:
                query = """
                UPDATE expense
                SET title = ?, amount = ?, picture = ?, content = ?, expense_group_id = ?
                WHERE id = ? 
                """
                conditions = (expense_title, amount, bytePicture, content, expense_group_id, expense_id)
            try:
                cursor.execute(query, conditions)
            except Exception as e:
                return jsonify(error=412, text="Cannot update expense"), 412
            conn.commit()
            # Return expense id
            return jsonify("Updated Successfully")
    return ModifyExpense.template_method(ModifyExpense, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/removeExpense")
def removeExpense():
    """
    Removes expense from the whole database.
    Expects headers:
    expense_id
    Returns: 
    'Removed successfully' if removed successfully
    """ 
    class RemoveExpense(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_id = None
            # Get Headers
            try:
                expense_id = request.headers.get('expense_id')
            except Exception as e:
                return jsonify(error=412, text="Expense id missing"), 412

            # Check if user has permissions to delete expense
            try:
                expense_group_id = getExpenseGroup(expense_id, cursor)
                if not(isExpenseCreator(user_id, expense_id, cursor) or isModerator(user_id, expense_group_id, cursor)):
                    return jsonify(error=412, text="Caller has no permission to remove expense"), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412
            
            # Remove expense
            query1 = """
            DELETE FROM accured_expenses WHERE expense_id = ?
            """
            query2 = """
            DELETE FROM expense WHERE id = ?
            """
            try:
                cursor.execute(query1, (expense_id,))
                cursor.execute(query2, (expense_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot remove expense"), 412
            conn.commit()
            return jsonify("Removed successfully")
    return RemoveExpense.template_method(RemoveExpense, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/getExpensePicture/<expenseid>/<apikey>", methods=["GET", "POST"])
def getExpensePicture(expenseid, apikey):
    """
    Returns HTML Website with the picture of the expense
    Expects headers:
    expense_id: id of expense.
    Returns: 
    HTML website containing picture
    """
    class GetExpensePicture(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_id, pictureBytes = None, None

            # Get headers
            try:
                expense_id = expenseid
            except Exception as e:
                return jsonify(error=412, text="Expense id missing"), 412

            # Check if user has permissions to get the expense picture
            try:
                if not(isMember(user_id, expense_group_id, cursor)):
                    return jsonify(error=412, text="User must be member of the expense group to see an expense picture."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412

            # Get picture
            query = "SELECT picture from expense where id = ?"

            try:
                cursor.execute(query, (expense_id,))
                pictureBytes = cursor.fetchone()[0]
            except Exception as e:
                return jsonify(error=412, text="Cannot get picture"), 412
            return send_file(BytesIO(pictureBytes),
                             attachment_filename=f"expense_id_{expense_id}",
                             mimetype='image/jpg')
    return GetExpensePicture.template_method(GetExpensePicture, apikey)


@app.route("/createExpenseIOU/<iouJson>")
def createExpenseIOU(iouJson):
    '''
    Add how much each person owes the creator of an expense after creating an expense
    Expects json string in following form:
    {userid1:amount1, userid2:amount2, ...} id's should be in quotes!
    Expects headers:
    expense_id: id of expense where IOU has to be added to
    returns: "Added succesfully" if succesful.
    '''
    class CreateExpenseIOU(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            iou = json.loads(iouJson)
            expense_id = ""
            query = ''' INSERT INTO accured_expenses VALUES (?, ?, ?, ?) '''
            # Get headers
            try:
                expense_id = request.headers.get('expense_id')
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense id"), 412

            # Check permissions of caller
            try:
                if not(isExpenseCreator(user_id, expense_id, cursor)):
                    return jsonify(error=412, text="User must be the creator of the expense to add this."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412

            # Iterate through iouJson and add each Accured Expense to db.
            for key in iou:
                try:
                    cursor.execute(query, (expense_id, key, iou[key], False))
                except Exception as e:
                    return jsonify(error=412, text="Cannot add transaction"), 412
            conn.commit()
            return jsonify("Added successfully")
    return CreateExpenseIOU.template_method(CreateExpenseIOU, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/getExpenseGroupExpenses")
def getExpenseGroupExpenses():
    ''' 
    Returns all expenses that are in an expense group.
    Expects: expense_group_id
    Returns: expense_id, user_id, title, amount, content, expense_group_id of expense
    '''
    class ExpenseGroupExpenses(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_group_id = ""
            # Get headers
            try:
                expense_group_id = request.headers.get('expense_group_id')
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense_group_id"), 412

            # Check if user has permissions to get the expense picture
            try:
                if not(isMember(user_id, expense_group_id, cursor)):
                    return jsonify(error=412, text="User must be member of the expense group to see group expenses."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412

            # Get expense groups from db.
            query = ''' SELECT id, user_id, title, amount, content, expense_group_id
            FROM expense
            WHERE expense_group_id = ?'''
            try:
                cursor.execute(query, (expense_group_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense_group_expenses"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return ExpenseGroupExpenses.template_method(ExpenseGroupExpenses, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/getUsersExpenses")
def getUsersExpenses():
    '''
    Returns all expenses created by user that makes the request
    Expects: None
    Returns: expense_id, user_id, title, amount, content, expense_group_id of expense
    '''
    class GetUsersExpenses(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            # Get users expenses from db.
            query = ''' SELECT id, user_id, title, amount, content, expense_group_id
            FROM expense
            WHERE user_id = ?'''
            try:
                cursor.execute(query, (user_id, ))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expenses"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return GetUsersExpenses.template_method(GetUsersExpenses, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/getUserOwedExpenses")
def getUserOwedExpenses():
    '''
    Returns all expenses where the user owes someone money.
    Expects: None
    Returns: expense_id, user_id, title, amount, content, amount, paid
    '''
    class GetUserOwedExpenses(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            # Get users expenses from db.
            query = ''' SELECT e.id, e.user_id, e.title, e.amount, e.content, e.expense_group_id, a.amount, a.paid
            FROM expense AS e, accured_expenses AS a
            WHERE e.id = a.expense_id AND a.user_id = ?'''
            try:
                cursor.execute(query, (user_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get owed expenses for user"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return GetUserOwedExpenses.template_method(GetUserOwedExpenses, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/getExpenseDetails")
def getExpenseDetails():
    '''
    Returns expense details given expense_id
    Expects headers:
    expense_id: id of expense
    Returns: expense_id, user_id, title, amount, content, expense_group_id of expense
    '''
    class GetExpenseDetails(AbstractAPI):
        def api_operation(self, user_id, conn):
            expense_id = ""
            # Get headers
            try:
                expense_id = request.headers['expense_id']
            except Exception as e:
                return jsonify(error=412, text="Missing expense_id"), 412
            cursor = conn.cursor()
            # Check if user has permissions to get the expense details
            try:
                expense_group_id = getExpenseGroup(expense_id, cursor)
                if not(isMember(user_id, expense_group_id, cursor)):
                    return jsonify(error=412, text="User must be member of the expense group to see expense details."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412
            # Get expense details
            query = ''' SELECT * FROM expense WHERE id = ?'''
            try:
                cursor.execute(query, (expense_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense details"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return GetExpenseDetails.template_method(GetExpenseDetails, request.headers["api_key"] if "api_key" in request.headers else None)


@app.route("/getOwedExpenses")
def getOwedExpenses():
    '''
    Returns how much each person owes the expense creator given expense_id.
    Expects headers:
    expense_id
    Returns:
    user_id of creator, expense_id, user_id of the ower, amount, paid
    '''
    class GetOwedExpenses(AbstractAPI):
        def api_operation(self, user_id, conn):
            expense_id = ""
            # Get headers
            try:
                expense_id = request.headers.get('expense_id')
            except Exception as e:
                return jsonify(error=412, text="Missing expense_id"), 412
            cursor = conn.cursor()
            # Check if user has permissions to get the owed expenses
            try:
                expense_group_id = getExpenseGroup(expense_id, cursor)
                if not(isMember(user_id, expense_group_id, cursor)):
                    return jsonify(error=412, text="User must be member of the expense group to see an expense picture."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412
            # Get expenses where user owes someone money
            query = ''' SELECT e.user_id, a.expense_id, a.user_id, a.amount, a.paid
            FROM expense AS e, accured_expenses AS a
            WHERE e.id = a.expense_id AND expense_id = ?'''
            try:
                cursor.execute(query, (expense_id,))
            except Exception as e:
                return jsonify(error=412, text="Cannot get expense details"), 412
            result = cursor.fetchall()
            return self.generateJson(self, result)
    return GetOwedExpenses.template_method(GetOwedExpenses, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/removeOwedExpense")
def removeOwedExpense():
    '''
    Removes person from owed expenses given an expense_id and user_id.
    Expects headers:
    expense_id
    user_id
    Returns:
    "Removed successfully" if successfully removed.
    '''
    class RemoveOwedExpense(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_id, user = None, None
            # Get headers
            try:
                expense_id = request.headers.get('expense_id')
                user = request.headers.get('user_id')
            except Exception as e:
                return jsonify(error=412, text="MIssing expense_id or user_id"), 412

            # Check if user has permission to remove from owed expenses
            try:
                expense_group_id = getExpenseGroup(expense_id, cursor)
                if not(isExpenseCreator(user_id, expense_id, cursor) or isModerator(user_id, expense_group_id, cursor)):
                    return jsonify(error=412, text="User must be member of the expense group to see an expense picture."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412
            
            #Execute query
            query = '''
            DELETE FROM accured_expenses
            WHERE expense_id = ? AND user_id = ?
            '''
            try:
                cursor.execute(query, (expense_id, user))
            except Exception as e:
                return jsonify(error=412, text="Cannot remove accured expense"), 412
            cursor.commit()
            return jsonify("Removed successfully")
    return RemoveOwedExpense.template_method(RemoveOwedExpense, request.headers["api_key"] if "api_key" in request.headers else None)

@app.route("/setUserPaidExpense")
def setUserPaidExpense():
    '''
    Toggles paid value in owedExpenses given expense_id and user_id
    Expects headers:
    expense_id
    user_id
    Returns:
    "Set successfully" if successful.
    '''
    class SetUserPaidExpense(AbstractAPI):
        def api_operation(self, user_id, conn):
            cursor = conn.cursor()
            expense_id = ""
            user = ""
            currentValue, newValue = -1, -1
            # Get headers
            try:
                expense_id = request.headers.get('expense_id')
                user = request.headers.get('user_id')
            except Exception as e:
                return jsonify(error=412, text="Missing expense_id"), 412
            # Check if user has permissions to toggle the expense
            try:
                expense_group_id = getExpenseGroup(expense_id, cursor)
                if not(isExpenseCreator(user_id, expense_group_id, cursor) or isModerator(user_id, expense_group_id, cursor)):
                    return jsonify(error=412, text="User must be member of the expense group to see an expense picture."), 412
            except Exception as e:
                return jsonify(error=412, text="Cannot determine if caller has permissions"), 412
            # Toggle paid value in accured expenses.
            query = """
            SELECT paid FROM accured_expenses
            WHERE user_id = ? AND expense_id = ?
            """

            try:
                cursor.execute(query, (user, expense_id))
                currentValue = cursor.fetchone()[0]
                newValue = not(currentValue)
            except Exception as e:
                return jsonify(error=412, text="Cannot get current status of accured expense."), 412

            query = f'''
            UPDATE accured_expenses
            SET paid = {str(newValue)}
            WHERE user_id = ? AND expense_id = ? '''
            try:
                cursor.execute(query, (user, expense_id))
            except Exception as e:
                return jsonify(error=412, text="Cannot set paid value to true"), 412
            conn.commit()
            return jsonify("Set successfully")
    return SetUserPaidExpense.template_method(SetUserPaidExpense, request.headers["api_key"] if "api_key" in request.headers else None)

def detect_document(image):
    """Detects document features in an image."""
    from google.cloud import vision
    import io
    client = vision.ImageAnnotatorClient()

    content = image

    image = vision.Image(content=content)

    response = client.document_text_detection(image=image)

    for page in response.full_text_annotation.pages:
        for block in page.blocks:
            print('\nBlock confidence: {}\n'.format(block.confidence))

            for paragraph in block.paragraphs:
                print('Paragraph confidence: {}'.format(
                    paragraph.confidence))

                for word in paragraph.words:
                    word_text = ''.join([
                        symbol.text for symbol in word.symbols
                    ])
                    print('Word text: {} (confidence: {})'.format(
                        word_text, word.confidence))

                    for symbol in word.symbols:
                        print('\tSymbol: {} (confidence: {})'.format(
                            symbol.text, symbol.confidence))

    if response.error.message:
        raise Exception(
            '{}\nFor more info on error messages, check: '
            'https://cloud.google.com/apis/design/errors'.format(
                response.error.message))
