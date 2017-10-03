package org.jeromegout.simplycloud.activities;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;


import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.fragments.FileFragment;
import org.jeromegout.simplycloud.fragments.MovieFragment;
import org.jeromegout.simplycloud.fragments.PhotoFragment;

import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends BaseActivity {

	private TabLayout tabLayout;
	private ViewPager viewPager;

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
		tabLayout.getTabAt(0).setIcon(R.drawable.ic_movie_selection);
		tabLayout.getTabAt(1).setIcon(R.drawable.ic_photo_selection);
		tabLayout.getTabAt(2).setIcon(R.drawable.ic_file_selection);
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
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_selection;
	}

	private void setViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new MovieFragment(), "Movie");
		adapter.addFragment(new PhotoFragment(),"Photo");
		adapter.addFragment(new FileFragment(), "File");
		viewPager.setAdapter(adapter);
	}
}
