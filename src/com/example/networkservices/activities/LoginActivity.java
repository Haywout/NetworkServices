package com.example.networkservices.activities;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.networkservices.R;
import com.example.networkservices.TweetApplication;

public class LoginActivity extends Activity {
	private CommonsHttpOAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;
	private WebView webview;
	private TweetApplication app;
	public static final String TOKENS = "tokens";
	public static final String TOKEN = "token";
	public static final String TOKENSECRET = "tokensecret";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		SharedPreferences preferencesTokens = getSharedPreferences(TOKENS, 0);
		String tempToken = preferencesTokens.getString(TOKEN, null);
		String tempTokenSecret = preferencesTokens.getString(TOKENSECRET, null);
		app = (TweetApplication) getApplicationContext();
		provider = app.getProvider();
		consumer = app.getConsumer();

		Log.d("TokensPref", tempToken + " + " + tempTokenSecret);

		// als er geen tokens opgeslagen zijn. Haal dan tokens op van twitter.
		// als er wel tokens opgeslagen zijn. Ga dan verder naar de
		// mainactivity.
		if (tempToken == null || tempTokenSecret == null) {
			webview = (WebView) findViewById(R.id.webView1);

			webview.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (url.startsWith(TweetApplication.OAUTH_CALLBACK_URL)) {
						String oauthVerifier = url.split("=")[2];
						Log.d("verifier", oauthVerifier);
						RetrieveAccessTokenTask task = new RetrieveAccessTokenTask();
						task.execute(oauthVerifier);

					}
					return super.shouldOverrideUrlLoading(view, url);
				}
			});

			RetrieveRequestTokenTask task = new RetrieveRequestTokenTask();
			task.execute(TweetApplication.OAUTH_CALLBACK_URL);
		} else {

			consumer.setTokenWithSecret(tempToken, tempTokenSecret);

			startMainActivity();
		}
	}

	private void startMainActivity() {
		Intent i = new Intent(this, HomeActivity.class);
		startActivity(i);
		finish();
	}

	/**
	 * Retrieves the request token, where the user can login.
	 * 
	 * @author Casper
	 * 
	 */
	private class RetrieveRequestTokenTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String url = null;
			try {
				url = provider.retrieveRequestToken(consumer, params[0]);
				return url;

			} catch (Exception e) {
				return url;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("result", result);
			if (result != null) {
				webview.loadUrl(result);
			}

			super.onPostExecute(result);
		}
	}

	/**
	 * Retrieves the accesstoken, which can be used for creating the consumer
	 * object later.
	 * 
	 * @author Casper
	 * 
	 */
	private class RetrieveAccessTokenTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				provider.retrieveAccessToken(consumer, params[0]);
			} catch (Exception e) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			String token = consumer.getToken();
			String tokenSecret = consumer.getTokenSecret();

			rememberCredents(token, tokenSecret);

			Log.d("tokens", token + ", " + tokenSecret);

			startMainActivity();
			super.onPostExecute(result);
		}

	}

	/**
	 * Saves the tokens in a shared preference, so that on restart the tokens
	 * can be pulled out of it
	 * 
	 * @param token
	 * @param tokenSecret
	 */
	private void rememberCredents(String token, String tokenSecret) {
		SharedPreferences settings = getSharedPreferences(TOKENS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(TOKEN, token);
		editor.putString(TOKENSECRET, tokenSecret);
		// Commit the edits!
		editor.commit();

	}

}
