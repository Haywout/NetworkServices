package com.example.networkservices.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class TweetModel extends Observable implements Observer{
	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	private String jsonString = "";
	
	
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
		generateTweetList();
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Getter to get the arraylist with tweets
	 * @return the arraylist tweets
	 */
	public ArrayList<Tweet> getTweets() {
		return tweets;
	}
	/**
	 * Adds the tweet to the tweet list
	 * @param tweet the tweet to be added
	 */
	public void addTweet(Tweet tweet){
		tweet.addObserver(this);
		tweets.add(tweet);
	}
	@Override
	public void update(Observable observable, Object data) {
		
		setChanged();
		notifyObservers();
		Log.d("hoi", "model");
		
	}
	
	public void generateTweetList(){
		tweets.clear();
		try {
			JSONObject zoekresultaat = new JSONObject(jsonString);
			JSONArray tweetsJson = zoekresultaat.getJSONArray("statuses");
			
			// doorloopt de array om zo alle tweets eruit te halen
			for (int i = 0; i < tweetsJson.length(); i++) {
				addTweet(new Tweet(tweetsJson.getJSONObject(i)));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
