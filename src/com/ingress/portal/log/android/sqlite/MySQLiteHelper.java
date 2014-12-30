package com.ingress.portal.log.android.sqlite;
 
import java.util.ArrayList;

import com.ingress.portal.log.Portal;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class MySQLiteHelper extends SQLiteOpenHelper {
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "PortalDB";
 
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PORTAL_TABLE = "CREATE TABLE portals ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "name TEXT, " +
                "date DATETIME, " +
                "recharge DATETIME," +
                "latitude REAL," +
                "longitude REAL)";

        db.execSQL(CREATE_PORTAL_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS portals");
        this.onCreate(db);
    }
 
    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */
 
    // Books table name
    private static final String TABLE_PORTALS = "portals";
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_PORTAL_GROUPS = "portalgroups";
 
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_RECHARGE = "recharge";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    
    private static final String KEY_ID_PORTAL = "portal_id";
    private static final String KEY_ID_GROUP = "group_id";
 
    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_DATE, KEY_RECHARGE, KEY_LATITUDE, KEY_LONGITUDE};
    private static final String[] COLUMNS_OLD = {KEY_ID, KEY_NAME, KEY_DATE, KEY_RECHARGE};
    
    private static final String[] COLUMNS_GROUPS = {KEY_ID, KEY_NAME};
    
    private static final String[] COLUMNS_PORTAL_GROUPS = {KEY_ID, KEY_ID_PORTAL, KEY_ID_GROUP};
    
    public boolean isUpdated() {
    	boolean ret = false;
    	
    	if(COLUMNS.length != this.getReadableDatabase().rawQuery("SELECT id FROM portals", null).getColumnCount()) {
    		ret = true;
    	}
    	
    	Log.d("UP", "PASSED UPDATE " + ret);
    	
    	return ret;
    }
    
    public Cursor getGroups() {
    	SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id as _id, name FROM groups ORDER BY name ASC", null);
        if(cursor != null)
        	cursor.moveToFirst();
        
        return cursor;
    }
    
    public Cursor getGroupPortals(String group, int order) {
        String query = "SELECT P.id as _id, P.name, date, strftime('%d/%m/%Y  -  %H:%M:%S', date) AS dateF, CAST((julianday('now') - julianday(date)) AS INTEGER) AS days, recharge, strftime('%d/%m/%Y  -  %H:%M:%S', recharge) AS rechargeF, CAST((julianday('now') - julianday(recharge)) AS INTEGER) AS recharges, latitude, longitude FROM " + TABLE_PORTALS +
        		" AS P, " + TABLE_GROUPS + " AS G, " + TABLE_PORTAL_GROUPS + " AS GP WHERE GP.group_id = G.id AND GP.portal_id = P.id AND G.name = \"" + group + "\"";
        
        switch(order) {
        	case 0:
        		query += " ORDER BY P.name";
        		break;
        	case 1:
        		query += " ORDER BY julianday(date) ASC";
        		break;
        	case 2:
        		query += " ORDER BY julianday(date) DESC";
        		break;
        	case 3:
        		query += " ORDER BY julianday(recharge) ASC";
        		break;
        }
        		
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null)
        	cursor.moveToFirst();
        return cursor;
    }
    
    public long addGroup(String name) {
    	
    	ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);

        long err = this.getWritableDatabase().insert(TABLE_GROUPS,
                null,
                values);
    	
        return err;
    }
    
    public int getGroup(String name) {
    	SQLiteDatabase db = this.getReadableDatabase();
    	 
        Cursor cursor = db.rawQuery("SELECT id FROM groups WHERE name = \"" + name + "\"", null);
        
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    
    public void deleteGroup(int id) {
    	 
        SQLiteDatabase db = this.getWritableDatabase();
 
        db.delete(TABLE_GROUPS,
                KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        
        db.delete(TABLE_PORTAL_GROUPS,
                KEY_ID_GROUP + " = ?",
                new String[] { String.valueOf(id) });
 
        db.close();
    }
    
    public boolean addPortalGroup(int portalID, int groupID) {
    	
    	ContentValues values = new ContentValues();
        values.put(KEY_ID_PORTAL, portalID);
        values.put(KEY_ID_GROUP, groupID);

        long err = this.getWritableDatabase().insert(TABLE_PORTAL_GROUPS,
                null,
                values);
    	
        if(err == -1)
        	return false;
        else
        	return true;
    }
    
    public void createGroups() {
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	db.execSQL("DROP TABLE IF EXISTS groups");
    	db.execSQL("DROP TABLE IF EXISTS portalgroups");
    	
    	String CREATE_GROUP_TABLE = "CREATE TABLE groups ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "name TEXT UNIQUE)";
    	db.execSQL(CREATE_GROUP_TABLE);
    	
    	CREATE_GROUP_TABLE = "CREATE TABLE portalgroups ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "portal_id INTEGER, " +
                "group_id INTEGER)";
    	db.execSQL(CREATE_GROUP_TABLE);
    	
    	db.close();
    }
    
    public void Upgrade() {
    	SQLiteDatabase db = this.getReadableDatabase();
    	ArrayList<Portal> l = new ArrayList<Portal>();
    	
    	String query = "SELECT * FROM portals";
    	Cursor c = db.rawQuery(query, null);
    	c.moveToFirst();
    	while(!c.isAfterLast()) {
    		Log.d("SIZE", getPortalPure(c.getInt(0)).toString());
    		l.add(getPortalPure(c.getInt(0)));
    		c.moveToNext();
    	}
    	
    	db.execSQL("DROP TABLE IF EXISTS portals");
    	
    	String CREATE_PORTAL_TABLE = "CREATE TABLE portals ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "name TEXT, " +
                "date DATETIME, " +
                "recharge DATETIME," +
                "latitude REAL," +
                "longitude REAL)";
    	db.execSQL(CREATE_PORTAL_TABLE);
    	
    	int i;
    	for(i=0;i<l.size();i++) {
    		addPortal(l.get(i));
    	}
    }
 
    public void addPortal(Portal p){
        Log.d("addBook", p.toString());
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, p.getName());
        values.put(KEY_DATE, p.getDate());
        values.put(KEY_RECHARGE, p.getRecharge());
        values.put(KEY_LATITUDE, p.getPos()[0]);
        values.put(KEY_LONGITUDE, p.getPos()[1]);
 
        db.insert(TABLE_PORTALS,
                null,
                values);
 
        db.close(); 
    }
    
    public Portal getPortalPure(int id){
    	 
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = 
                db.query(TABLE_PORTALS,
                COLUMNS_OLD,
                " id = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
 
        if (cursor != null)
            cursor.moveToFirst();
 
        Portal p = new Portal(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), 500.0, 500.0);
 
        return p;
    }
 
    public Portal getPortal(int id){
 
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = 
                db.query(TABLE_PORTALS,
                COLUMNS,
                " id = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
 
        if (cursor != null)
            cursor.moveToFirst();
 
        Portal book = new Portal(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(0), cursor.getDouble(1));

        return book;
    }
    
    public Cursor getAllPortals(int order) {
        String query = "SELECT id as _id, name, date, strftime('%d/%m/%Y  -  %H:%M:%S', date) AS dateF, CAST((julianday('now') - julianday(date)) AS INTEGER) AS days, recharge, strftime('%d/%m/%Y  -  %H:%M:%S', recharge) AS rechargeF, CAST((julianday('now') - julianday(recharge)) AS INTEGER) AS recharges, latitude, longitude FROM " + TABLE_PORTALS;
        
        switch(order) {
        	case 0:
        		query += " ORDER BY name";
        		break;
        	case 1:
        		query += " ORDER BY julianday(date) ASC";
        		break;
        	case 2:
        		query += " ORDER BY julianday(date) DESC";
        		break;
        	case 3:
        		query += " ORDER BY julianday(recharge) ASC";
        		break;
        }
        		
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null)
        	cursor.moveToFirst();
        return cursor;
    }
 
    public int updatePortal(Portal p) {
 
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put("name", p.getName());
        values.put("date", p.getDate());
        values.put("recharge", p.getRecharge());
 
        String query = "UPDATE portals SET recharge = '" + p.getRecharge() + "' WHERE " + KEY_ID + " = ?" + p.getId();
      
        db.execSQL(query);

        db.close();
 
        return 0;
 
    }


    public void deletePortal(int id) {

        SQLiteDatabase db = this.getWritableDatabase(); 
        
        db.delete(TABLE_PORTALS,
                KEY_ID+" = ?",
                new String[] { String.valueOf(id) });
        
        db.delete(TABLE_PORTAL_GROUPS,
                KEY_ID_PORTAL + " = ?",
                new String[] { String.valueOf(id) });

        db.close();
 
        Log.d("deleteBook", String.valueOf(id));
 
    }
}