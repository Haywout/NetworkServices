package com.example.networkservices;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.crypto.spec.IvParameterSpec;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.example.networkservices.model.Entities;
import com.example.networkservices.model.Hashtag;
import com.example.networkservices.model.Media;
import com.example.networkservices.model.Tweet;
import com.example.networkservices.model.Url;
import com.example.networkservices.model.User;

import android.R.drawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

public class UserAdapter extends ArrayAdapter<User> implements Observer {
	private Context context;
	private CommonsHttpOAuthConsumer consumer;
	
	public UserAdapter(Context context, ArrayList<User> users) {
		super(context, 0, users);
		this.context = context;
		consumer = ((TweetApplication) context.getApplicationContext()).getConsumer();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final User user = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.listdetailsuser, parent, false);
		}

		ImageView userIcon = (ImageView) convertView
				.findViewById(R.id.ivUserPictureListdetails);
		TextView userName = (TextView) convertView
				.findViewById(R.id.tvUsernameListDetails);
		TextView userScreenName = (TextView) convertView
				.findViewById(R.id.tvUserScreenNameListDetails);
		
		
		userName.setText(user.getName());
		userScreenName.setText(user.getScreenName());
		// loads user picture. 
		// If no picture available it makes a new task to download the image
		Bitmap userPicture = user.getScreenPicture();
		if (userPicture == null) {
			DownloadUserImageTask task = new DownloadUserImageTask(user, context);
			task.execute(user.getPictureURL());
			
			
		} else {
			userIcon.setImageBitmap(userPicture);
		}
	
		
		return convertView;
	}

	@Override
	public void update(Observable observable, Object data) {
		this.notifyDataSetChanged();
		
	}
}
