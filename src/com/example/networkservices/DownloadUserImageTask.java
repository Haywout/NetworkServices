package com.example.networkservices;

import java.io.InputStream;

import com.example.networkservices.model.User;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadUserImageTask extends AsyncTask<String, Void, Bitmap> {
	private User user;
	private Context context;

	/**
	 * public constructor
	 */
	public DownloadUserImageTask(User user, Context context) {
		this.user = user;
		this.context = context;
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		// gets the url
		String url = urls[0];

		Bitmap userIcon = null;

		try {
			InputStream input = new java.net.URL(url).openStream();
			userIcon = BitmapFactory.decodeStream(input);
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userIcon;

	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// als er geen resultaat terugkomt dan bestaat het plaatje niet meer. En
		// wordt er een standaard plaatje in de gebruiker opgeslagen.
		// Wordt er wel een resultaat gevonden dan wordt deze in de gebruiker
		// opgeslagen.
		if (result == null) {
			result = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.nopicturepicture);
			Log.d("Plaatje", "Plaatje niet gevonden");
		} else {
			Log.d("Plaatje", "Plaatje gevonden");
		}
		user.setUserPicture(result);

		super.onPostExecute(result);
	}

}
