package com.ingress.portal.log;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;

public class MainActivity extends Activity
implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	static public boolean showSort = false;
	static public Menu menuMain = null;
	static public boolean savePos = true;
	static public String version = "1";
	static public boolean first = true;

	static public String getVersion() { 
		return version;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences settings = getSharedPreferences("Settings", 0);
		savePos = settings.getBoolean("savePos", true);

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));


	}

	@Override
	protected void onStart(){
		super.onStart();

		if(MainActivity.first) {
			try {
				Boolean Resultado = new CheckVersion().execute(MainActivity.getVersion()).get();
				if(Resultado) {
					Toast.makeText(getApplicationContext(), "Nova versão disponível, baixando APK atualizado", Toast.LENGTH_LONG).show();
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/IPL-dev/IPL/blob/master/bin/IPL.apk?raw=true"));
					startActivity(browserIntent);
				}
				else if (CheckPortalsFragment.outdated){
					Toast.makeText(getApplicationContext(), "Sem conexão com a internet", Toast.LENGTH_LONG).show();
					CheckPortalsFragment.outdated = false;
				}
				else {
					Toast.makeText(getApplicationContext(), "Versão atualizada", Toast.LENGTH_LONG).show();
				}
			} catch (Exception ex) {
			}
			MainActivity.first = false;
		}
	}

	@Override
	protected void onStop(){
		super.onStop();

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences("Settings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("savePos", savePos);

		// Commit the edits!
		editor.commit();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		switch(position) {
		case 1:
			fragmentManager.beginTransaction()
			.replace(R.id.container, new InsertPortalFragment())
			.commit();
			showSort = false;
			break;
		case 0:
			fragmentManager.beginTransaction()
			.replace(R.id.container, new CheckPortalsFragment())
			.commit();
			showSort = true;
			break;
		case 2:
			fragmentManager.beginTransaction()
			.replace(R.id.container, new ShowPivotsFragment())
			.commit();
			showSort = true;
			break;
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuMain = menu;
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			if(showSort)
				getMenuInflater().inflate(R.menu.main2, menu);
			else
				getMenuInflater().inflate(R.menu.main, menu);

			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		boolean selected = false;

		switch(id) {
		case(R.id.action_settings):
			/*FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
					.replace(R.id.container, new SettingsFragment())
					.commit();*/
			Intent dialogIntent = new Intent(getBaseContext(), SettingsFragment.class);
		//dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(dialogIntent);
		selected = false;
		break;
		case(R.id.menuSortName):
			Toast.makeText(getApplicationContext(), "List sorted by portal name", Toast.LENGTH_SHORT).show();
		CheckPortalsFragment.SortOrder = 0;
		selected = true;
		break;
		case(R.id.menuSortDate):
			Toast.makeText(getApplicationContext(), "List sorted by portal capture date", Toast.LENGTH_SHORT).show();
		CheckPortalsFragment.SortOrder = 1;
		selected = true;
		break;
		case(R.id.menuSortTime):
			Toast.makeText(getApplicationContext(), "List sorted by portal time captured", Toast.LENGTH_SHORT).show();
		CheckPortalsFragment.SortOrder = 2;
		selected = true;
		break;
		case(R.id.menuSortRecharge):
			Toast.makeText(getApplicationContext(), "List sorted by portal recharge date", Toast.LENGTH_SHORT).show();
		CheckPortalsFragment.SortOrder = 3;
		selected = true;
		break;
		}

		if(selected) {
			CheckPortalsFragment.activeFrag.refreshList();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(
					getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

}
