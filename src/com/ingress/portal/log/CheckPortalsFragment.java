package com.ingress.portal.log;

import java.util.Calendar;

import java.util.TimeZone;
import com.ingress.portal.log.android.sqlite.MySQLiteHelper;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.ListView;

public class CheckPortalsFragment extends Fragment{

	public SimpleCursorAdapter dataAdapter;
	static public boolean active = false;
	static public CheckPortalsFragment activeFrag;
	static public int SortOrder = 0;

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
	}

	public void refreshList(){
		displayResultList(this.getView());
	}

	public void displayResultList(View v) {

		MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
		if(db.isUpdated()) {
			db.Upgrade();
		}
		
		Cursor cursor = db.getAllPortals(SortOrder);

		// The desired columns to be bound
		String[] columns = new String[] {
				"name", "dateF", "days", "rechargeF", "recharges", "latitude", "longitude"
		};

		// the XML defined views which the data will be bound to
		int[] to = new int[] {
				R.id.name,
				R.id.date,
				R.id.time,
				R.id.recharge,
				R.id.decayed
		};

		// create the adapter using the cursor pointing to the desired data 
		//as well as the layout information
		dataAdapter = new SimpleCursorAdapter(
				getActivity(), R.layout.listitem2, 
				cursor, 
				columns, 
				to,
				0);
		ListView listView = (ListView) v.findViewById(R.id.listView1Check);
		//getActivity().registerForContextMenu(listView);
		registerForContextMenu(listView);

		// Assign adapter to ListView		
		listView.setAdapter(dataAdapter);
	}

	public void onActivityResult (int requestCode, int resultCode, Intent data) {
		//Collect data from the intent and use it
		switch(requestCode) {
		case 0:
			if(resultCode == Activity.RESULT_OK)
				refreshList();
			break;
		case 1:
			if(resultCode == Activity.RESULT_OK)
				refreshList();
			break;
		}
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.check_portals, container, false);
		displayResultList(v);
		
		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==R.id.listView1Check) {
			ListView listView = (ListView) v.findViewById(R.id.listView1Check);
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			Cursor cursor = (Cursor) listView.getItemAtPosition(info.position);
			String temp = cursor.getString(1);
			menu.setHeaderTitle(temp);
			String[] menuItems;
			if(cursor.getDouble(8) != 500.0 && cursor.getDouble(9) != 500.0) {
				menuItems = new String[] {
						"Recharge", "Remove", "Google Maps", "Ingress Intel"
				};
			}
			else {
				menuItems = new String[] {
						"Recharge", "Remove"
				};
			}
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int position = item.getItemId();

		ListView listView = (ListView) this.getView().findViewById(R.id.listView1Check);
		Cursor cursor = (Cursor) listView.getItemAtPosition(info.position);

		double lat = cursor.getDouble(8);
		double lon = cursor.getDouble(9);
		Intent browserIntent;
		
		int id = Integer.valueOf(cursor.getString(0));
		switch(position) {
			case 0:
				Calendar cal = Calendar.getInstance(TimeZone.getDefault());
				Portal p = new Portal(cursor.getString(1), cursor.getString(2), DateFormat.format("yyyy-MM-dd HH:mm:ss", cal.getTime()).toString(), cursor.getDouble(0),cursor.getDouble(1));
				db.deletePortal(id);
				db.addPortal(p);
				break;
			case 1:
				db.deletePortal(Integer.valueOf(cursor.getString(0)));
				break;
			case 2:
				browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + lat + "," + lon));
				startActivity(browserIntent);
				break;
			case 3:
				browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ingress.com/intel?ll=" + lat + "," + lon + "&z=17"));
				startActivity(browserIntent);
				break;
		}
		refreshList();

		return true;
	}
}
