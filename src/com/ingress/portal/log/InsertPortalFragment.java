package com.ingress.portal.log;

import java.util.Calendar;


import com.ingress.portal.log.android.sqlite.MySQLiteHelper;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class InsertPortalFragment extends Fragment{
		
	public void clearForms(EditText et, DatePicker dp, TimePicker tp) {
		Calendar cal=Calendar.getInstance();
		
		et.setText("");

		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		int day=cal.get(Calendar.DAY_OF_MONTH);
		int hour=cal.get(Calendar.HOUR_OF_DAY);
		int minute=cal.get(Calendar.MINUTE);

		dp.updateDate(year, month, day);

		tp.setCurrentHour(hour);
		tp.setCurrentMinute(minute);
		
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
        	// Inflate the layout for this fragment
			View v = inflater.inflate(R.layout.insert_portal, container, false); 

			Button b = (Button) v.findViewById(R.id.insertPortal);
			final EditText et = (EditText) v.findViewById(R.id.editTextInsert);
			final DatePicker dp = (DatePicker) v.findViewById(R.id.datePicker1);
			final TimePicker tp = (TimePicker) v.findViewById(R.id.timePicker1);
			
			tp.setIs24HourView(true);
			
			b.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
					
					int day = dp.getDayOfMonth();
					int month = dp.getMonth() + 1;
					int year = dp.getYear();
					int hour = tp.getCurrentHour();
					int minute = tp.getCurrentMinute();
					String date = String.format("%04d-%02d-%02d %02d:%02d:00", year,month,day,hour,minute);
					String dateF = String.format("%02d/%02d/%04d", day,month,year);
					String timeF = String.format("%02d:%02d:00", hour,minute);
					//String date = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";
					//Toast.makeText(getActivity().getApplicationContext(), "Portal \"" + et.getText()+ "\" captured " + day + "/" + month + "/" + year + " at " + hour + ":" + minute + ":00", Toast.LENGTH_SHORT).show();
					Toast.makeText(getActivity().getApplicationContext(), "Portal \"" + et.getText()+ "\" captured " + dateF + " at " + timeF, Toast.LENGTH_SHORT).show();
					db.addPortal(new Portal(et.getText().toString(), date, date, 500.0, 500.0));
					clearForms(et, dp, tp);
				}
			});
			
			clearForms(et, dp, tp);
			
	        return v;
	    }
	
	
}
