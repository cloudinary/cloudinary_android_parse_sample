package com.cloudinary.photoalbum;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShowPhotoActivity extends FragmentActivity {
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	Context context;
	String cloudinaryIdentifier;

	/**
	 * Cloudinary: List of transformations and names to display
	 */
	@SuppressWarnings("serial")
	static ArrayList<Pair<String, Transformation>> transformations = new ArrayList<Pair<String, Transformation>>() {{
		add(new Pair<String, Transformation>("Original", new Transformation()));
		add(new Pair<String, Transformation>("Round fill", new Transformation().width(400).height(700).crop("fill").radius(10)));
		add(new Pair<String, Transformation>("Scale", new Transformation().width(400).height(700).crop("scale")));
		add(new Pair<String, Transformation>("Fit", new Transformation().width(400).height(700).crop("fit")));
		add(new Pair<String, Transformation>("Thumb + face", new Transformation().width(400).height(700).crop("thumb").gravity("face")));
		add(new Pair<String, Transformation>("Shabang", new Transformation().width(400).height(700).crop("fill").gravity("north").chain().angle(20).chain().effect("sepia")));
	}};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_photo);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		Bundle bundle = getIntent().getExtras();
		cloudinaryIdentifier = bundle.getString(Constants.EXTRA_PHOTO);

		context = this;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new ImageByURLSectionFragment();
			Bundle args = new Bundle();

			Cloudinary cloudinary = PhotoAlbumApplication.getInstance(context).getCloudinary();
			Pair<String, Transformation> transformation = transformations.get(position);
			// Cloudinary: generate a URL reflecting the given transformation on the given identifier.
			String url = cloudinary.url().fromIdentifier(cloudinaryIdentifier).transformation(transformation.second).generate();
			args.putString(ImageByURLSectionFragment.ARG_URL, url);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return transformations.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Pair<String, Transformation> transformation = transformations.get(position);
			Locale l = Locale.getDefault();
			return transformation.first.toUpperCase(l);
		}
	}

	/**
	 * A fragment representing a section of the app
	 * displays an image
	 */
	public static class ImageByURLSectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_URL = "url";

		public ImageByURLSectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_show_photo, container, false);

			ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
			ImageLoader.getInstance().displayImage(getArguments().getString(ARG_URL), imageView);

			return rootView;
		}
	}
}