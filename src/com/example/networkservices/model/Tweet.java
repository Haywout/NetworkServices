package com.example.networkservices.model;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class Tweet extends Observable implements Observer {
	private String text;
	private String datum;
	private User user;
	private ArrayList<Entities> entities = new ArrayList<Entities>() ;
	private String tweetId;
	
	/**
	 * Public constructor
	 * @param tweetObj een JSON object dat 
	 * @throws JSONException
	 */
	public Tweet(JSONObject tweetObj) throws JSONException {
		text = tweetObj.getString("text");
		datum = tweetObj.getString("created_at");
		user = new User(tweetObj.getJSONObject("user"));
		tweetId = tweetObj.getString("id_str");
		user.addObserver(this);
		
		// leest de entities uit 
		JSONObject entitieObj = tweetObj.getJSONObject("entities");
		
		// haalt alle hashtags uit het entitie object
		JSONArray hashtagsJSON = null;
		if (entitieObj.has("hashtags")) {
			hashtagsJSON = entitieObj.getJSONArray("hashtags");
		}
		
		// haalt alle url entitieiten uit het entitie JSON object
		JSONArray urlsJSON = null;
		if (entitieObj.has("urls")) {
			urlsJSON = entitieObj.getJSONArray("urls");
		}
		// haalt alle usermentions uit het entitie JSON object
		JSONArray mentions = null;
		if (entitieObj.has("user_mentions")) {
			mentions = entitieObj.getJSONArray("user_mentions");
		}
		// haalt alle afbeeldingen uit het entitie JSON object
		JSONArray media = null;
		if (entitieObj.has("media")) {
			media = entitieObj.getJSONArray("media");
		}
		
		// maakt voor elke hashtag een nieuw hashtag object aan
		for (int i = 0; i < hashtagsJSON.length(); i++) {
			entities.add(new Hashtag(hashtagsJSON.getJSONObject(i)));
		}
		// maakt voor elke url een nieuw urlObject.
		for (int i = 0; i < urlsJSON.length(); i++) {
			entities.add(new Url(urlsJSON.getJSONObject(i)));
		}
		// maakt voor elke usermention een nieuw usermention object
		for (int i = 0; i < mentions.length(); i++) {
			entities.add(new UserMentions(mentions.getJSONObject(i)));
		}
		
		// maakt voor elke hashtag een nieuw hashtag object aan
		if (media != null) {
			for (int i = 0; i < media.length(); i++) {
				entities.add(new Media(media.getJSONObject(i)));
			}
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
	 * Formats the date string first into a date object.
	 * @return the date when the tweet has sended
	 */
	public String getDatum() {
		SimpleDateFormat readFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
		SimpleDateFormat writeFormat = new SimpleDateFormat("dd MMMM yyyy  HH:mm", Locale.getDefault());
		Date date = null;
		try {
			date = readFormat.parse(datum);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String formattedDate = "";
		if (date != null) {
			formattedDate = writeFormat.format(date);
		}
		
		Log.d("datum", formattedDate);
		return formattedDate;
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

	@Override
	public void update(Observable observable, Object data) {
		setChanged();
		notifyObservers();
		Log.d("hoi", "Tweet");
		
	}
	
	public String getTweetId() {
		return tweetId;
	}
}
