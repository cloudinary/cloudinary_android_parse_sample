package com.cloudinary.photoalbum;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;

import com.cloudinary.Cloudinary;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class PhotoAlbumApplication extends Application {
	private Cloudinary cloudinary;
	
	public Cloudinary getCloudinary() {
		return cloudinary;
	}

	public static PhotoAlbumApplication getInstance(Context context) {
		return (PhotoAlbumApplication)context.getApplicationContext();
	}

	public void onCreate() {
		initUIL();
		initParse();
		initCloudinary();
	}

	private void initUIL() {
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
		L.i("Universal Image Loader initialized");
	}

	private void initParse() {
		Parse.initialize(this, "PARSE-APP-ID", "PARSE-CLIENT-KEY");

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);
		L.i("Parse initialized");
	}
	
	private void initCloudinary() {
		Map<String, String> config = new HashMap<String, String>();
		config.put("cloud_name", "CLOUDINARY-CLOUD-NAME");
		config.put("api_key", "CLOUDINARY-API-KEY");
		cloudinary = new Cloudinary(config);
	}
}
