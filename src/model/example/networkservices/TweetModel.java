package model.example.networkservices;

import java.util.ArrayList;

import com.example.networkservices.Tweet;

public class TweetModel {
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
		tweets.add(tweet);
	}
}
