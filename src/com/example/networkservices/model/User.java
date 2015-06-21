package com.example.networkservices.model;

import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

public class User extends Observable{
	private String name;
	private String screenName;
	private String pictureURL;
	
	private int followers;
	private int send_tweets;
	private int following;
	
	private Bitmap screenPicture;
	private String user_id;
	/**
	 * public construtor
	 * @param userObject json object met alle data van de user.
	 * @throws JSONException
	 */
	public User(JSONObject userObject) throws JSONException {
		name = userObject.getString("name");
		screenName = userObject.getString("screen_name");
		pictureURL =  userObject.getString("profile_image_url");
		followers = userObject.getInt("followers_count");
		following = userObject.getInt("friends_count");
		send_tweets = userObject.getInt("statuses_count");
		user_id = userObject.getString("id_str");
		
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
	
	public String getPictureURL() {
		return pictureURL;
	}
	
	public void setUserPicture(Bitmap result) {
		screenPicture = result;
		setChanged();
		notifyObservers();
		Log.d("hoi", "User");
	}
	
	public Bitmap getScreenPicture() {
		return screenPicture;
	}
	
	public int getFollowers() {
		return followers;
	}
	
	public int getFollowing() {
		return following;
	}
	
	public int getSend_tweets() {
		return send_tweets;
	}
	
	public String getUserId() {
		return user_id;
	}
	
	public void updateUser(JSONObject userObject) throws JSONException{
		name = userObject.getString("name");
		screenName = userObject.getString("screen_name");
		pictureURL =  userObject.getString("profile_image_url");
		followers = userObject.getInt("followers_count");
		following = userObject.getInt("following");
		send_tweets = userObject.getInt("statuses_count");
		
		setChanged();
		notifyObservers();
	}
	
	
}
