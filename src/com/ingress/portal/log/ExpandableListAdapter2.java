package com.ingress.portal.log;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter2 extends BaseExpandableListAdapter {

    private final LayoutInflater inf;
    private ArrayList<Group> groups;

    public ExpandableListAdapter2(ArrayList<Group> l, Activity act) {
    	this.groups = l;
    	
        inf = LayoutInflater.from(act);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getChildren().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getChildren().get(childPosition);
    }
    
    public Object getChild(int groupPosition) {
        return groups.get(groupPosition).getChildren();
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

            convertView = inf.inflate(R.layout.listrow_single, parent, false);
            
            TextView name = (TextView) convertView.findViewById(R.id.textView1);
            name.setText(getChild(groupPosition, childPosition).toString());
            
            return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = inf.inflate(R.layout.listrow_group, parent, false);

        TextView text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText(((Group)getGroup(groupPosition)).getGroup());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}