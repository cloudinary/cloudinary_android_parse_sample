package com.cloudinary.photoalbum;

import static com.cloudinary.photoalbum.Constants.TAG;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseACL;

public class PhotoAlbumApplication extends Application {
	private Cloudinary cloudinary;
	
	/**
	 * @return An initialized Cloudinary instance
	 */
	public Cloudinary getCloudinary() {
		return cloudinary;
	}

	/**
	 * Provides access to the singleton and the getCloudinary method
	 * @param context Android Application context
	 * @return instance of the Application singleton.
	 */
	public static PhotoAlbumApplication getInstance(Context context) {
		return (PhotoAlbumApplication)context.getApplicationContext();
	}

	/**
	 * Initializes UIL, Parse and Cloudinary upon creation of Application.
	 */
	@Override
	public void onCreate() {
		L.setTag(TAG);
		initUIL();
		initParse();
		initCloudinary();
	}

	private void initUIL() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	        .cacheInMemory(true)
	        .cacheOnDisk(true)
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

		
		/// Parse - Initializes with appId and clientKey from manifest
		Parse.initialize(this, appId, clientKey);

		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);
		L.i("Parse initialized");
	}
	
	private void initCloudinary() {
		// Cloudinary: creating a cloudinary instance using meta-data from manifest
		
		cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this));
		L.i("Cloudinary initialized");
	}
}
