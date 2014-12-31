package com.ingress.portal.log;

import java.util.ArrayList;

import com.ingress.portal.log.android.sqlite.MySQLiteHelper;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
		case 3:
			fragmentManager.beginTransaction()
			.replace(R.id.container, new CheckGroupsFragment())
			.commit();
			showSort = false;
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
	
	public void DebugTables() {
		MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
		Log.d("TABELA", "PORTALS");
		Cursor c = db.getAllPortals(0);
		
		c.moveToFirst();
		while(!c.isAfterLast()) {
			Log.d("TABELA", c.getString(0) + "|" + c.getString(1) + "|" +c.getString(2) + "|" +c.getString(3) + "|" +c.getString(4) + "|" +c.getString(5) + "|" +c.getString(6) + "|" +c.getString(7) + "|" + c.getString(8) + "|" + c.getString(9));
			c.moveToNext();
		}
		c.close();
		
		Log.d("TABELA", "GROUPS");
		c = db.getGroups();
		
		c.moveToFirst();
		while(!c.isAfterLast()) {
			Log.d("TABELA", c.getString(0) + "|" + c.getString(1));
			c.moveToNext();
		}
		c.close();
		
		Log.d("TABELA", "PORTALGROUPS");
		c = db.getAllPortalGroups();
		
		c.moveToFirst();
		while(!c.isAfterLast()) {
			Log.d("TABELA", c.getString(0) + "|" + c.getString(1) + "|" +c.getString(2));
			c.moveToNext();
		}
		c.close();
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
	
	public void openGroupInsert(final Spinner parent) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Insert Group");
		alert.setMessage("Group Name");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		final MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
		
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				db.addGroup(value);
				updateSpinner(parent, value);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();
	}
	
	public void updateSpinner(Spinner sp, String item) {
		ArrayList<String> lista = new ArrayList<String>();
		MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
		Cursor c = db.getGroups();
		
		c.moveToFirst();
		lista.add("");
		lista.add("New Group...");
		lista.add("All");
		int it = 0;
		int i = 0;
		while(!c.isAfterLast()) {
			lista.add(c.getString(1));
			if(c.getString(1).compareTo(item) == 0) {
				it = i;
			}
			c.moveToNext();
			i++;
		}
		
		c.close();
		
		if(CheckGroupsFragment.active) {
			CheckGroupsFragment.activeFrag.refreshList();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		if(item.compareTo("") != 0)
			it += 3;
		sp.setSelection(it);
	}
	
	public void openGroupDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Insert Group");
		alert.setMessage("Group Name");

		// Set an EditText view to get user input 
		final Spinner input = new Spinner(this);
		
		updateSpinner(input, "");
		
		input.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg2 == 1) {
					openGroupInsert(input);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getSelectedItem().toString();
				Toast.makeText(getApplicationContext(), "TESTE: " + value, Toast.LENGTH_LONG).show();
				if((value.compareTo("All") != 0) && (value.compareTo("") != 0) && (value.compareTo("New Group...") != 0)) {
					CheckPortalsFragment.list = 1;
					CheckPortalsFragment.Group = value;
				}
				else {
					CheckPortalsFragment.list = 0;
					CheckPortalsFragment.Group = "";
				}
				if(CheckPortalsFragment.active) CheckPortalsFragment.activeFrag.refreshList();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();
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
		case(R.id.group_menu):
			openGroupDialog();
			DebugTables();
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
