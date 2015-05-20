package model.example.networkservices;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.util.Log;

import com.example.networkservices.Tweet;

public class TweetModel extends Observable implements Observer{
	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	
	/**
	 * Getter to get the arraylist with tweets
	 * @return the arraylist tweets
	 */
	public ArrayList<Tweet> getTweets() {
		return tweets;
	}
	/**
	 * Adds the tweet to the tweet list
	 * @param tweet the tweet to be added
	 */
	public void addTweet(Tweet tweet){
		tweet.addObserver(this);
		tweets.add(tweet);
	}
	@Override
	public void update(Observable observable, Object data) {
		setChanged();
		notifyObservers();
		Log.d("hoi", "model");
		
	}
}
