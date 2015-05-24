package com.example.networkservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import model.example.networkservices.TweetModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends Activity {
	private String jsonInput;
	private TweetModel model;
	private LinearLayout llSearchLayout, llMakeTweetLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// haal het model op
		TweetApplication app = (TweetApplication) getBaseContext()
				.getApplicationContext();
		model = app.getModel();

		// lees het json bestand uit
		try {
			jsonInput = readAssetIntoString("searchresult.json");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			JSONObject zoekresultaat = new JSONObject(jsonInput);
			JSONArray tweetsJson = zoekresultaat.getJSONArray("statuses");

			// doorloopt de array om zo alle tweets eruit te halen
			for (int i = 0; i < tweetsJson.length(); i++) {
				model.addTweet(new Tweet(tweetsJson.getJSONObject(i)));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		ListView listView = (ListView) findViewById(R.id.lvTweet);
		llMakeTweetLayout = (LinearLayout) findViewById(R.id.llmakeTweet);
		llSearchLayout = (LinearLayout) findViewById(R.id.llSearch);
		Tweetadapter tweetAdapter = new Tweetadapter(this, model.getTweets());
		model.addObserver(tweetAdapter);
		listView.setAdapter(tweetAdapter);
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
		if (id == R.id.action_add) {
			
			if (llMakeTweetLayout.getVisibility() != View.VISIBLE) {
				llMakeTweetLayout.setVisibility(View.VISIBLE);
				if (llSearchLayout.getVisibility() == View.VISIBLE) {
					llSearchLayout.setVisibility(View.GONE);
				}
			} else {
				llMakeTweetLayout.setVisibility(View.GONE);
			}
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
	 * Reads an asset file and returns a string with the full contents.
	 * 
	 * @param filename
	 *            The filename of the file to read.
	 * @return The contents of the file.
	 * @throws IOException
	 *             If file could not be found or not read.
	 */
	private String readAssetIntoString(String filename) throws IOException {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			InputStream is = getAssets().open(filename,
					AssetManager.ACCESS_BUFFER);
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

}
