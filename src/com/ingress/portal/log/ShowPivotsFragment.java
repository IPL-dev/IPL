package com.ingress.portal.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.ingress.portal.log.android.sqlite.MySQLiteHelper;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class ShowPivotsFragment extends Fragment{

	public SimpleCursorAdapter dataAdapter;
	static public int SortOrder = 0;
	static public boolean outdated = false;
	public HashMap<Integer, Node> graph;

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void refreshList(){
		displayResultList(this.getView());
	}

	public ArrayList<distPortal> distance(ArrayList<Portal> portals, double medLat, double medLong) {
		int i;
		double dist;
		double[] pos;
		Portal p;
		ArrayList<distPortal> dists = new ArrayList<distPortal>();
		for(i=0;i<portals.size();i++) {
			p = portals.get(i);
			pos = p.getPos();
			dist = Math.pow(pos[0] - medLat, 2) + Math.pow(pos[1] - medLong, 2);
			dists.add(new distPortal(p.getId(), p.getName(), dist, pos, i));
		}

		return dists;
	}

	public int compare3(double a, double b, double c) {
		if(a <= b && a <= c) {
			return 0;
		}
		else if(b <= c) {
			return 1;
		}
		else {
			return 2;
		}
	}

	public ArrayList<Group> calculatePivots(Cursor c) {
		ArrayList<Group> res = new ArrayList<Group>();
		ArrayList<Portal> portals = new ArrayList<Portal>();
		Portal temp;

		double sumLat = 0.0, sumLong = 0.0;
		int n = 0;
		c.moveToFirst();
		while(!c.isAfterLast()) {
			if((c.getDouble(8) != 500.0 && c.getDouble(9) != 500.0) && (c.getDouble(8) != 0.0 && c.getDouble(9) != 0.0)) {
				temp = new Portal(c.getInt(0), c.getString(1), c.getString(3), c.getString(6), c.getString(4), c.getString(7), c.getDouble(8), c.getDouble(9));
				sumLat += c.getDouble(8);
				sumLong += c.getDouble(9);
				n++;
				portals.add(temp);
			}
			c.moveToNext();
		}
		c.close();
		double medLat, medLong;

		medLat = sumLat/n;
		medLong = sumLong/n;
		
		Log.d("PIVOTSMED", medLat + ", " + medLong);

		if(n > 3) {
			ArrayList<distPortal> dists = distance(portals, medLat, medLong);
			Collections.sort(dists);
			
			Log.d("PIVOTS", dists.toString());

			distPortal[] pivots = new distPortal[3];


			pivots[0] = dists.get(0);
			pivots[1] = dists.get(1);
			pivots[2] = dists.get(2);
			
			Log.d("PIVOTS", pivots[0].name + " - " + pivots[1].name + " - " + pivots[2].name);

			portals.remove(pivots[0].index);
			portals.remove(pivots[1].index);
			portals.remove(pivots[2].index);

			ArrayList<distPortal> distsP1 = distance(portals, pivots[0].getPos()[0], pivots[0].getPos()[1]);
			ArrayList<distPortal> distsP2 = distance(portals, pivots[1].getPos()[0], pivots[1].getPos()[1]);
			ArrayList<distPortal> distsP3 = distance(portals, pivots[2].getPos()[0], pivots[2].getPos()[1]);

			Group tempG;

			tempG = new Group(pivots[0].name, pivots[0].id);
			res.add(0, tempG);
			tempG = new Group(pivots[1].name, pivots[1].id);
			res.add(1, tempG);
			tempG = new Group(pivots[2].name, pivots[2].id);
			res.add(2, tempG);

			//graph.add(pivots[0].id + "_" + pivots[1].id);
			Log.d("EDGE", pivots[0].id + "_" + pivots[1].id);
			//graph.add(pivots[0].id + "_" + pivots[2].id);
			Log.d("EDGE", pivots[0].id + "_" + pivots[2].id);
			//graph.add(pivots[1].id + "_" + pivots[2].id);
			Log.d("EDGE", pivots[1].id + "_" + pivots[2].id);
			
			int i;
			int min;
			for(i=0;i<portals.size();i++) {
				min = compare3(distsP1.get(i).getDist(), distsP2.get(i).getDist(), distsP3.get(i).getDist());
				res.get(min).insertChild(distsP1.get(i).name);
				//graph.add(res.get(min).id + "_" + distsP1.get(i).id);
				Log.d("EDGE", res.get(min).id + "_" + distsP1.get(i).id);
			}
		}
		else {
			int i;
			for(i=0;i<portals.size();i++) {
				Group tempG;
				tempG = new Group(portals.get(i).getName(), portals.get(i).getId());
				res.add(tempG);
			}
		}

		return res;
	}
	
	public boolean isCross(Point p, Point a, Point b, Point c) {
		boolean ret = true;
		
		double xa, ya, ka, xb, yb, kb;
		
		xa = p.y - a.y;
		ya = a.x - p.x;
		ka = p.x*a.y - p.y*a.x;
		
		xb = b.y - c.y;
		yb = c.x - b.x;
		kb = b.x*c.y - b.y*c.x;
		
		if((xa*b.x + ya*b.y + ka)*(xa*c.x + ya*c.y + ka) > 0) {
			ret = false;
		}
		else {
			if((xb*p.x + yb*p.y + kb)*(xb*a.x + yb*a.y + kb) > 0) {
				ret = false;
			}
			else {
				ret = true;
			}
		}
		
		return ret;
	}

	public void displayResultList(View v) {

		MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
		if(db.isUpdated()) {
			db.Upgrade();
		}

		Cursor cursor = db.getAllPortals(SortOrder);

		ExpandableListView listView = (ExpandableListView) v.findViewById(R.id.listView1Check);
		//getActivity().registerForContextMenu(listView);
		registerForContextMenu(listView);

		graph = new HashMap<Integer, Node>();
		ArrayList<Group> tempL = calculatePivots(cursor);

		// Assign adapter to ListView		
		//listView.setAdapter(dataAdapter);
		listView.setAdapter(new ExpandableListAdapter2(tempL, getActivity()));
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
	
	public class Node {
		int id;
		ArrayList<Integer> edges;
		
		public Node(int u) {
			this.id = u;
			this.edges = new ArrayList<Integer>();
		}
		
		public void addEdge(int v) {
			edges.add(v);
		}
	}
	
	public class Point {
		public double x, y;
	}
	

	public class distPortal implements Comparable<distPortal>{
		int id;
		String name;
		double dist;
		double[] pos;
		int index;

		public distPortal(int i, String n, double d, double[] p, int ind) {
			this.id = i;
			this.name = n;
			this.dist = d;
			this.pos = p;
			this.index = ind;
		}

		public double getDist() {
			return this.dist;
		}
		
		public double getAng() {
			return Math.atan2(this.pos[1], this.pos[0]);
		}

		public double[] getPos() {
			return this.pos;
		}
		
		@Override
		public String toString() {
			return this.name + " - " + this.dist;
		}

		public int compareTo(distPortal compareDist) {

			double distT = compareDist.getDist();

			if(this.dist > distT) {
				return 1;
			}
			else if (this.dist < distT) {
				return -1;
			}
			else {
				return 0;
			}
		}
	}
}
