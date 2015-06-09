package com.example.networkservices;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import com.example.networkservices.model.TweetModel;

import android.app.Application;

public class TweetApplication extends Application{
	public static final String CONSUMERKEY = "1Omry6axtR8CxwPTul52t4Ser";
	public static final String CONSUMERSECRET ="jLJezF5rQA15NlRUuuCj5Ra4znqIy25YmkwHj9QCuGc59J1K0G";
	public static final String OAUTH_REQUEST_URL ="https://api.twitter.com/oauth/request_token";
	public static final String OAUTH_ACCESSTOKEN_URL="https://api.twitter.com/oauth/access_token";
	public static final String OAUTH_AUTHORIZE_URL="https://api.twitter.com/oauth/authorize";
	public static final String OAUTH_CALLBACK_URL = "http://www.gijs.applefag.nl";
	 
	
	TweetModel model = new TweetModel();
	
	
	private CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider(OAUTH_REQUEST_URL, OAUTH_ACCESSTOKEN_URL, OAUTH_AUTHORIZE_URL);
	private CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMERKEY, CONSUMERSECRET);
	
	
	public TweetModel getModel() {
		return model;
	}
	
	public CommonsHttpOAuthProvider getProvider() {
		return provider;
	}
	
	public CommonsHttpOAuthConsumer getConsumer() {
		return consumer;
	}
	
	
	
}
	
