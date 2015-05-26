package com.example.networkservices;

import org.json.JSONException;
import org.json.JSONObject;

public class Media extends Entities{
	private String mediaUrl = "";
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
	
}
