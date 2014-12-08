package com.ingress.portal.log;

import java.util.Calendar;



import java.util.TimeZone;
import com.ingress.portal.log.android.sqlite.MySQLiteHelper;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MyDialog extends Activity {

	
	public void CapturePortal(String name, String date) {
		MySQLiteHelper db = new MySQLiteHelper(this);
		
		db.addPortal(new Portal(name, date, date));
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
			Toast.makeText(getApplicationContext(), "Portal \"" + value + "\" captured " + DateFormat.format("dd/MM/yyyy", cal.getTime()) + " at " + DateFormat.format("HH:mm:ss", cal.getTime()), Toast.LENGTH_SHORT).show();
			CapturePortal(value, DateFormat.format("yyyy-MM-dd HH:mm:ss", cal.getTime()).toString());
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
