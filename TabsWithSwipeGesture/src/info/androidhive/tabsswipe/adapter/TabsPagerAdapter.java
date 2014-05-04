package info.androidhive.tabsswipe.adapter;

import info.androidhive.tabsswipe.ScanFragment;
import info.androidhive.tabsswipe.ViewFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Scanning Object Page
			return new ScanFragment();
		case 1:
			// View Scanned Object activity
			return new ViewFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
