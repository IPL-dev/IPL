package com.ingress.portal.log;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingsFragment extends Fragment{
		
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
        	// Inflate the layout for this fragment
			View v = inflater.inflate(R.layout.fragment_settings, container, false);
			
			Switch s = (Switch) v.findViewById(R.id.switch1);

			s.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					MainActivity.savePos = isChecked;
				}
			});
			
	        return v;
	    }
}
