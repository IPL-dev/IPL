package com.ingress.portal.log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.ingress.portal.log.android.sqlite.MySQLiteHelper;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;


public class CheckGroupsFragment extends Fragment{

	public SimpleCursorAdapter dataAdapter;
	static public boolean active = false;
	static public CheckGroupsFragment activeFrag;
	static public int SortOrder = 0;
	static public boolean outdated = false;

	@Override
	public void onStart() {
		super.onStart();
		active = true;
		activeFrag = this;
	}

	@Override
	public void onStop() {
		super.onStop();
		active = false;
		activeFrag = null;
		getActivity().unregisterForContextMenu((ListView) this.getView().findViewById(R.id.listView1groups));
	}

	public void refreshList(){
		displayResultList(this.getView());
	}

	public void displayResultList(View v) {

		MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
		
		Cursor cursor = db.getGroups();
		String[] columns = new String[] {
				"name"
		};
		
		int[] fields = new int[] {
				R.id.textView1
		};

		dataAdapter = new SimpleCursorAdapter(
				getActivity(), R.layout.listrow_group, 
				cursor, 
				columns, 
				fields,
				0);
		ListView listView = (ListView) v.findViewById(R.id.listView1groups);
		registerForContextMenu(listView);
			
		listView.setAdapter(dataAdapter);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_groups, container, false);
		displayResultList(v);
		
		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.listView1groups) {
			
			ListView listView = (ListView) v.findViewById(R.id.listView1groups);
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
			
			menu.setHeaderTitle(((Cursor)listView.getItemAtPosition(info.position)).getString(1));
			String[] menuItems = new String[] {
					"Remove"
			};

			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	public String formatDate(String d) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd/MM/yyyy  -  HH:mm:ss");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(d);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String finalDate = timeFormat.format(myDate);

		return finalDate;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
		int position = item.getItemId();
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();

		ListView listView = (ListView) this.getView().findViewById(R.id.listView1groups);
		Cursor cursor = (Cursor) listView.getItemAtPosition(info.position);
		
		Toast.makeText(getActivity().getApplicationContext(), "id: " + cursor.getInt(0) + " name: " + cursor.getString(1), Toast.LENGTH_LONG).show();

		switch(position) {
			case 0:
				db.deleteGroup(cursor.getInt(0));
				break;
		}
		refreshList();

		return true;
	}
}
