//package com.dblappdev.app.api;
//
//import android.graphics.Bitmap;
//
//import com.android.volley.VolleyError;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.List;
//import java.util.Map;
//
//public class APIExample {
//    /**
//     * Example 1:
//     * <p>
//     * Logging in
//     */
//    void APIExample1() {
//        APIService.login("pietje", "password", this,
//                new APIResponse<String>() {
//                    @Override
//                    public void onResponse(String data) {
//                        // This method is called when the request was successful
//                        // The data is specified in the contract. In this case data == apiKey
//                        // Specify what to do with the apiKey
//                        saveAPIKeySomewhere(data);
//                        doSomeUIStuff();
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error, String errorMessage) {
//                        // This method is called when the request was unsuccessful
//                        // VolleyError contains the error that is returned
//                        // ErrorMessage contains the message returned by the backend
//                        // Do something with the error or errorMessage like creating a toast
//                        createToast(errorMessage);
//                    }
//                });
//    }
//
//    /**
//     * Example 2:
//     * <p>
//     * Creating an expense group
//     */
//    void APIExample2() {
//        APIService.createExpenseGroup(User.getInstance().getAPIKey(), "New Expense group",
//                this, new APIResponse<String>() {
//                    @Override
//                    public void onResponse(String data) {
//                        // The data here is the ID of the new expenseGroup.
//                        // Specify what to do with the ID of the expense group.
//                        saveExpenseGroupId(data);
//                        doSomeUIStuff();
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error, String errorMessage) {
//                        // This method is called when the request was unsuccessful
//                        // VolleyError contains the error that is returned
//                        // ErrorMessage contains the message returned by the backend
//                        // Do something with the error or errorMessage like creating a toast
//                        createToast(errorMessage);
//                    }
//                });
//    }
//
//    /**
//     * Example 3:
//     * <p>
//     * Creating an expense
//     * <p>
//     * Creating an expense consists of multiple stages.
//     * Stage 1: Creating the expense entry
//     * Stage 2: Creating the IOU (I owe you) of the expense, this is the entry that contains how much each person owes the creator of the expense.
//     */
//    void Stage1() {
//        // Creates the expense entry
//        Bitmap picture = null; // This would be the picture of the receipt made by the user (can be null in the case there is no picture)
//        APIService.createExpense(User.getInstance().getAPIKey(), User.getInstance().getUser(),
//                "My new expense", "10.50", picture, "Eggs, toast, butter, milk",
//                "548632", this, new APIResponse<String>() {
//                    @Override
//                    public void onResponse(String data) {
//                        // data contains the expense id, save it somewhere, you will need it
//                        save(data);
//                        Stage2();
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error, String errorMessage) {
//                        // This method is called when the request was unsuccessful
//                        createToast(errorMessage);
//                    }
//                });
//    }
//
//    void Stage2() {
//        // Creates the entry that contains how much each person owes the creator of the expense.
//        JSONObject iouJson = new JSONObject();
//        // Intialize the iouJson. In this object the usernames are the keys and the amount they owe are the values
//        try {
//            // In this case user1 owes €3.50 and user2 owes €4.00
//            iouJson.put("user1", "3.5");
//            iouJson.put("user2", "4");
//        } catch (JSONException e) {
//            // This exception must be handled because reasons
//        }
//
//        APIService.createExpenseIOU(User.getInstance().getAPIKey(), "2", iouJson, this,
//                new APIResponse<String>() {
//                    @Override
//                    public void onResponse(String data) {
//                        // In this call the backend does not return any meaningful information.
//                        // The data object will not contain any meaningful information, so you can ignore it.
//                        // This method can now be used to make changes in the frontend, as now the call is succeful.
//                        doUIStuffOnSuccessfulExpenseAdded();
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error, String errorMessage) {
//                        // This method is called when the request was unsuccessful
//                        createToast(errorMessage);
//                    }
//                });
//
//    }
//
//    /**
//     * Example 4:
//     * <p>
//     * Getting the expenses in an expense group.
//     * <p>
//     * This call is special as it does not return a string but a List<Map<String, String>> Object
//     */
//    void APIExample4() {
//        // First it is important to read the contract. The contract states the following:
//        // 'Each entry contains: id, title, amount, content, expense_group_id, user_id.'
//        // These are the keys you will have to retrieve to get the information!
//
//        APIService.getExpenseGroupExpenses(User.getInstance().getAPIKey(), "456985",
//                this, new APIResponse<List<Map<String, String>>>() {
//            @Override
//            public void onResponse(List<Map<String, String>> data) {
//                // The data in this case represents all expense details of an expense group.
//                // You can choose to create a parser for this data, or parse it here (A parser gives better testability)
//                parse(data);
//
//                // You can also iterate through the List:
//                for (Map<String, String> expense : data) {
//                    expense.get("id"); // This is the expense id
//                    expense.get("title"); // This is the expense title
//                    expense.get("amount"); // This is the expense amount
//                    expense.get("content"); // This is the expense content
//                    expense.get("expense_group_id"); // This is the expense_group_id
//                    expense.get("user_id"); // This is the expense user_id, i.e. the user that made the expense.
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error, String errorMessage) {
//                // This method is called when the request was unsuccessful
//                createToast(errorMessage);
//            }
//        });
//    }
//}
