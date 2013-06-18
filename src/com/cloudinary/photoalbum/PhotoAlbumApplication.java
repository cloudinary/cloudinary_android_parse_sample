package com.cloudinary.photoalbum;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

public class PhotoAlbumApplication extends Application {
	public void onCreate() {
		initUIL();
		initParse();
	}
	private void initUIL() {
		ImageLoaderConfiguration.createDefault(this);
		L.i("Universal Image Loader initialized");
	}

	private void initParse() {
		Parse.initialize(this, "PARSE-APP-ID", "PARSE-CLIENT-KEY");

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		// defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		L.i("Parse initialized");
	}
}
