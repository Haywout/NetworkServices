package com.example.networkservices;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private String name;
	private String screenName;
	//private String pictureURL;
	/**
	 * public construtor
	 * @param userObject json object met alle data van de user.
	 * @throws JSONException
	 */
	public User(JSONObject userObject) throws JSONException {
		name = userObject.getString("name");
		screenName = userObject.getString("screen_name");
		
	}
	
	/**
	 * Getter for the name
	 * @return the name of the user
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Getter for the screenname
	 * @return the screenname
	 */
	public String getScreenName() {
		return screenName;
	}
}
