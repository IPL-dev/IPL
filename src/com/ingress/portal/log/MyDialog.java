package com.ingress.portal.log;

import java.util.Calendar;
import java.util.List;



import java.util.TimeZone;
import com.ingress.portal.log.android.sqlite.MySQLiteHelper;
import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MyDialog extends Activity {


	public void CapturePortal(String name, String date, double lat, double lon) {
		MySQLiteHelper db = new MySQLiteHelper(this);

		db.addPortal(new Portal(name, date, date, lat, lon));
	}

	private double[] getGPS() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Insert Portal");
		alert.setMessage("Portal Name");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				Calendar cal = Calendar.getInstance(TimeZone.getDefault());
				double[] pos = getGPS();
				if(MainActivity.savePos && (pos[0] != 0.0 && pos[1] != 0.0)) {
					pos = getGPS();
					Toast.makeText(getApplicationContext(), "Portal \"" + value + "\" captured " + DateFormat.format("dd/MM/yyyy", cal.getTime()) + " at " + DateFormat.format("HH:mm:ss", cal.getTime()) + " in position: " + pos[0] + " - " + pos[1], Toast.LENGTH_LONG).show();
				}
				else if(pos[0] == 0.0 && pos[1] == 0.0) {
					Toast.makeText(getApplicationContext(), "Portal \"" + value + "\" captured " + DateFormat.format("dd/MM/yyyy", cal.getTime()) + " at " + DateFormat.format("HH:mm:ss", cal.getTime()) + ", but location could not be determined", Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getApplicationContext(), "Portal \"" + value + "\" captured " + DateFormat.format("dd/MM/yyyy", cal.getTime()) + " at " + DateFormat.format("HH:mm:ss", cal.getTime()), Toast.LENGTH_LONG).show();
				}
				CapturePortal(value, DateFormat.format("yyyy-MM-dd HH:mm:ss", cal.getTime()).toString(), pos[0], pos[1]);
				if(CheckPortalsFragment.active) {
					CheckPortalsFragment f = CheckPortalsFragment.activeFrag;
					f.displayResultList(f.getView());
				}
				finish();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		});

		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_dialog, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
