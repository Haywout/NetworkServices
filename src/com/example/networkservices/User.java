package com.example.networkservices;

import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

public class User extends Observable{
	private String name;
	private String screenName;
	private String pictureURL;
	private Bitmap screenPicture;
	/**
	 * public construtor
	 * @param userObject json object met alle data van de user.
	 * @throws JSONException
	 */
	public User(JSONObject userObject) throws JSONException {
		name = userObject.getString("name");
		screenName = userObject.getString("screen_name");
		pictureURL =  userObject.getString("profile_image_url");
		
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
	
	
}
