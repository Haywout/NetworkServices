package com.example.networkservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import model.example.networkservices.TweetModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends Activity {
	private static final String APIKEY = "1Omry6axtR8CxwPTul52t4Ser";
	private static final String APISECRET = "jLJezF5rQA15NlRUuuCj5Ra4znqIy25YmkwHj9QCuGc59J1K0G";
	private static final String ACCESSTOKEN = "3221154035-0ktlrwepNdRyURFcYNklYs0En3wiK5KYdYAQO3T";
	private static final String ACCESSTOKENSECRET = "43C4V71c8j5n89K5OqCUs1oxeAO3EqAkv1K0BVzAmkNEu";
	private static String bearerToken = "";

	private String jsonInput;
	private TweetModel model;
	private LinearLayout llSearchLayout, llMakeTweetLayout;
	private Button btnSearch;
	private EditText etSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			generateOAUTHToken();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// haal het model op
		TweetApplication app = (TweetApplication) getBaseContext()
				.getApplicationContext();
		model = app.getModel();

		
		
		// haal de componenten op
		ListView listView = (ListView) findViewById(R.id.lvTweet);
		llMakeTweetLayout = (LinearLayout) findViewById(R.id.llmakeTweet);
		llSearchLayout = (LinearLayout) findViewById(R.id.llSearch);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setEnabled(false);
		btnSearch.setBackgroundColor(Color.rgb(114, 149, 166));
		etSearch = (EditText) findViewById(R.id.etSearchText);
		Tweetadapter tweetAdapter = new Tweetadapter(this, model.getTweets());
		model.addObserver(tweetAdapter);
		listView.setAdapter(tweetAdapter);
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GetTweetsTask getTweets = new GetTweetsTask();
				getTweets.execute(etSearch.getText()+ "");
				Log.d("Search", "button pressed");
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
		// als er op de knop wordt gedrukt (op de actionbar) om een tweet te maken
		if (id == R.id.action_add) {
			if (llMakeTweetLayout.getVisibility() != View.VISIBLE) {
				llMakeTweetLayout.setVisibility(View.VISIBLE);
				if (llSearchLayout.getVisibility() == View.VISIBLE) {
					llSearchLayout.setVisibility(View.GONE);
				}
			} else {
				llMakeTweetLayout.setVisibility(View.GONE);
			}
		// als er op de knop wordt gedrukt (op de actionbar) om tweets te zoeken
		} else if (id == R.id.action_search) {
			if (llSearchLayout.getVisibility() != View.VISIBLE) {
				llSearchLayout.setVisibility(View.VISIBLE);
				if (llMakeTweetLayout.getVisibility() == View.VISIBLE) {
					llMakeTweetLayout.setVisibility(View.GONE);
				}
			} else {
				llSearchLayout.setVisibility(View.GONE);
			}
		}

		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * Genereert eerst de authenticatie string. Om met deze vervolgens de bearer token op te halen.
	 * @throws Exception
	 */
	private void generateOAUTHToken() throws Exception {

		String authString = APIKEY + ":" + APISECRET;
		String base64 = Base64.encodeToString(authString.getBytes(),
				Base64.NO_WRAP);

		GenerateTokenTask task = new GenerateTokenTask();
		task.execute(base64);

	}
	
	/**
	 * 
	 * Asynctask om de Bearer token aan te vragen(als er nog geen bearer token is aangevraagd met die AutenticatieString)
	 * Of om de bearer token op te halen van de twitter api, als die al wel een keer is aangevraagd.
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

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return token;
		}
		
		@Override
		protected void onPostExecute(String result) {
			bearerToken = result;
			// zet de zoekknop op enabled zodat mensen kunnen gaan zoeken.
			btnSearch.setEnabled(true);
			btnSearch.setBackgroundColor(Color.rgb(80, 157, 221));
			super.onPostExecute(result);
		}

	}
	
	/**
	 * Een asynctask dat de tweets ophaalt met de gevraagde zoekterm.
	 * Gebruikt hiervoor de bearertoken om bij twitter de zoekterm op te kunnen vragen.
	 *
	 */
	public class GetTweetsTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("https://api.twitter.com/1.1/search/tweets.json?q=" + params[0]);	
			httpGet.setHeader("Authorization", "Bearer " + bearerToken);
			ResponseHandler<String> handler = new BasicResponseHandler();
			String searchJSON = "";
			try {
				searchJSON = client.execute(httpGet, handler);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return searchJSON;
		}
		
		@Override
		protected void onPostExecute(String result) {
			model.setJsonString(result);
			Log.d("search result", result);
			super.onPostExecute(result);
		}
		
	}

}
