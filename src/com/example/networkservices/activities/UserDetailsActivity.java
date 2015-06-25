package com.example.networkservices.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkservices.R;
import com.example.networkservices.TweetApplication;
import com.example.networkservices.model.Tweet;
import com.example.networkservices.model.TweetModel;
import com.example.networkservices.model.User;
import com.example.networkservices.view.Tweetadapter;

public class UserDetailsActivity extends Activity {

	private TweetModel model;
	private User user;
	private TextView tvFollowersCount, tvFollowingCount, tvTweetsPostedCount,
			tvUsername, tvUserNickName;
	private CommonsHttpOAuthConsumer consumer;
	private Button btnFollow;
	private ListView lvUserTimeLine;

	private ArrayList<Tweet> usertimeLinelist;
	private Tweetadapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);
		Intent intent = getIntent();

		int pos = intent.getIntExtra("position", 0);
		String type = intent.getStringExtra("IntentType");

		TweetApplication app = (TweetApplication) getApplicationContext();
		model = app.getModel();
		consumer = app.getConsumer();

		tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCountNumber);
		tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCountNumber);
		tvTweetsPostedCount = (TextView) findViewById(R.id.tvSendTweetsCountNumber);
		tvUsername = (TextView) findViewById(R.id.tvNameUserDetailsActivity);
		tvUserNickName = (TextView) findViewById(R.id.tvNickNameUserDetailsActivity);
		lvUserTimeLine = (ListView) findViewById(R.id.lvUserTimeLine);
		usertimeLinelist = new ArrayList<Tweet>();
		adapter = new Tweetadapter(this, usertimeLinelist);
		lvUserTimeLine.setAdapter(adapter);
		lvUserTimeLine.setVisibility(View.GONE);

		btnFollow = (Button) findViewById(R.id.btnFollow);
		btnFollow.setVisibility(View.GONE);
		btnFollow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FollowUserTask userTask = new FollowUserTask();
				userTask.execute(user.getUserId());

			}
		});

		if (type != null) {

			if (type.equals("search")) {
				user = model.getSearchedUsers().get(pos);
				updateScreenValues();
				btnFollow.setVisibility(View.VISIBLE);
				lvUserTimeLine.setVisibility(View.VISIBLE);

				getUserTimeLineTask userTimeLineTask = new getUserTimeLineTask();
				userTimeLineTask.execute(user.getUserId());
			}
		} else {
			getLoggedUserInfo();
		}

	}

	private void getLoggedUserInfo() {
		getLoggedUserInfoTask task = new getLoggedUserInfoTask();
		task.execute("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * With this task you can get the account details of the person thats logged
	 * in.
	 * 
	 * @author Casper
	 * 
	 */
	private class getLoggedUserInfoTask extends AsyncTask<String, Void, String> {

		private HttpResponse response;
		private String searchJSON;

		@Override
		protected String doInBackground(String... params) {

			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(
					"https://api.twitter.com/1.1/account/verify_credentials.json");
			try {
				consumer.sign(httpGet);
			} catch (OAuthMessageSignerException e1) {
				e1.printStackTrace();
			} catch (OAuthExpectationFailedException e1) {
				e1.printStackTrace();
			} catch (OAuthCommunicationException e1) {
				e1.printStackTrace();
			}

			try {
				ResponseHandler<String> handler = new BasicResponseHandler();
				response = client.execute(httpGet);
				searchJSON = handler.handleResponse(response);
			} catch (ClientProtocolException e) {
				int statusCode = response.getStatusLine().getStatusCode();
				return "" + statusCode;
			} catch (IOException e) {
				searchJSON = "Internet";
				// e.printStackTrace();
			} catch (Exception ec) {
				ec.printStackTrace();
			}

			return searchJSON;

		}

		@Override
		protected void onPostExecute(String result) {
			model.updateLoggedUser(result);
			user = model.getLoggedInUser();

			updateScreenValues();

			super.onPostExecute(result);
		}

	}

	/**
	 * With this task you can follow people with the id that comes in the
	 * params.
	 * 
	 * @author Casper
	 * 
	 */
	private class FollowUserTask extends AsyncTask<String, Void, String> {
		private HttpResponse response;

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			int statuscode = 0;
			HttpPost post = new HttpPost(
					"https://api.twitter.com/1.1/friendships/create.json?user_id="
							+ params[0] + "&follow=true");

			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("status", params[0]));
			try {
				post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {

				e1.printStackTrace();
			}

			try {
				consumer.sign(post);
			} catch (OAuthMessageSignerException e1) {

				e1.printStackTrace();
			} catch (OAuthExpectationFailedException e1) {

				e1.printStackTrace();
			} catch (OAuthCommunicationException e1) {

				e1.printStackTrace();
			}
			try {
				response = client.execute(post);
				statuscode = response.getStatusLine().getStatusCode();
			} catch (ClientProtocolException e) {
				statuscode = response.getStatusLine().getStatusCode();
				return "" + statuscode;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return statuscode + "";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				if (result.equals("403")) {
					Toast.makeText(getBaseContext(),
							"You are already friends with this person",
							Toast.LENGTH_LONG).show();
				} else if (result.equals("200")) {
					Toast.makeText(
							getBaseContext(),
							"You are now Succesfully following: "
									+ user.getName(), Toast.LENGTH_LONG).show();
				}
			}

			Log.d("statuscode", "" + response.getStatusLine().getStatusCode());

			super.onPostExecute(result);
		}

	}

	/**
	 * Updates the components on the screen
	 */
	private void updateScreenValues() {
		tvFollowersCount.setText(user.getFollowers() + "");
		tvFollowingCount.setText(user.getFollowing() + "");
		tvTweetsPostedCount.setText(user.getSend_tweets() + "");
		tvUsername.setText(user.getName());
		tvUserNickName.setText(user.getScreenName());
	}

	/**
	 * Gets the JSON from twitter that contains the usertimeline of the id that
	 * has been send with it.
	 * 
	 * @author Casper
	 * 
	 */
	private class getUserTimeLineTask extends AsyncTask<String, Void, String> {

		private HttpResponse response;
		private String searchJSON;

		@Override
		protected String doInBackground(String... params) {

			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(
					"https://api.twitter.com/1.1/statuses/user_timeline.json?user_id="
							+ params[0] + "&count=5");
			try {
				consumer.sign(httpGet);
			} catch (OAuthMessageSignerException e1) {
				e1.printStackTrace();
			} catch (OAuthExpectationFailedException e1) {
				e1.printStackTrace();
			} catch (OAuthCommunicationException e1) {
				e1.printStackTrace();
			}

			try {
				ResponseHandler<String> handler = new BasicResponseHandler();
				response = client.execute(httpGet);
				searchJSON = handler.handleResponse(response);
			} catch (ClientProtocolException e) {
				int statusCode = response.getStatusLine().getStatusCode();
				return "" + statusCode;
			} catch (IOException e) {
				searchJSON = "Internet";
				// e.printStackTrace();
			} catch (Exception ec) {
				ec.printStackTrace();
			}

			return searchJSON;

		}

		@Override
		protected void onPostExecute(String result) {
			handleResult(result);
			Log.d("Timelineresult", result);
			super.onPostExecute(result);
		}

	}

	private void handleResult(String result) {
		// creates the user time line
		try {
			JSONArray usertimeline = new JSONArray(result);

			for (int i = 0; i < usertimeline.length(); i++) {
				Tweet tweet = new Tweet(usertimeline.getJSONObject(i));
				tweet.addObserver(adapter);
				usertimeLinelist.add(tweet);

			}

			adapter.notifyDataSetChanged();

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
