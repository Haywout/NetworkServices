package com.example.networkservices;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.networkservices.model.TweetModel;
import com.example.networkservices.model.User;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class UserDetailsActivity extends Activity {

	private TweetModel model;
	private User user;
	private TextView tvFollowersCount, tvFollowingCount, tvTweetsPostedCount, tvUsername, tvUserNickName;
	private ImageView ivUserImage;
	private CommonsHttpOAuthConsumer consumer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);

		TweetApplication app = (TweetApplication) getApplicationContext();
		model = app.getModel();
		consumer = app.getConsumer();
		
		
		updateCurrentLoggedUser();
		
		
		tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCountNumber);
		tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCountNumber);
		tvTweetsPostedCount = (TextView) findViewById(R.id.tvSendTweetsCountNumber);
		tvUsername = (TextView) findViewById(R.id.tvNameUserDetailsActivity);
		tvUserNickName = (TextView) findViewById(R.id.tvNickNameUserDetailsActivity);
		
		tvFollowersCount.setText("0");
		tvFollowingCount.setText("0");
		tvTweetsPostedCount.setText("0");
	}

	private void updateCurrentLoggedUser() {
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (OAuthExpectationFailedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (OAuthCommunicationException e1) {
				// TODO Auto-generated catch block
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
			updateScreenValues();
			
			super.onPostExecute(result);
		}

	}
	
	private void updateScreenValues(){
		tvFollowersCount.setText(model.getLoggedInUser().getFollowers()+ "");
		tvFollowingCount.setText(model.getLoggedInUser().getFollowing()+ "");
		tvTweetsPostedCount.setText(model.getLoggedInUser().getSend_tweets()+ "");
		tvUsername.setText(model.getLoggedInUser().getName());
		tvUserNickName.setText(model.getLoggedInUser().getScreenName());
	}

}
