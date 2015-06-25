package com.example.networkservices;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.app.Application;

import com.example.networkservices.model.TweetModel;

public class TweetApplication extends Application {
	// some final strings that are needed in the whole application.
	public static final String CONSUMERKEY = "1Omry6axtR8CxwPTul52t4Ser";
	public static final String CONSUMERSECRET = "jLJezF5rQA15NlRUuuCj5Ra4znqIy25YmkwHj9QCuGc59J1K0G";
	public static final String OAUTH_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String OAUTH_ACCESSTOKEN_URL = "https://api.twitter.com/oauth/access_token";
	public static final String OAUTH_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
	public static final String OAUTH_CALLBACK_URL = "http://www.gijs.applefag.nl";

	TweetModel model = new TweetModel();
	
	// 2 signpost objects that handle oauth procedures
	private CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider(
			OAUTH_REQUEST_URL, OAUTH_ACCESSTOKEN_URL, OAUTH_AUTHORIZE_URL);
	private CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(
			CONSUMERKEY, CONSUMERSECRET);
	
	/**
	 * Getter to obtain the model object
	 */
	public TweetModel getModel() {
		return model;
	}
	
	/**
	 * Getter to obtain the provider object
	 */
	public CommonsHttpOAuthProvider getProvider() {
		return provider;
	}
	/**
	 * Getter to obtain the consumer object
	 */
	public CommonsHttpOAuthConsumer getConsumer() {
		return consumer;
	}

}
