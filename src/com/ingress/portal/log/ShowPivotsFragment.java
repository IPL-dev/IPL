package com.ingress.portal.log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
import android.os.PowerManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateFormat;
import android.util.Log;
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

public class ShowPivotsFragment extends Fragment{

	public SimpleCursorAdapter dataAdapter;
	static public int SortOrder = 0;
	static public boolean outdated = false;

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void refreshList(){
		displayResultList(this.getView());
	}

	public ArrayList<distPortal> distance(ArrayList<Portal> portals, double medLat, double medLong) {
		int i;
		double dist;
		double[] pos;
		Portal p;
		ArrayList<distPortal> dists = new ArrayList<distPortal>();
		for(i=0;i<portals.size();i++) {
			p = portals.get(i);
			pos = p.getPos();
			dist = Math.pow(medLat - pos[0], 2) + Math.pow(medLong - pos[1], 2);
			dists.add(new distPortal(p.getId(), p.getName(), dist, pos, i));
		}

		return dists;
	}

	public int compare3(double a, double b, double c) {
		if(a <= b && a <= c) {
			return 0;
		}
		else if(b <= c) {
			return 1;
		}
		else {
			return 2;
		}
	}

	public ArrayList<Group> calculatePivots(Cursor c) {
		ArrayList<Group> res = new ArrayList<Group>();
		ArrayList<Portal> portals = new ArrayList<Portal>();
		Portal temp;

		double sumLat = 0.0, sumLong = 0.0;
		int n = 0;
		c.moveToFirst();
		while(!c.isAfterLast()) {
			temp = new Portal(c.getInt(0), c.getString(1), c.getString(3), c.getString(6), c.getString(4), c.getString(7), c.getDouble(8), c.getDouble(9));
			sumLat += c.getDouble(8);
			sumLong += c.getDouble(9);
			n++;
			portals.add(temp);
			c.moveToNext();
		}
		c.close();
		double medLat, medLong;

		medLat = sumLat/n;
		medLong = sumLong/n;
		
		Log.d("PIVOTSMED", medLat + ", " + medLong);

		if(n > 3) {
			ArrayList<distPortal> dists = distance(portals, medLat, medLong);
			Collections.sort(dists);
			
			Log.d("PIVOTS", dists.toString());

			distPortal[] pivots = new distPortal[3];


			pivots[0] = dists.get(0);
			pivots[1] = dists.get(1);
			pivots[2] = dists.get(2);
			
			Log.d("PIVOTS", pivots[0].name + " - " + pivots[1].name + " - " + pivots[2].name);

			portals.remove(pivots[0].index);
			portals.remove(pivots[1].index);
			portals.remove(pivots[2].index);

			ArrayList<distPortal> distsP1 = distance(portals, pivots[0].getPos()[0], pivots[0].getPos()[1]);
			ArrayList<distPortal> distsP2 = distance(portals, pivots[1].getPos()[0], pivots[1].getPos()[1]);
			ArrayList<distPortal> distsP3 = distance(portals, pivots[2].getPos()[0], pivots[2].getPos()[1]);

			Group tempG;

			tempG = new Group(pivots[0].name);
			res.add(0, tempG);
			tempG = new Group(pivots[1].name);
			res.add(1, tempG);
			tempG = new Group(pivots[2].name);
			res.add(2, tempG);


			int i;
			int min;
			for(i=0;i<portals.size();i++) {
				min = compare3(distsP1.get(i).getDist(), distsP2.get(i).getDist(), distsP3.get(i).getDist());
				
				Log.d("PIVOTS", "P1: " + distsP1.get(i).getDist() + " P2: " + distsP2.get(i).getDist() + " P3: " + distsP3.get(i).getDist() + " MIN: " + min);

				res.get(min).insertChild(distsP1.get(i).name);
			}
		}
		else {
			int i;
			for(i=0;i<portals.size();i++) {
				Group tempG;
				tempG = new Group(portals.get(i).getName());
				res.add(tempG);
			}
		}

		return res;
	}

	public void displayResultList(View v) {

		MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
		if(db.isUpdated()) {
			db.Upgrade();
		}

		Cursor cursor = db.getAllPortals(SortOrder);

		ExpandableListView listView = (ExpandableListView) v.findViewById(R.id.listView1Check);
		//getActivity().registerForContextMenu(listView);
		registerForContextMenu(listView);

		ArrayList<Group> tempL = calculatePivots(cursor);

		// Assign adapter to ListView		
		//listView.setAdapter(dataAdapter);
		listView.setAdapter(new ExpandableListAdapter2(tempL, getActivity()));
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

	public class distPortal implements Comparable<distPortal>{
		int id;
		String name;
		double dist;
		double[] pos;
		int index;

		public distPortal(int i, String n, double d, double[] p, int ind) {
			this.id = i;
			this.name = n;
			this.dist = d;
			this.pos = p;
			this.index = ind;
		}

		public double getDist() {
			return this.dist;
		}

		public double[] getPos() {
			return this.pos;
		}
		
		@Override
		public String toString() {
			return this.name + " - " + this.dist;
		}

		public int compareTo(distPortal compareDist) {

			double distT = compareDist.getDist();

			if(this.dist > distT) {
				return 1;
			}
			else if (this.dist < distT) {
				return -1;
			}
			else {
				return 0;
			}
		}
	}
}
