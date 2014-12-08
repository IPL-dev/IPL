package com.ingress.portal.log;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment{
		
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
        	// Inflate the layout for this fragment
			View v = inflater.inflate(R.layout.fragment_settings, container, false); 

	        return v;
	    }
}
