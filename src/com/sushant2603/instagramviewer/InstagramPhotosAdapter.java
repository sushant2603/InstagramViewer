package com.sushant2603.instagramviewer;

import java.util.List;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Point;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

	public InstagramPhotosAdapter(Context context, List<InstagramPhoto> photos) {
		super(context, android.R.layout.simple_list_item_1, photos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InstagramPhoto photo = getItem(position);
		// Only few items in the memory. Check if recycled view.
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo_advanced,
					parent, false);
		}
		// Fill the header.
		View header = convertView.findViewById(R.id.header);
		// User Name
		TextView username = (TextView) header.findViewById(R.id.tvUsername);
		username.setText(photo.username);
		// Location.
		TextView location = (TextView) header.findViewById(R.id.tvLocation);
		if (!photo.location.isEmpty()) {
			location.setText(photo.location);
		} else {
			location.setVisibility(TextView.GONE);
		}
		// Date
		TextView date = (TextView) header.findViewById(R.id.tvDate);
		if (photo.date != 0) {
			date.setText(DateUtils.getRelativeTimeSpanString(photo.date * 1000));
		} else {
			date.setVisibility(TextView.GONE);
		}

		// User profile image.
		ImageView imgUser = (ImageView) header.findViewById(R.id.imgUser);
		imgUser.setImageResource(0);
		Picasso.with(getContext()).load(photo.userImageUrl).into(imgUser);
		ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.ImageView01);

		// Get the main image and modified it to display with right aspect ratio.
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int newWidth = size.x;
        int newHeight = (int) (newWidth * photo.imageWidth / photo.imageHeight);
		imgPhoto.setImageResource(0);
		Picasso.with(getContext()).load(photo.imageUrl).resize(newWidth, newHeight).into(imgPhoto);

		// Populate the footer.
		View footer = convertView.findViewById(R.id.footer);
		// User Name.
		TextView likes = (TextView) footer.findViewById(R.id.tvLikes);
		likes.setText(Integer.toString(photo.likes_count) + " likes");

		// Add username and caption.
		TextView caption = (TextView) footer.findViewById(R.id.tvCaption);
		String caption_str = "";
		if (!photo.caption.isEmpty()) {
			caption_str = "<b><font color=\"#3A5FCD\">" + photo.username + "</font></b>";
			caption_str += " " + photo.caption;
			caption.setText(Html.fromHtml(caption_str));
		} else {
			caption.setVisibility(TextView.GONE);
		}
		// Add comment
		View commentsView = (View) footer.findViewById(R.id.commentsSection);
		if (photo.comment != null) {
			TextView comment = (TextView) commentsView.findViewById(R.id.tvComment1);
			String comment_str = "<b><font color=\"#3A5FCD\">" + photo.comment.username + "</font></b>";
			comment_str += " " + photo.comment.text;
			comment.setText(Html.fromHtml(comment_str));
			ImageView commentImage = (ImageView) footer.findViewById(R.id.imgUser1);
			commentImage.setImageResource(0);
			Picasso.with(getContext()).load(photo.comment.userImageUrl).into(commentImage);
		} else {
			commentsView.setVisibility(View.GONE);
		}
		return convertView;
	}

}
