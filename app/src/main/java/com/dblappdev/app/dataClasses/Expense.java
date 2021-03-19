package com.dblappdev.app.dataClasses;

import java.util.HashMap;

public class Expense {
    //Set on instance creation, after that only Get
    private int id;
    private int expenseGroupId;
    private User creator;

    //Set and Get at any time
    private float amount;
    private String title;
    private String description;
    private HashMap<User, Integer> distribution;

    public Expense(int id, int expenseGroupId, User creator, float amount, String title, String description, HashMap<User, Integer> distribution) {
        this.id = id;
        this.expenseGroupId = expenseGroupId;
        this.creator = creator;
        this.amount = amount;
        this.title = title;
        this.description = description;
        this.distribution = distribution;
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
     * sets the distribution map of the expense
     * @post {@code this.distribution == distribution}
     */
    public void setDistribution(HashMap<User, Integer> distribution) {
        this.distribution = distribution;
    }

    /**
     * sets the distribution of a single user
     * @post {@code this.distribution.get(user) == amount}
     */
    public void setSingleDistribution(User user, int amount) {
        distribution.put(user, amount);
    }

    /**
     * gets the distribution map of the expense
     * @return {@code this.distribution}
     */
    public HashMap<User, Integer> getDistribution() { return distribution; }

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
