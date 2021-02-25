package com.dblappdev.app;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

public abstract class AbstractAPIRequest {
    String api_url = "http://localhost:5000/";
    String api_link;
    JSONArray response;
    AbstractAPIRequest(String api_link) {
        this.api_link = api_link;
    }
//    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, api_url+api_link, null, new Response.Listener<JSONArray>(){
//        @Override
//        public void onResponse(JSONArray response){
//
//        }
//    },
//            new Response.ErrorListener() {
//
//    }) {
//
//    }
}
