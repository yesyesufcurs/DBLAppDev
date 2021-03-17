package com.dblappdev.app.dataClasses;

public class Expense {
    //Set on instance creation, after that only Get
    private int id;
    private int expenseGroupId;
    private User creator;

    //Set and Get at any time
    private float amount;
    private String title;
    private String description;

    public Expense(int id, int expenseGroupId, User creator, float amount, String title, String description) {
        this.id = id;
        this.expenseGroupId = expenseGroupId;
        this.creator = creator;
        this.amount = amount;
        this.title = title;
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public int getExpenseGroupId() {
        return expenseGroupId;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public User getCreator() {
        return creator;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
