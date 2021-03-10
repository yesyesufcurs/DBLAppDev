from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_script import Manager
from flask_migrate import Migrate, MigrateCommand

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///app.db'

db = SQLAlchemy(app)
migrate = Migrate(app, db)

manager = Manager(app)
manager.add_command('db', MigrateCommand)

#### Database Models #####
# Primary Keys are called ID for uniformity.


class User(db.Model):
    id = db.Column(db.String(30), primary_key=True)     # = Username
    password = db.Column(db.String(64), nullable=False)  # = Password
    email = db.Column(db.String(255), nullable=False)   # = Email Address
    api_key = db.Column(db.String(64), nullable=False)  # = ApiKey


class ExpenseGroup(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(30), nullable=False)
    moderator_id = db.Column(db.String(30), db.ForeignKey('user.id'))


class Expense(db.Model):
    id = db.Column(db.Integer, primary_key=True)        # = Expense ID
    user_id = db.Column(db.String(30), db.ForeignKey(
        'user.id'))                                     # = Username of creator
    title = db.Column(db.String(100), nullable=False)   # = Title
    amount = db.Column(db.Float, nullable=False)        # = Expense Amount
    picture = db.Column(db.LargeBinary, nullable=True)  # = Picture
    # Content of picture found by OCR
    content = db.Column(db.Text, nullable=True)
    # Expense group the expense belongs to
    expense_group_id = db.Column(db.Integer, db.ForeignKey(
        'expense_group.id'))

# Contains the amount each person owes for some expense


class AccuredExpenses(db.Model):
    expense_id = db.Column(db.Integer, db.ForeignKey(
        'expense.id'), primary_key=True)
    user_id = db.Column(db.String(30), db.ForeignKey(
        'user.id'), primary_key=True)
    # Amount ExpenseUser.user_id owes to ExpenseUser.expense_id.user_id
    amount = db.Column(db.Float, nullable=False)
    # True if paid back, else false.
    paid = db.Column(db.Boolean, nullable=False)


class ExpenseGroupMembers(db.Model):
    expense_group_id = db.Column(db.Integer, db.ForeignKey(
        'expense_group.id'), primary_key=True)
    user_id = db.Column(db.String(30), db.ForeignKey(
        'user.id'), primary_key=True)


if __name__ == '__main__':
    manager.run()
