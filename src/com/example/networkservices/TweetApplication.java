package com.example.networkservices;

import com.example.networkservices.model.TweetModel;

import android.app.Application;

public class TweetApplication extends Application{
	TweetModel model = new TweetModel();
	
	public TweetModel getModel() {
		return model;
	}
}
