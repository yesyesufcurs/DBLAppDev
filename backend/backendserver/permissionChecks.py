def number_expense_group_members(expense_group_id, cursor):
    '''
    Returns number of expense group members in a expense group
    '''
    cursor.execute(
        "SELECT * FROM expense_group_members WHERE expense_group_id = ?", (expense_group_id,))
    return len(cursor.fetchall())


def isModerator(user_id, expense_group_id, cursor):
    '''
    Returns if user is moderator of expense group
    '''
    query = "SELECT id FROM expense_group WHERE id = ? AND moderator_id = ?"
    cursor.execute(query, (expense_group_id, user_id))
    return len(cursor.fetchall()) == 1

def isMember(user_id, expense_group_id, cursor):
    '''
    Returns if user is in the expense group
    '''
    query = "SELECT * FROM expense_group_members WHERE expense_group_id = ? AND user_id = ?"
    cursor.execute(query, (expense_group_id, user_id))
    return len(cursor.fetchall()) == 1

def isExpenseCreator(user_id, expense_id, cursor):
    '''
    Returns if user has created the expense
    '''
    query = "SELECT id FROM expense WHERE id = ? AND user_id = ?"
    cursor.execute(query, (expense_id, user_id))
    return len(cursor.fetchall()) == 1

def getExpenseGroup(expense_id, cursor):
    '''
    Returns the expense_group_id given an expense_id
    '''
    query = "SELECT expense_group_id FROM expense WHERE id = ?"
    cursor.execute(query, (expense_id,))
    return cursor.fetchone()[0]