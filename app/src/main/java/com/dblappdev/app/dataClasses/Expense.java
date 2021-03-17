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

    /**
     * gets amount of expense instance
     * @return {@code this.amount}
     */
    public float getAmount() {
        return amount;
    }

    /**
     * gets identifier of expense group this expense is in
     * @return {@code this.expenseGroupId}
     */
    public int getExpenseGroupId() {
        return expenseGroupId;
    }

    /**
     * gets identifier of this expense instance
     * @return {@code this.id}
     */
    public int getId() {
        return id;
    }

    /**
     * gets description this expense instance
     * @return {@code this.description}
     */
    public String getDescription() {
        return description;
    }

    /**
     * gets title of this expense instance
     * @return {@code this.title}
     */
    public String getTitle() {
        return title;
    }

    /**
     * gets User object containing info of the person that submitted this expense
     * @return {@code this.creator}
     */
    public User getCreator() {
        return creator;
    }

    /**
     * sets description of this expense instance
     * @post {@code this.description == description}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * sets title of this expense instance
     * @post {@code this.title == title}
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * gets identifier of this expense instance
     * @post {@code this.amount == amount}
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }
}
