package com.dblappdev.app.dataClasses;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class ExpenseGroup {
    private int id;
    private String title;
    private ArrayList<User> users;
    private User moderator;
    private ArrayList<Expense> expenses;
    private HashMap<User, Float> balance;

    public ExpenseGroup(int id, String title, User moderator) {
        this.id = id;
        this.title = title;
        this.moderator = moderator;
        this.users = new ArrayList<User>();;
    }

    /**
     * adds users to the user list of the expense group
     * @post {@code \forall user; users.contains(user); this.users.contains(user) == true}
     */
    public void addUsers(List<User> users) {
        for (User user : users) {
            addUser(user);
        }
    }

    /**
     * adds a user to the user list of the expense group
     * @post {@code this.users.contains(user) == true}
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * gets the user list of the expense group
     * @return {@code this.users}
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * adds expenses to the expense list of the expense group
     * @post {@code \forall expense; expenses.contains(expense); this.expenses.contains(expense) == true}
     */
    public void setExpenses(List<Expense> expenses) {
        for (Expense expense : expenses) {
            addExpense(expense);
        }
    }

    /**
     * adds expense to the expense list of the expense group
     * @post {@code this.expenses.contains(expense) == true}
     */
    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    /**
     * gets the expense list of the expense group
     * @return {@code this.expenses}
     */
    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    /**
     * sets the balance map of the expense group
     * @post {@code this.balance == balance}
     */
    public void setBalance(HashMap<User, Float> balance) {
        this.balance = balance;
    }

    /**
     * sets the balance of a single user
     * @post {@code this.balance.get(user) == amount}
     */
    public void setSingleBalance(User user, float amount) {
        balance.put(user, amount);
    }

    /**
     * gets the balance map of the expense group
     * @return {@code this.balance}
     */
    public HashMap<User, Float> getBalance() {
        return balance;
    }
}
