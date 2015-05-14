package com.example.networkservices;

import model.example.networkservices.TweetModel;
import android.app.Application;

public class TweetApplication extends Application{
	TweetModel model = new TweetModel();
	
	public TweetModel getModel() {
		return model;
	}
}
