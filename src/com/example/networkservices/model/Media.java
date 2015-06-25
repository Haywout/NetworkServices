package com.example.networkservices.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class Media extends Entities{
	private String mediaUrl = "";
	private Bitmap picture = null;
	public Media(JSONObject mediaObj) throws JSONException {		
		super(mediaObj.getJSONArray("indices"));
		
		mediaUrl = mediaObj.getString("media_url");
	}
	
	public String getMediaUrl() {
		return mediaUrl;
	}
	
	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}
	
	public Bitmap getPicture() {
		return picture;
	}
	
	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}
}
