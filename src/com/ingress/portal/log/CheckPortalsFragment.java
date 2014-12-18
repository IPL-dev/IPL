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
import android.widget.Toast;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;


public class CheckPortalsFragment extends Fragment{

	public SimpleCursorAdapter dataAdapter;
	static public boolean active = false;
	static public CheckPortalsFragment activeFrag;
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
		/*
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
		ListView listView = (ListView) v.findViewById(R.id.listView1Check);*/
		ExpandableListView listView = (ExpandableListView) v.findViewById(R.id.listView1Check);
		//getActivity().registerForContextMenu(listView);
		registerForContextMenu(listView);

		// Assign adapter to ListView		
		//listView.setAdapter(dataAdapter);
		listView.setAdapter(new ExpandableListAdapter(cursor, getActivity()));
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

		try {
			Boolean Resultado = new CheckVersion().execute(MainActivity.getVersion()).get();
			if(Resultado) {
				Toast.makeText(getActivity().getApplicationContext(), "Nova versão disponível, baixando APK atualizado", Toast.LENGTH_LONG).show();
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/IPL-dev/IPL/blob/master/bin/IPL.apk?raw=true"));
				startActivity(browserIntent);
			}
			else if (outdated){
				Toast.makeText(getActivity().getApplicationContext(), "Sem conexão com a internet", Toast.LENGTH_LONG).show();
				outdated = false;
			}
			else {
				Toast.makeText(getActivity().getApplicationContext(), "Versão atualizada", Toast.LENGTH_LONG).show();
			}
		} catch (Exception ex) {
		}
		
		return v;
	}

	private double[] getGPS() {
		LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);  
		//List<String> providers = lm.getProviders(true);

		/* Loop over the array backwards, and if you get an accurate location, then break                 out the loop*/
		Location l = null;
		/*
		for (int i=providers.size()-1; i>=0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null) break;
		}*/

		l = lm.getLastKnownLocation("gps");

		double[] gps = new double[2];
		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		return gps;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==R.id.listView1Check) {
			ExpandableListView listView = (ExpandableListView) v.findViewById(R.id.listView1Check);
			ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo)menuInfo;
			int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);

			Portal p = (Portal) listView.getExpandableListAdapter().getGroup(group);

			menu.setHeaderTitle(p.getName());
			String[] menuItems;
			if((p.getPos()[0] != 500.0 && p.getPos()[1] != 500.0) && (p.getPos()[0] != 0.0 && p.getPos()[1] != 0.0)) {
				menuItems = new String[] {
						"Recharge", "Remove", "Google Maps", "Ingress Intel", 
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

			if(MainActivity.savePos) {
				menu.add(Menu.NONE, 4, 4, "Save current position"); 
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
		//AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = item.getItemId();

		ExpandableListView listView = (ExpandableListView) this.getView().findViewById(R.id.listView1Check);
		//Cursor cursor = (Cursor) listView.getItemAtPosition(info.position);

		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo)item.getMenuInfo();
		int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);

		Portal p = (Portal) listView.getExpandableListAdapter().getGroup(group);

		double lat = p.getPos()[0];
		double lon = p.getPos()[1];
		Intent browserIntent;
		Portal temp;

		int id = p.getId();
		switch(position) {
		case 0:
			Calendar cal = Calendar.getInstance(TimeZone.getDefault());
			temp = new Portal(p.getName(), formatDate(p.getDate()), DateFormat.format("yyyy-MM-dd HH:mm:ss", cal.getTime()).toString(), lat, lon);
			db.deletePortal(id);
			db.addPortal(temp);
			break;
		case 1:
			db.deletePortal(p.getId());
			break;
		case 2:
			browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + lat + "," + lon));
			startActivity(browserIntent);
			break;
		case 3:
			browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ingress.com/intel?ll=" + lat + "," + lon + "&z=17"));
			startActivity(browserIntent);
			break;
		case 4:
			double[] pos = getGPS(); 
			if(pos[0] == 0.0 && pos[1] == 0.0) {
				Toast.makeText(getActivity().getApplicationContext(), "Location could not be determined", Toast.LENGTH_LONG).show();
			}
			else {
				temp = new Portal(p.getName(), formatDate(p.getDate()), formatDate(p.getRecharge()), pos[0], pos[1]);
				db.deletePortal(id);
				db.addPortal(temp);
				Toast.makeText(getActivity().getApplicationContext(), "Location updated", Toast.LENGTH_LONG).show();
			}
			break;
		}
		refreshList();

		return true;
	}
}
