package com.sushant2603.instagramviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class PhotosActivity extends Activity {
	public static final String CLIENT_ID = "a475dc8f9f2d40039c0e67ef6bac350d";
	public static final String FIELD_ID = "data";
	private ArrayList<InstagramPhoto> photos;
	private InstagramPhotosAdapter aPhotos;
	private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        fetchPopularPhotos();
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				fetchPopularPhotos();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchPopularPhotos() {
    	photos = new ArrayList<InstagramPhoto>();
    	aPhotos = new InstagramPhotosAdapter(this, photos);
    	ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
    	lvPhotos.setAdapter(aPhotos);
    	// Setup populat url endpoint.
    	String popular_url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
    	// Create the network client.
    	AsyncHttpClient client = new AsyncHttpClient();
    	// Trigger the network client.
    	client.get(popular_url, new JsonHttpResponseHandler() {
    		// define success and failure callbacks.
    		@Override
    		public void onSuccess(int statusCode, Header[] headers,
    				JSONObject response) {
    			JSONArray photosJSON = null;
    			try {
    				photos.clear();
    				photosJSON = response.getJSONArray(FIELD_ID);
    				for (int index = 0; index < photosJSON.length(); index++) {
    					JSONObject photoJSON = photosJSON.getJSONObject(index);
    					InstagramPhoto photo = new InstagramPhoto();
  						photo.username = photoJSON.getJSONObject("user").getString("username");
    					photo.imageUrl = photoJSON.getJSONObject("images").
    							getJSONObject("standard_resolution").getString("url");
    					photo.imageHeight = photoJSON.getJSONObject("images")
    							.getJSONObject("standard_resolution").getInt("height");
    					photo.imageWidth = photoJSON.getJSONObject("images")
    							.getJSONObject("standard_resolution").getInt("width");
    					photo.likes_count = photoJSON.getJSONObject("likes").getInt("count");
    					photo.userImageUrl = photoJSON.getJSONObject("user")
    							.getString("profile_picture");

    					// Get date
  						photo.date = photoJSON.getLong("created_time");
    					// Check and add caption.
						photo.caption = "";
    					if (!photoJSON.isNull("caption")) {
    						photo.caption = photoJSON.getJSONObject("caption").getString("text");
    					}
    					// Parse Location.
    					photo.location="";
    					if(!photoJSON.isNull("location")) {
    						JSONObject locationObject = photoJSON.getJSONObject("location");
    						if(locationObject.has("name")){
    							photo.location = photoJSON.getJSONObject("location")
    									.getString("name");
    						} else if(locationObject.has("latitude") &&
    								locationObject.has("longitude")) {
	    						Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
	    						double lat = locationObject.getDouble("latitude");
	    						double lng = locationObject.getDouble("longitude");
		    					try {
		    						List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
		    						if (addresses.size() > 0) {
		    							photo.location = addresses.get(0).getLocality();
		    						}
		    					} catch (IOException e) {
		    						e.printStackTrace();
		    					}
	    					}
    					}
    					// Parse comments and get the latest.
    					if (!photoJSON.isNull("comments")) {
                            PhotoComment comment = new PhotoComment();
                            JSONArray listComments = photoJSON.getJSONObject("comments")
                            		.getJSONArray("data");
                            JSONObject commentObject = listComments.getJSONObject(
                            		listComments.length()-1);
                            JSONObject userObject = commentObject.getJSONObject("from");
                            comment.username = userObject.getString("username");
                            comment.userImageUrl = userObject.getString("profile_picture");
                            comment.text = commentObject.getString("text");
                            photo.comment = comment;
    					}
    					photos.add(photo);
    				}
    				aPhotos.notifyDataSetChanged();
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    		}
    		@Override
    		public void onFailure(int statusCode, Header[] headers,
    				String responseString, Throwable throwable) {
    			super.onFailure(statusCode, headers, responseString, throwable);
    		}
    	});
    }
 }
