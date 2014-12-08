package com.ingress.portal.log;

import com.ingress.portal.log.android.sqlite.MySQLiteHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MyDialog2 extends Activity {

	public void LosePortal(int id) {
		MySQLiteHelper db = new MySQLiteHelper(this);
		
		db.deletePortal(id);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_dialog2);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Remove Portal");
		alert.setMessage("Are you sure?");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			//String value = input.getText().toString();
			int value = getIntent().getIntExtra("PORTAL_NAME", -1);
			LosePortal(Integer.valueOf(value));
			Intent intent=new Intent();
            setResult(RESULT_OK, intent);
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
		getMenuInflater().inflate(R.menu.my_dialog2, menu);
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
