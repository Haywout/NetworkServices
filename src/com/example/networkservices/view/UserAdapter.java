package com.example.networkservices.view;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.networkservices.DownloadUserImageTask;
import com.example.networkservices.R;
import com.example.networkservices.model.User;

public class UserAdapter extends ArrayAdapter<User> implements Observer {
	private Context context;
	
	public UserAdapter(Context context, ArrayList<User> users) {
		super(context, 0, users);
		this.context = context;
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
