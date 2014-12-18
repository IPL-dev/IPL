package com.ingress.portal.log;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inf;
    private ArrayList<Portal> portals;

    public ExpandableListAdapter(Cursor c, Activity act) {
    	Portal temp;
    	
    	this.portals = new ArrayList<Portal>();
    	
    	c.moveToFirst();
        while(!c.isAfterLast()) {
        	temp = new Portal(c.getInt(0), c.getString(1), c.getString(3), c.getString(6), c.getString(4), c.getString(7), c.getDouble(8), c.getDouble(9));
        	
            portals.add(temp);
            c.moveToNext();
        }
        c.close();
    	
        inf = LayoutInflater.from(act);
    }

    @Override
    public int getGroupCount() {
        return portals.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return portals.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return portals.get(groupPosition).getChildren().get(childPosition);
    }
    
    public Object getChild(int groupPosition) {
        return portals.get(groupPosition).getChildren();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            convertView = inf.inflate(R.layout.listrow_details3, parent, false);
            
            TextView date = (TextView) convertView.findViewById(R.id.date);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView recharge = (TextView) convertView.findViewById(R.id.recharge);
            TextView decay = (TextView) convertView.findViewById(R.id.decayed);

            date.setText(getChild(groupPosition, 0).toString());
            time.setText(getChild(groupPosition, 1).toString());
            recharge.setText(getChild(groupPosition, 2).toString());
            decay.setText(getChild(groupPosition, 3).toString());
            
            return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = inf.inflate(R.layout.listrow_group, parent, false);

        TextView text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText(((Portal)getGroup(groupPosition)).getName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}