package com.cloudinary.photoalbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SplashScreenActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.d("SplashScreen - created");

		setContentView(R.layout.activity_splash_screen);
		scheduleRedirect();
	}

	private void scheduleRedirect() {
		// Calls login activity after splash screen timeout
		final Activity current = this;
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent(current, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}, Constants.SPLASH_SCREEN_TIMEOUT);
	}
}