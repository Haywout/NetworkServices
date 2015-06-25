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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.networkservices.R;
import com.example.networkservices.TweetApplication;
import com.example.networkservices.model.TweetModel;
import com.example.networkservices.view.Tweetadapter;

public class HomeActivity extends Activity {
	private TweetModel model;
	private CommonsHttpOAuthConsumer consumer;
	private Button btnSendTweet;
	private EditText etMakeTweetText;
	private LinearLayout llMakeTweet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ListView timeLineView = (ListView) findViewById(R.id.lvTimeline);
		TweetApplication app = (TweetApplication) getApplicationContext();
		consumer = app.getConsumer();
		etMakeTweetText = (EditText) findViewById(R.id.etTweettext);
		// if there is no text in the edittext, disable the button. Else enable
		// the button
		etMakeTweetText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (etMakeTweetText.length() > 0) {
					btnSendTweet.setEnabled(true);
				} else {
					btnSendTweet.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		btnSendTweet = (Button) findViewById(R.id.btnSendTweet);
		btnSendTweet.setEnabled(false);
		btnSendTweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// get the text thats in the edittext
				String tweetText = etMakeTweetText.getText() + "";
				Log.d("Sendtweet clicked", "Clicked");
				etMakeTweetText.setText("");

				// send the tweet in the asynctask.
				postTweetTask postTask = new postTweetTask();
				postTask.execute(tweetText);

			}
		});

		llMakeTweet = (LinearLayout) findViewById(R.id.llmakeTweet);
		llMakeTweet.setVisibility(View.GONE);

		model = app.getModel();
		updateTimeLine();

		Tweetadapter tweetAdapter = new Tweetadapter(this, model.getTimeLine());
		model.addObserver(tweetAdapter);
		timeLineView.setAdapter(tweetAdapter);

	}

	private void updateTimeLine() {
		getTimeLineTask task = new getTimeLineTask();
		task.execute("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_search) {
			startActivity(new Intent(this, SearchActivity.class));
		} else if (id == R.id.action_logout) {
			TweetApplication app = (TweetApplication) getApplicationContext();
			CommonsHttpOAuthConsumer consumer = app.getConsumer();
			consumer.setTokenWithSecret(null, null);

			SharedPreferences prefs = getSharedPreferences(
					LoginActivity.TOKENS, 0);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(LoginActivity.TOKEN, null);
			editor.putString(LoginActivity.TOKENSECRET, null);
			editor.commit();
			Intent returnToLogin = new Intent(this, LoginActivity.class);
			startActivity(returnToLogin);
			finish();
		} else if (id == R.id.action_refresh) {
			updateTimeLine();
		} else if (id == R.id.action_createtweet) {
			if (llMakeTweet.getVisibility() == View.VISIBLE) {
				llMakeTweet.setVisibility(View.GONE);
			} else {
				llMakeTweet.setVisibility(View.VISIBLE);
			}
		} else if (id == R.id.action_userProfile) {
			Intent goToUserProfile = new Intent(this, UserDetailsActivity.class);
			startActivity(goToUserProfile);

		}
		return super.onOptionsItemSelected(item);
	}

	private class getTimeLineTask extends AsyncTask<String, Void, String> {
		private HttpResponse response;
		private String searchJSON;

		@Override
		protected String doInBackground(String... params) {

			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(
					"https://api.twitter.com/1.1/statuses/home_timeline.json");
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
				// als er iets is fout gegaan met de request sturen (oftewel
				// statuscode is geen 200)
				int statusCode = response.getStatusLine().getStatusCode();
				return "" + statusCode;
			} catch (IOException e) {
				// als je geen internet hebt.
				searchJSON = "Internet";
			} catch (Exception ec) {
				ec.printStackTrace();
			}

			return searchJSON;

		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("timelineresult", result);
			if (result.equals("Internet")) {
			} else if (!result.equals("")) {
				model.handleTimeLine(result);
			}

			super.onPostExecute(result);
		}

	}

	private class postTweetTask extends AsyncTask<String, Void, String> {
		private HttpResponse response;

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost(
					"https://api.twitter.com/1.1/statuses/update.json");

			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("status", params[0]));
			try {
				post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
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
			} catch (ClientProtocolException e) {
				// foute statuscode (dus geen 200)
				int statusCode = response.getStatusLine().getStatusCode();
				return "" + statusCode;
			} catch (IOException e) {
				// geen internet
				return "Internet";
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				if (result.equals("403")) {
					Toast.makeText(getBaseContext(),
							"You can't post 2 of the same tweets",
							Toast.LENGTH_LONG).show();
				} else if (result.equals("Internet")) {
					Toast.makeText(getApplicationContext(),
							"Je hebt geen internet", Toast.LENGTH_SHORT).show();
				} else {
					updateTimeLine();
					Toast.makeText(getBaseContext(), "Tweet has been sended",
							Toast.LENGTH_SHORT).show();
				}
			}

			Log.d("statuscode", "" + response.getStatusLine().getStatusCode());

			updateTimeLine();
			super.onPostExecute(result);
		}

	}
}
