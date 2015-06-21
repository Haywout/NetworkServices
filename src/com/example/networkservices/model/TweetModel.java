package com.example.networkservices.model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/*
 *  5 get requests:
 *  Tweet Search (get tweets/search)
 *  Home timeline (get tweets/home_timeline)
 *  User search	(get users/search)
 *  Logged in user profile (account verify_credentials)
 *  person user profile
 *  
 *  3 post requests:
 *  Tweet
 *  Retweet
 *  Follow
 *  
 */


public class TweetModel extends Observable implements Observer{
	private ArrayList<Tweet> searchedTweets = new ArrayList<Tweet>();
	private ArrayList<Tweet> timeline = new ArrayList<Tweet>();
	private ArrayList<User> searchedUsers = new ArrayList<User>();
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
	
	public void handleUserSearch(String jsonString){
		generateSearchedUserList(jsonString);
		setChanged();
		notifyObservers();
	}
	
	

	private void generateSearchedUserList(String jsonString) {
		searchedUsers.clear();
		try {
			
			JSONArray userJson = new JSONArray(jsonString);
			
			// doorloopt de array om zo alle tweets eruit te halen
			for (int i = 0; i < userJson.length(); i++) {
				addUser(new User(userJson.getJSONObject(i)));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	private void addUser(User user) {
		user.addObserver(this);
		searchedUsers.add(user);
		
	}
	
	public ArrayList<User> getSearchedUsers(){
		return searchedUsers;
	}

	/**
	 * Getter to get the arraylist with tweets
	 * @return the arraylist tweets
	 */
	public ArrayList<Tweet> getTweets() {
		return searchedTweets;
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
		searchedTweets.add(tweet);
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
	private void generateTimeLineList(String jsonString){
		
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
		searchedTweets.clear();
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
