package com.example.networkservices;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadUserImageTask extends AsyncTask<String, Void, Bitmap>{
	User user;
	Context context;
	public DownloadUserImageTask(User user, Context context) {
		this.user = user;
		this.context = context;
	}
	@Override
	protected Bitmap doInBackground(String... urls) {
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
		if (result == null) {
			result = BitmapFactory.decodeResource(context.getResources(), R.drawable.nopicturepicture);
			Log.d("Plaatje", "Plaatje niet gevonden");
		} else {
			Log.d("Plaatje", "Plaatje gevonden");
		}
		user.setUserPicture(result);
		
		super.onPostExecute(result);
	}

}
