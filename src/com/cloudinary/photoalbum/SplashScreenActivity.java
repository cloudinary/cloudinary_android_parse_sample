package com.cloudinary.photoalbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nostra13.universalimageloader.utils.L;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SplashScreenActivity extends Activity {
	private static final int SPLASH_SCREEN_TIMEOUT = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash_screen);
		scheduleRedirect();
	}

	private void scheduleRedirect() {
		// Calls login activity after splash screen timeout
		final Activity current = this;
		L.i("Running");
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent(current, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}, SPLASH_SCREEN_TIMEOUT);
	}
}