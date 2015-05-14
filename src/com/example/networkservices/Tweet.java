package com.example.networkservices;
import org.json.JSONException;
import org.json.JSONObject;


public class Tweet {
	private String text;
	private String datum;
	private User user;
	
	/**
	 * Public constructor
	 * @param tweetObj een JSON object dat 
	 * @throws JSONException
	 */
	public Tweet(JSONObject tweetObj) throws JSONException {
		text = tweetObj.getString("text");
		datum = tweetObj.getString("created_at");
		user = new User(tweetObj.getJSONObject("user"));
	}
	
	/**
	 * Getter for the text that has been tweeted
	 * @return the text of the tweet
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Getter for the datum that the tweet has been send
	 * @return the date when the tweet has sended
	 */
	public String getDatum() {
		return datum;
	}
	
	/**
	 * Returns the user that has send the tweet
	 * @return the user that sended the tweet
	 */
	public User getUser() {
		return user;
	}
}
