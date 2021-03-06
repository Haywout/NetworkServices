package com.example.networkservices.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkservices.R;
import com.example.networkservices.TweetApplication;
import com.example.networkservices.model.TweetModel;
import com.example.networkservices.view.Tweetadapter;
import com.example.networkservices.view.UserAdapter;

public class SearchActivity extends Activity {
	private static final String APIKEY = "1Omry6axtR8CxwPTul52t4Ser";
	private static final String APISECRET = "jLJezF5rQA15NlRUuuCj5Ra4znqIy25YmkwHj9QCuGc59J1K0G";
	private static String bearerToken = "";

	private TweetModel model;

	private ListView lvList;
	private Button btnSearch;
	private EditText etSearch;
	private TextView searchHint;
	private String type;

	private CommonsHttpOAuthConsumer consumer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getActionBar().setHomeButtonEnabled(true);

		// generateOAUTHToken();

		// Get the model
		TweetApplication app = (TweetApplication) getBaseContext()
				.getApplicationContext();
		model = app.getModel();

		consumer = app.getConsumer();

		// haal de componenten op
		lvList = (ListView) findViewById(R.id.lvTweet);
		searchHint = (TextView) findViewById(R.id.tvSearch_Hint);
		btnSearch = (Button) findViewById(R.id.btnSearch);

		etSearch = (EditText) findViewById(R.id.etSearchText);

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (type.equals("user")) {
					seeDetails(position);
				}

			}

		});
		btnSearch.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				searchHint.setVisibility(View.GONE);
				if ((etSearch.getText() + "").startsWith("@")) {
					// search for user 
					type = "user";
					Log.d("type", type);
					UserAdapter userAdapter = new UserAdapter(
							SearchActivity.this, model.getSearchedUsers());
					model.addObserver(userAdapter);
					lvList.setAdapter(userAdapter);
					GetUsersTask getUsers = new GetUsersTask();
					String searchString = etSearch.getText() + "";
					btnSearch.setText("Searching ...");
					btnSearch.setEnabled(false);
					getUsers.execute(URLEncoder.encode(searchString));
					Log.d("Search", "button pressed");
				} else {
					// search for tweets

					Tweetadapter tweetAdapter = new Tweetadapter(
							SearchActivity.this, model.getTweets());
					model.addObserver(tweetAdapter);
					lvList.setAdapter(tweetAdapter);
					GetTweetsTask getTweets = new GetTweetsTask();
					String searchString = etSearch.getText() + "";
					btnSearch.setText("Searching ...");
					btnSearch.setEnabled(false);
					getTweets.execute(URLEncoder.encode(searchString));
					Log.d("Search", "button pressed");
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}

		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Loads the detailscreen for the person at a specific position in the list
	 * @param position the position that was clicked on.
	 */
	private void seeDetails(int position) {
		Intent userDetailsIntent = new Intent(this, UserDetailsActivity.class);
		userDetailsIntent.putExtra("position", position);
		userDetailsIntent.putExtra("IntentType", "search");
		startActivity(userDetailsIntent);
	}

	/**
	 * 
	 * Wordt niet meer gebruikt.
	 * 
	 * Genereert eerst de authenticatie string. Om met deze vervolgens de bearer
	 * token op te halen.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void generateOAUTHToken() {

		String authString = APIKEY + ":" + APISECRET;
		String base64 = Base64.encodeToString(authString.getBytes(),
				Base64.NO_WRAP);

		GenerateTokenTask task = new GenerateTokenTask();
		task.execute(base64);

	}

	/**
	 * Wordt niet meer gebruikt. Er wordt nu gebruik gemaakt van de consumer
	 * tokens.
	 * 
	 * Asynctask om de Bearer token aan te vragen(als er nog geen bearer token
	 * is aangevraagd met die AutenticatieString) Of om de bearer token op te
	 * halen van de twitter api, als die al wel een keer is aangevraagd.
	 * 
	 * 
	 */
	public class GenerateTokenTask extends AsyncTask<String, Void, String> {
		private HttpResponse response;

		@Override
		protected String doInBackground(String... params) {
			HttpPost request = new HttpPost(
					"https://api.twitter.com/oauth2/token");
			request.setHeader("Authorization", "Basic " + params[0]);
			request.setHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			String token = "";
			try {
				request.setEntity(new StringEntity(
						"grant_type=client_credentials"));
				HttpClient client = new DefaultHttpClient();
				response = client.execute(request);

				String responseString = new BasicResponseHandler()
						.handleResponse(response);

				JSONObject jsonO = new JSONObject(responseString);
				token = jsonO.getString("access_token");
			} catch (ClientProtocolException cpE) {
//				int statuscode = response.getStatusLine().getStatusCode();
				cpE.printStackTrace();
			} catch (UnsupportedEncodingException ueE) {
				// niet van belang om er iets op uit te voeren
			} catch (IOException ioE) {
				// geen internet
			} catch (JSONException jE) {
				// something goes wrong when making it a json object
			}

			return token;
		}

		@Override
		protected void onPostExecute(String result) {
			bearerToken = result;
			// Enable search button so that users can search
			if (!bearerToken.equals("")) {
				btnSearch.setEnabled(true);
			} else {
				Toast.makeText(
						getBaseContext(),
						"Something went wrong with connecting to the twitter server",
						Toast.LENGTH_LONG).show();
			}

			super.onPostExecute(result);
		}

	}

	/**
	 * Een asynctask dat de tweets ophaalt met de gevraagde zoekterm. Gebruikt
	 * hiervoor de user om bij twitter de zoekterm op te kunnen vragen.
	 * 
	 */
	public class GetTweetsTask extends AsyncTask<String, Integer, String> {
		private HttpResponse response;

		@Override
		protected String doInBackground(String... params) {
			String searchJSON = "";
			if (!params[0].equals("")) {

				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"https://api.twitter.com/1.1/search/tweets.json?q="
								+ params[0]);

				try {
					consumer.sign(httpGet);

					ResponseHandler<String> handler = new BasicResponseHandler();
					response = client.execute(httpGet);
					searchJSON = handler.handleResponse(response);
				} catch (ClientProtocolException e) {
//					int statusCode = response.getStatusLine().getStatusCode();
					e.printStackTrace();
				} catch (IOException e) {
					searchJSON = "Internet";
					// e.printStackTrace();
				} catch (Exception ec) {
					ec.printStackTrace();
				}

			}
			return searchJSON;

		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("")) {
				Toast.makeText(getBaseContext(),
						"Not able to search for nothing", Toast.LENGTH_SHORT)
						.show();
			} else if (result.equals("Internet")) {
				Log.d("check", "check");
				Toast.makeText(
						getBaseContext(),
						"Cant connect to twitter service, Check your internet connection, and try again later",
						Toast.LENGTH_LONG).show();
			} else {
				model.handleTweetSearch(result);
			}

			btnSearch.setEnabled(true);
			btnSearch.setText("Search");
			Log.d("search result", result);
			super.onPostExecute(result);
		}

	}

	/**
	 * Een asynctask dat de Users ophaalt met de gevraagde zoekterm. Gebruikt
	 * hiervoor de user tokens om bij twitter de zoekterm op te kunnen vragen.
	 * 
	 */
	public class GetUsersTask extends AsyncTask<String, Integer, String> {
		private HttpResponse response;

		@Override
		protected String doInBackground(String... params) {
			String searchJSON = "";
			if (!params[0].equals("")) {

				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"https://api.twitter.com/1.1/users/search.json?q="
								+ params[0]);
				try {
					consumer.sign(httpGet);

					ResponseHandler<String> handler = new BasicResponseHandler();
					response = client.execute(httpGet);
					searchJSON = handler.handleResponse(response);
				} catch (ClientProtocolException e) {
					//int statusCode = response.getStatusLine().getStatusCode();
					e.printStackTrace();
				} catch (IOException e) {
					searchJSON = "Internet";
					// e.printStackTrace();
				} catch (Exception ec) {
					ec.printStackTrace();
				}

			}
			return searchJSON;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("")) {
				Toast.makeText(getBaseContext(),
						"Not able to search for nothing", Toast.LENGTH_SHORT)
						.show();
			} else if (result.equals("Internet")) {
				Log.d("check", "check");
				Toast.makeText(
						getBaseContext(),
						"Cant connect to twitter service, Check your internet connection, and try again later",
						Toast.LENGTH_LONG).show();
			} else {
				model.handleUserSearch(result);
			}

			btnSearch.setEnabled(true);
			btnSearch.setText("Search");
			Log.d("search result", result);
			super.onPostExecute(result);
		}

	}

}
