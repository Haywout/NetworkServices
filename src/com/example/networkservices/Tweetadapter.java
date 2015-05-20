package com.example.networkservices;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Tweetadapter extends ArrayAdapter<Tweet> implements Observer {
	private Context context;
	
	public Tweetadapter(Context context, ArrayList<Tweet> tweets) {
		super(context, 0, tweets);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);
		User user = tweet.getUser();
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.listdetails, parent, false);
		}

		ImageView gebruikersIcoon = (ImageView) convertView
				.findViewById(R.id.ivGebruikersIcoon);
		TextView gebruikersNaam = (TextView) convertView
				.findViewById(R.id.tvNaam);
		TextView gebruikersSchermnaam = (TextView) convertView
				.findViewById(R.id.tvGebruikersnaam);
		TextView tweetDatum = (TextView) convertView.findViewById(R.id.tvDatum);
		TextView tweetText = (TextView) convertView.findViewById(R.id.tvTweet);

		String tweetStringText = tweet.getText();
		String userName = tweet.getUser().getName();
		final SpannableStringBuilder sbTweet = new SpannableStringBuilder(
				tweetStringText);
		final SpannableStringBuilder sbUser = new SpannableStringBuilder(
				userName);
		final ForegroundColorSpan urlFCS = new ForegroundColorSpan(Color.rgb(
				40, 40, 255));
		final ForegroundColorSpan hashtagFCS = new ForegroundColorSpan(
				Color.rgb(40, 255, 40));
		final StyleSpan nameBoldStyle = new StyleSpan(
				android.graphics.Typeface.BOLD);
		
		
		
		ArrayList<Entities> tweetEntities = tweet.getEntities();

		for (Entities entities : tweetEntities) {
			if (entities instanceof Hashtag) {
				sbTweet.setSpan(hashtagFCS, entities.getIndexEen(),
						entities.getIndexTwee(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			} else if (entities instanceof Url) {
				sbTweet.setSpan(urlFCS, entities.getIndexEen(),
						entities.getIndexTwee(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			}
		}

		sbUser.setSpan(nameBoldStyle, 0, userName.length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		// zo kan het gewoon verder
		
		Bitmap userPicture = user.getScreenPicture();
		if (userPicture == null) {
			DownloadUserImageTask task = new DownloadUserImageTask(user, context);
			task.execute(user.getPictureURL());
		}
	
		
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

}
