package com.cloudinary.photoalbum;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.cloudinary.Cloudinary;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	        .cacheInMemory()
	        .cacheOnDisc()
	        .build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
	        .defaultDisplayImageOptions(defaultOptions)
	        .build();
		ImageLoader.getInstance().init(config);
		L.i("Universal Image Loader initialized");
	}

	private void initParse() {
		String appId = null;
		String clientKey = null;

		try {
			Bundle bundle = getPackageManager()
					.getApplicationInfo( getPackageName(), PackageManager.GET_META_DATA)
					.metaData;
			appId = bundle.getString("PARSE_APPLICATION_ID");
			clientKey = bundle.getString("PARSE_CLIENT_KEY");
		} catch (NameNotFoundException e) {
			// fall-thru
		} catch (NullPointerException e) {
			// fall-thru
		}
		if (appId == null || clientKey == null) {
			throw new RuntimeException("Couldn't load Parse meta-data params from manifest");
		}
		Parse.initialize(this, appId, clientKey);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);
		L.i("Parse initialized");
	}
	
	private void initCloudinary() {
		cloudinary = new Cloudinary(this);
	}
}
