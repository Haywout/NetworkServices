package com.example.networkservices;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.networkservices.R;

public class Tweetadapter extends ArrayAdapter<Tweet> {
	
	

	public Tweetadapter(Context context, ArrayList<Tweet> tweets) {
		super(context, 0, tweets);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);
		
		 if(convertView == null)
	        {
	            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listdetails, parent, false);
	        }
		 
		ImageView gebruikersIcoon = (ImageView)convertView.findViewById(R.id.ivGebruikersIcoon);
		TextView gebruikersNaam = (TextView)convertView.findViewById(R.id.tvNaam);
		TextView gebruikersSchermnaam = (TextView)convertView.findViewById(R.id.tvGebruikersnaam);
		TextView tweetDatum = (TextView)convertView.findViewById(R.id.tvDatum);
		TextView tweetText = (TextView)convertView.findViewById(R.id.tvTweet);
		
		// zo kan het gewoon verder
		gebruikersNaam.setText(tweet.getUser().getName());
		gebruikersSchermnaam.setText(tweet.getUser().getScreenName());
		tweetDatum.setText(tweet.getDatum());
		tweetText.setText(tweet.getText());
		return convertView;
	}
		
}
