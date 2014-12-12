package com.ingress.portal.log;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingsFragment extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("CREATE", "CRIOU");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_settings);
				
		Switch s = (Switch) this.findViewById(R.id.switch1);
		
		s.setChecked(MainActivity.savePos);

		s.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				MainActivity.savePos = isChecked;
			}
		});
	}
	
	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getTitle());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main3, menu);
		//restoreActionBar();
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch(id) {
			case(R.id.returnSettings):
				finish();
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
