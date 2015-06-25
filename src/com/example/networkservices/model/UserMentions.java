package com.example.networkservices.model;


import org.json.JSONException;
import org.json.JSONObject;


public class UserMentions extends Entities {

	public UserMentions(JSONObject mentionsObj) throws JSONException {
		
		
		super(mentionsObj.getJSONArray("indices"));
		

	}

}
