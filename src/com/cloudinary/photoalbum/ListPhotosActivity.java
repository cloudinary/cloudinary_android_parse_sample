package com.cloudinary.photoalbum;

import java.util.List;

import android.app.Activity;
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
import com.nostra13.universalimageloader.utils.L;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ListPhotosActivity extends Activity {
	protected Cloudinary cloudinary;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ParseImageAdapter adapter;
	protected GridView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private void showImage(String identifier) {
		Intent intent = new Intent(this, ShowPhotoActivity.class);
		intent.putExtra("com.cloudinary.photo", identifier);
		startActivity(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		adapter.clearCache();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_photos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_upload:
			Intent intent = new Intent(this, UploadPhotoActivity.class);
			startActivity(intent);
		}
		return false;
	}
	
	public class ParseImageAdapter extends BaseAdapter {
		private static final int ITEM_PER_FETCH = 20;
		private ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
		private int cachePosition;
		private List<ParseObject> cache = null;
		private Transformation thumbnailTransformation = new Transformation().width(120).height(120).crop("fill");

		private String getIdentifier(int index) throws ParseException {
			int base = index-(index%ITEM_PER_FETCH);
			L.i("getIdentifier for index %d", index);
			if (cache == null || cachePosition != base) {
				L.i("Fetching %d items since %d", ITEM_PER_FETCH, base);
				query.setSkip(base);
				query.setLimit(ITEM_PER_FETCH);
				cache = query.find();
				L.i("Done");
				L.i("cache: %s", cache);
				L.i("Done");
			}
			ParseObject obj = cache.get(index % ITEM_PER_FETCH);
			L.i("Returning obj: %s for index %d", obj.toString(), index);
			String identifier = obj.getString("cloudinary_identifier");
			L.i("Returning identifier: %s for index %d", identifier, index);
			return identifier;
		}

		private String getUrl(int index) throws ParseException {
			String identifier = getIdentifier(index);
			String url = cloudinary.url().fromIdentifier(identifier).transformation(thumbnailTransformation).generate();
			L.i("Returning url: %s for index %d", url, index);
			return url;
		}

		private void clearCache() {
			L.i("Clearing cache. Cache policy: %s", query.getCachePolicy().toString());
			cache = null;
			query.clearCachedResult();
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			try {
				int count;
				L.i("Counting");
				count = query.count();
				L.i("Done: %d", count);
				return count;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return imageView;
		}
	}
}
