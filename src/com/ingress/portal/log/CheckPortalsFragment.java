package com.ingress.portal.log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.ingress.portal.log.android.sqlite.MySQLiteHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ExpandableListView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;


public class CheckPortalsFragment extends Fragment{

	public SimpleCursorAdapter dataAdapter;
	static public boolean active = false;
	static public CheckPortalsFragment activeFrag;
	static public int SortOrder = 0;
	static public String Group = "";
	static public boolean outdated = false;
	static public int list = 0;

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
		getActivity().unregisterForContextMenu((ListView) this.getView().findViewById(R.id.listView1Check));
	}

	public void refreshList(){
		displayResultList(this.getView());
	}

	public void displayResultList(View v) {

		MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
		if(!db.isUpdated()) {
			db.Upgrade();
		}

		Cursor cursor = null;
		switch(list) {
			case 0:
				cursor = db.getAllPortals(SortOrder);
				break;
			case 1:
				Toast.makeText(getActivity().getApplicationContext(), "GRUPO", Toast.LENGTH_LONG).show();
				cursor = db.getGroupPortals(Group, SortOrder);
				break;
		}
		
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
			menu.add(Menu.NONE, 5, 5, "Add to Group");
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
	
	public void openGroupInsert(final Spinner parent) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		
		alert.setTitle("Insert Group");
		alert.setMessage("Group Name");

		// Set an EditText view to get user input 
		final EditText input = new EditText(getActivity());
		final MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
		
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
		MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
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
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lista);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		if(item.compareTo("") != 0)
			it += 3;
		sp.setSelection(it);
	}
	
	public void addPortalToGroup(int idp, String nameg) {
		
		MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
		int idg = db.getGroup(nameg);
		db.addPortalGroup(idp, idg);
		return;
	}
	
	public void openGroupDialog(final int idp) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		
		alert.setTitle("Insert Group");
		alert.setMessage("Group Name");

		// Set an EditText view to get user input 
		final Spinner input = new Spinner(getActivity());
		
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

		final Activity parent = getActivity();
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getSelectedItem().toString();
				Toast.makeText(parent, "TESTE: " + value, Toast.LENGTH_LONG).show();
				if((value.compareTo("All") != 0) && (value.compareTo("") != 0) && (value.compareTo("New Group...") != 0)) {
					addPortalToGroup(idp, input.getSelectedItem().toString());
				}
				refreshList();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();
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
		Cursor c;
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
		case 5:
			openGroupDialog(p.getId());
			break;
		}
		refreshList();

		return true;
	}
}
