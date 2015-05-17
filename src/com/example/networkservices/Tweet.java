package com.example.networkservices;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Tweet {
	private String text;
	private String datum;
	private User user;
	private ArrayList<Entities> entities = new ArrayList<Entities>() ;
	
	/**
	 * Public constructor
	 * @param tweetObj een JSON object dat 
	 * @throws JSONException
	 */
	public Tweet(JSONObject tweetObj) throws JSONException {
		text = tweetObj.getString("text");
		datum = tweetObj.getString("created_at");
		user = new User(tweetObj.getJSONObject("user"));
		
		// leest de entities uit en haalt alle hashtags eruit
		JSONObject entitieObj = tweetObj.getJSONObject("entities");
		JSONArray hashtagsJSON = entitieObj.getJSONArray("hashtags");
		
		// maakt voor elke hashtag een nieuw hashtag object aan
		for (int i = 0; i < hashtagsJSON.length(); i++) {
			entities.add(new Hashtag(hashtagsJSON.getJSONObject(i)));
		}
		
		// haalt alle url entitieiten uit het entitie JSON object
		JSONArray urlsJSON = entitieObj.getJSONArray("urls");
		
		// maakt voor elke url een nieuw urlObject.
		for (int i = 0; i < urlsJSON.length(); i++) {
			entities.add(new Url(urlsJSON.getJSONObject(i)));
		}
		
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
	
	public ArrayList<Entities> getEntities() {
		return entities;
	}
}
