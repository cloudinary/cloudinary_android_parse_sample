package com.cloudinary.photoalbum;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.cloudinary.Cloudinary;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

public class UploadPhotoActivity extends Activity {
	private final static int SELECT_PICTURE = 1;
	private final Activity current = this;
	private ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_PICTURE) {
			if (resultCode == RESULT_OK) {
				setDefaultLayout();
		        Uri selectedImageUri = data.getData();
		        L.d("Uploading file from URI: %s", selectedImageUri.getPath());
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};
	
	            Cursor cursor = getContentResolver().query(
	                               selectedImageUri, filePathColumn, null, null, null);
	            cursor.moveToFirst();
	
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();
		        L.d("Uploading file: %s", filePath);
		        startUpload(filePath);
			}
		}
	}
	
	private void setDefaultLayout() {
		setContentView(R.layout.activity_upload_photo);
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	private void startUpload(String filePath) {
		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
			protected String doInBackground(String... paths) {
				L.d("Running upload task");

				// sign request
				Map<String, String> uploadParams;
				try {
					// Parse+Cloudinary: retrieves a Cloudinary signature and upload params using the Parse cloud function.
					//   see https://github.com/cloudinary/cloudinary_parse
					uploadParams = ParseCloud.callFunction(Constants.PARSE_SIGN_CLOUD_FUNCTION, null);
			        L.i("Signed request: %s", uploadParams.toString());
				} catch (ParseException e) {
			        L.e(e, "Error signing request");
					return "Error signing request: " + e.toString();
				}
		
				// Upload to cloudinary
				Cloudinary cloudinary = PhotoAlbumApplication.getInstance(current).getCloudinary();
				File file = new File(paths[0]);
				JSONObject cloudinaryResult;
				try {
					// Cloudinary: Upload file using the retrieved signature and upload params
					cloudinaryResult = cloudinary.uploader().upload(file, uploadParams);
			        L.i("Uploaded file: %s", cloudinaryResult.toString());
				} catch (RuntimeException e) {
			        L.e(e, "Error uploading file");
					return "Error uploading file: " + e.toString();
				} catch (IOException e) {
			        L.e(e, "Error uploading file");
					return "Error uploading file: " + e.toString();
				}
		
				// update parse
				ParseObject photo = new ParseObject("Photo");
				try {
					// Parse+Cloudinary: Save a reference to the uploaded image in Parse backend. The
					//   field may be verified using the beforeSave filter demonstrated in:
					//   https://github.com/cloudinary/cloudinary_parse
					photo.put(Constants.PARSE_CLOUDINARY_FIELD, cloudinary.signedPreloadedImage(cloudinaryResult));
					photo.save();
			        L.i("Saved object");
				} catch (JSONException e) {
			        L.e(e, "Error saving object");
					return "Error saving object: " + e.toString();
				} catch (ParseException e) {
			        L.e(e, "Error saving object");
					return "Error saving object: " + e.toString();
				}
				return null;
			}

			protected void onPostExecute(String error) {
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				if (error == null) {
					setResult(RESULT_OK);
					finish();
				} else {
					new AlertDialog.Builder(current)
						.setTitle("Error")
						.setMessage(error)
						.setPositiveButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								finish();
							}
						})
						.setCancelable(true)
						.create().show();
				}
			}
		};
		dialog = ProgressDialog.show(this, "Uploading", "Uploading image");
		task.execute(filePath);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload_photo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
