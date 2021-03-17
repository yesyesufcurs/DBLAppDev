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

    public void setUsers(List<User> users) {
        for (User user : users) {
            addUser(user);
        }
    }

    public void addUser(User user) {
        users.add(user);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setExpenses(List<Expense> expenses) {
        for (Expense expense : expenses) {
            addExpense(expense);
        }
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public void setBalance(HashMap<User, Float> balance) {
        this.balance = balance;
    }

    public void setSingleBalance(User user, float amount) {
        balance.put(user, amount);
    }

    public HashMap<User, Float> getBalance() {
        return balance;
    }
}
