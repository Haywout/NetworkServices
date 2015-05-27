package com.example.networkservices;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class Media extends Entities{
	private String mediaUrl = "";
	private String expanded_url = "";
	private Bitmap picture = null;
	public Media(JSONObject mediaObj) throws JSONException {		
		super(mediaObj.getJSONArray("indices"));
		
		mediaUrl = mediaObj.getString("media_url");
		expanded_url = mediaObj.getString("expanded_url");
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
