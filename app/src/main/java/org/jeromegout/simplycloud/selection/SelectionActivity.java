package org.jeromegout.simplycloud.selection;


import android.Manifest;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.andremion.counterfab.CounterFab;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.activities.BaseActivity;
import org.jeromegout.simplycloud.selection.fragments.FileFragment;
import org.jeromegout.simplycloud.selection.fragments.FileUtil;
import org.jeromegout.simplycloud.selection.fragments.MovieFragment;
import org.jeromegout.simplycloud.send.FreeSendActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends BaseActivity implements SelectionModel.SelectionListener,
		View.OnClickListener {

	private static final String TITLE_STATE = "title_state";


	private TabLayout tabLayout;
	private ViewPager viewPager;
	private CounterFab counterFab;

	class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		viewPager = (ViewPager) findViewById(R.id.viewpager);

		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);
		setViewPager(viewPager);
		tabLayout.getTabAt(0).setIcon(R.drawable.ic_movie_selection_24dp);
//		tabLayout.getTabAt(1).setIcon(R.drawable.ic_photo_selection_24dp);
		tabLayout.getTabAt(1).setIcon(R.drawable.ic_file_selection_24dp);
		tabLayout.addOnTabSelectedListener(
				new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
					@Override
					public void onTabSelected(TabLayout.Tab tab) {
						super.onTabSelected(tab);
						int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorTabSelectedIcon);
						tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
					}
					@Override
					public void onTabUnselected(TabLayout.Tab tab) {
						super.onTabUnselected(tab);
						int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorTabUnselectedIcon);
						tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
					}
					@Override
					public void onTabReselected(TabLayout.Tab tab) {
						super.onTabReselected(tab);
					}
				}
		);
		counterFab = (CounterFab) findViewById(R.id.counter_fab);
		counterFab.setOnClickListener(this);

		if (savedInstanceState == null) {
			setResult(RESULT_CANCELED);
			checkPermissions();
		} else {
			setToolbarTitle(savedInstanceState.getString(TITLE_STATE));
		}
	}

	private void checkPermissions() {
		MultiplePermissionsListener listener = SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
				.with(viewPager, "Storage and Internet permission is needed")
				.withOpenSettingsButton("Settings")
				.build();
		Dexter.withActivity(this)
				.withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.INTERNET)
				.withListener(listener)
				.check();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putCharSequence(TITLE_STATE, getToolbarTitle());
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_selection;
	}

	private void setViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new MovieFragment(), "Movie");
//		adapter.addFragment(new PhotoFragment(),"Photo");
		adapter.addFragment(new FileFragment(), "File");
		viewPager.setAdapter(adapter);
	}
	@Override
	public void onClick(View v) {
		long selectionSize = SelectionModel.instance.getSelectionSize();
		List<Uri> selection = SelectionModel.instance.getSelection();
		String msg = String.format("%d selected files (%s/%s)",
				selection.size(),
				FileUtil.getReadableSize(selectionSize),
				FileUtil.getReadableSize(FileUtil.getAvailableInternalMemorySize()));
		Snackbar.make(v, msg, Snackbar.LENGTH_SHORT).show();
		if(selection.size() > 0) {
			Intent intent = new Intent(getApplicationContext(), FreeSendActivity.class);
			intent.putExtra("size", selectionSize);
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("selection", (ArrayList<? extends Parcelable>) selection);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	@Override
	public void selectionChanged(int count, long amount) {
		counterFab.setCount(count);
		if(amount > 0) {
			Snackbar.make(viewPager.getRootView(), "Selection size is "+ FileUtil.getReadableSize(amount), Snackbar.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		SelectionModel.instance.addSelectionListener(this);
	}
}
