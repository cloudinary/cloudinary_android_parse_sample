package com.cloudinary.photoalbum;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;

public class ListPhotosActivity extends Activity {
	static final int REQUEST_UPLOAD = 1;

	protected Cloudinary cloudinary;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ParseImageAdapter adapter;
	protected GridView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Cloudinary: Retrieve and save initialized Cloudinary instance
		cloudinary = PhotoAlbumApplication.getInstance(this).getCloudinary();

		setContentView(R.layout.activity_list_photos);
		listView = (GridView) findViewById(R.id.gridView1);
		adapter = new ParseImageAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					showImage(adapter.getIdentifier(position));
				} catch (ParseException e) {
			        L.e(e, "Error getting identifier");
					errorMessage("Error getting identifier for image to show transformations: " + e.toString());
				}

			}
		});
	}

	private void errorMessage(String errorMessage) {
		new AlertDialog.Builder(this)
			.setTitle("Error")
			.setMessage(errorMessage)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					finish();
				}
			})
			.setCancelable(true)
			.create().show();
	}

	private void showImage(String identifier) {
		Intent intent = new Intent(this, ShowPhotoActivity.class);
		intent.putExtra(Constants.EXTRA_PHOTO, identifier);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_UPLOAD && resultCode == RESULT_OK) {
			adapter.clearCache();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_photos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.action_upload:
			intent = new Intent(this, UploadPhotoActivity.class);
			startActivityForResult(intent, REQUEST_UPLOAD);
			break;
		case R.id.action_refresh:
			adapter.clearCache();
			break;
		case R.id.action_logout:
			intent = new Intent(this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		}
		return false;
	}
	
	public class ParseImageAdapter extends BaseAdapter {
		private static final int ITEM_PER_FETCH = 20;
		private ParseQuery<ParseObject> query;
		private int cachePosition;
		private List<ParseObject> cache = null;
		private Transformation thumbnailTransformation = new Transformation().width(120).height(120).crop("fill");

		public ParseImageAdapter() {
			createQuery();
		}

		private void createQuery() {
			// Parse: Create a query for model Photo and set caching options
			query = ParseQuery.getQuery(Constants.PARSE_MODEL);
			query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		}

		private String getIdentifier(int index) throws ParseException {
			index = getCount() - 1 - index;
			int base = index-(index%ITEM_PER_FETCH);
			if (cache == null || cachePosition != base) {
				L.d("Fetching %d items since %d", ITEM_PER_FETCH, base);
				// Parse: Fetch ITEM_PER_FETCH items since index "base"
				query.setSkip(base);
				query.setLimit(ITEM_PER_FETCH);
				cache = query.find();
				cachePosition = base;
			}
			// Parse: Get identifier field from parse object
			ParseObject photo = cache.get(index % ITEM_PER_FETCH);
			String identifier = photo.getString(Constants.PARSE_CLOUDINARY_FIELD);
			L.d("Returning identifier: %s for index %d", identifier, index);
			return identifier;
		}

		private String getUrl(int index) throws ParseException {
			String identifier = getIdentifier(index);
			// Cloudinary: generate a URL reflecting the thumbnail transformation on the given identifier.
			String url = cloudinary.url().fromIdentifier(identifier).transformation(thumbnailTransformation).generate();
			return url;
		}

		private void clearCache() {
			L.d("Clearing cache. Cache policy: %s", query.getCachePolicy().toString());
			cache = null;
			ParseQuery.clearAllCachedResults();
			createQuery();
			query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			try {
				int count;
				count = query.count();
				return count;
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException("Can't query object count");
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
			} else {
				imageView = (ImageView) convertView;
			}

			try {
				imageLoader.displayImage(getUrl(position), imageView);
			} catch (ParseException e) {
		        L.e(e, "Error getting identifier");
				errorMessage("Error getting identifier for image to show in list: " + e.toString());
			}

			return imageView;
		}
	}
}
