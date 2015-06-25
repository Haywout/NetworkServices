package com.example.networkservices.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkservices.DownloadUserImageTask;
import com.example.networkservices.R;
import com.example.networkservices.TweetApplication;
import com.example.networkservices.model.Entities;
import com.example.networkservices.model.Hashtag;
import com.example.networkservices.model.Media;
import com.example.networkservices.model.Tweet;
import com.example.networkservices.model.Url;
import com.example.networkservices.model.User;
import com.example.networkservices.model.UserMentions;

public class Tweetadapter extends ArrayAdapter<Tweet> implements Observer {
	private Context context;
	private CommonsHttpOAuthConsumer consumer;

	public Tweetadapter(Context context, ArrayList<Tweet> tweets) {
		super(context, 0, tweets);
		this.context = context;
		consumer = ((TweetApplication) context.getApplicationContext())
				.getConsumer();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Tweet tweet = getItem(position);
		User user = tweet.getUser();
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.listdetailstweets, parent, false);
		}

		ImageView gebruikersIcoon = (ImageView) convertView
				.findViewById(R.id.ivGebruikersIcoon);
		TextView gebruikersNaam = (TextView) convertView
				.findViewById(R.id.tvNaam);
		TextView gebruikersSchermnaam = (TextView) convertView
				.findViewById(R.id.tvGebruikersnaam);
		TextView tweetDatum = (TextView) convertView.findViewById(R.id.tvDatum);
		TextView tweetText = (TextView) convertView.findViewById(R.id.tvTweet);
		Button button = (Button) convertView.findViewById(R.id.retweetButton);

		button.setMovementMethod(LinkMovementMethod.getInstance());

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				postRetweetTask retweetTask = new postRetweetTask();
				retweetTask.execute(tweet.getTweetId());

			}
		});
		LinearLayout ll = (LinearLayout) convertView
				.findViewById(R.id.llDatalayout);
		String tweetStringText = tweet.getText();
		String userName = tweet.getUser().getName();
		final SpannableStringBuilder sbTweet = new SpannableStringBuilder(
				tweetStringText);
		final SpannableStringBuilder sbUser = new SpannableStringBuilder(
				userName);

		int hashtagColor = Color.rgb(40, 255, 40);
		int urlColor = Color.rgb(40, 40, 255);
		int usermentionsColor = Color.rgb(40, 40, 200);

		final StyleSpan nameBoldStyle = new StyleSpan(
				android.graphics.Typeface.BOLD);

		ArrayList<Entities> tweetEntities = tweet.getEntities();

		for (int i = 0; i < ll.getChildCount(); i++) {
			if (ll.getChildAt(i) instanceof ImageView) {
				ImageView imageView = (ImageView) ll.getChildAt(i);
				ll.removeView(imageView);
			}
		}

		// sbTweet.setSpan(testFCS, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		for (Entities entities : tweetEntities) {
			if (entities instanceof Hashtag) {
				Log.d("entitie", "Hashtag");
				sbTweet.setSpan(new ForegroundColorSpan(hashtagColor),
						entities.getIndexEen(), entities.getIndexTwee(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (entities instanceof Url) {
				Log.d("entitie", "Url");
				sbTweet.setSpan(new ForegroundColorSpan(urlColor),
						entities.getIndexEen(), entities.getIndexTwee(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (entities instanceof UserMentions) {
				Log.d("entitie", "UserM");
				sbTweet.setSpan(new ForegroundColorSpan(usermentionsColor),
						entities.getIndexEen(), entities.getIndexTwee(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (entities instanceof Media) {
				Bitmap picture = ((Media) entities).getPicture();
				if (picture == null) {
					ImageDownloader downloader = new ImageDownloader(entities);
					downloader.execute(((Media) entities).getMediaUrl());
				} else {
					ImageView view = new ImageView(context);
					view.setImageBitmap(((Media) entities).getPicture());
					ll.addView(view);
				}
			}
		}

		sbUser.setSpan(nameBoldStyle, 0, userName.length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		// loads user picture.
		// If no picture available it makes a new task to download the image
		Bitmap userPicture = user.getScreenPicture();
		if (userPicture == null) {
			DownloadUserImageTask task = new DownloadUserImageTask(user,
					context);
			task.execute(user.getPictureURL());
		}

		// sets all data in the components on the screen
		gebruikersIcoon.setImageBitmap(user.getScreenPicture());
		gebruikersNaam.setText(sbUser);
		gebruikersSchermnaam.setText(tweet.getUser().getScreenName());
		tweetDatum.setText(tweet.getDatum());
		tweetText.setText(sbTweet);
		return convertView;
	}

	@Override
	public void update(Observable observable, Object data) {
		this.notifyDataSetChanged();

	}

	/**
	 * Downloads images from a tweet.
	 * 
	 * @author Casper
	 * 
	 */
	private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
		private Media media;

		public ImageDownloader(Entities entities) {
			media = (Media) entities;
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			String url = urls[0];
			Bitmap tweetPicture = null;
			try {
				InputStream input = new java.net.URL(url).openStream();
				tweetPicture = BitmapFactory.decodeStream(input);
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tweetPicture;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			media.setPicture(result);

			updateList();

			if (media.getPicture() == null) {
				Log.d("picture", "Not Found");
			} else {
				Log.d("picture", "Found picture");
			}

			super.onPostExecute(result);
		}
	}

	public void updateList() {
		notifyDataSetChanged();

	}

	/**
	 * Creates a retweet of the tweet that you want to retweed
	 * 
	 * @author Casper
	 * 
	 */
	private class postRetweetTask extends AsyncTask<String, Void, String> {
		private HttpResponse response;

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost(
					"https://api.twitter.com/1.1/statuses/retweet/" + params[0]
							+ ".json");

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
				int statusCode = response.getStatusLine().getStatusCode();
				return "" + statusCode;
			} catch (IOException e) {
				return "Internet";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				if (result.equals("403")) {
					Toast.makeText(context, "You already retweeted this tweet",
							Toast.LENGTH_LONG).show();
				} else if (result.equals("Internet")) {
					Toast.makeText(context, "Je hebt geen internet",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context, "Succesfully retweeted",
						Toast.LENGTH_SHORT).show();
			}

			Log.d("statuscode", "" + response.getStatusLine().getStatusCode());

			super.onPostExecute(result);
		}

	}

}
