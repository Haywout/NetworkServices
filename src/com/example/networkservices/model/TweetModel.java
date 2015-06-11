package com.example.networkservices.model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class TweetModel extends Observable implements Observer{
	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	private ArrayList<Tweet> timeline = new ArrayList<Tweet>();
	private User loggedInUser;
	
	
	public void handleTweetSearch(String jsonString) {
		generateTweetList(jsonString);
		setChanged();
		notifyObservers();
	}
	
	public void handleTimeLine(String jsonString){
		generateTimeLineList(jsonString);
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
	 * Getter to get the timeline
	 * @return the list with all tweets in the timeline
	 */
	public ArrayList<Tweet> getTimeLine(){
		return timeline;
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
	
	/**
	 * Generates the list with tweets that are on your timeline
	 * @param jsonString the string with json text to convert to tweets
	 */
	public void generateTimeLineList(String jsonString){
		
			timeline.clear();
			try {
				JSONArray zoekresultaat = new JSONArray(jsonString);
				JSONObject obj = zoekresultaat.getJSONObject(0);
				Log.d("timeline", obj.toString());
				
				// doorloopt de array om zo alle tweets eruit te halen
				for (int i = 0; i < zoekresultaat.length(); i++) {
					addTweetToTimeLine(new Tweet(zoekresultaat.getJSONObject(i)));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			
		}
	}
	
	/**
	 * adds tweets to the timeline list
	 * @param tweet
	 */
	private void addTweetToTimeLine(Tweet tweet) {
		tweet.addObserver(this);
		timeline.add(tweet);
		
	}
	
	/**
	 * Generates the list of tweets that you have searched for.
	 * @param jsonString the string with json text of your search result.
	 */
	private void generateTweetList(String jsonString){
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
	
	public void updateLoggedUser(String jsonString){
		JSONObject user;

		try {
			if (loggedInUser == null) {
				user = new JSONObject(jsonString);
				loggedInUser = new User(user);
			} else {
				user = new JSONObject(jsonString);
				loggedInUser.updateUser(user);
			}
		} catch (JSONException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public User getLoggedInUser() {
		return loggedInUser;
	}
	
}
