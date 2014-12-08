package com.ingress.portal.log.android.sqlite;
 
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
        // SQL statement to create book table
        String CREATE_PORTAL_TABLE = "CREATE TABLE portals ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "name TEXT, " +
                "date DATETIME, " +
                "recharge DATETIME)";
 
        // create books table
        db.execSQL(CREATE_PORTAL_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS portals");
 
        // create fresh books table
        this.onCreate(db);
    }
 
    
    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */
 
    // Books table name
    private static final String TABLE_PORTALS = "portals";
 
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_RECHARGE = "recharge";
 
    private static final String[] COLUMNS = {KEY_ID,KEY_NAME,KEY_DATE, KEY_RECHARGE};
 
    public void addPortal(Portal p){
        Log.d("addBook", p.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, p.getName()); // get title 
        values.put(KEY_DATE, p.getDate()); // get author
        values.put(KEY_RECHARGE, p.getRecharge()); // get author
 
        // 3. insert
        db.insert(TABLE_PORTALS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        db.close(); 
    }
 
    public Portal getPortal(int id){
 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
 
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_PORTALS, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections 
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
 
        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
 
        // 4. build book object
        Portal book = new Portal(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
 
        Log.d("getBook("+String.valueOf(id)+")", book.toString());
 
        // 5. return book
        return book;
    }
    
    // Get All Books
    public Cursor getAllPortals(int order) {
        // 1. build the query
        String query = "SELECT id as _id, name, date, strftime('%d/%m/%Y  -  %H:%M:%S', date) AS dateF, CAST((julianday('now') - julianday(date)) AS INTEGER) AS days, recharge, strftime('%d/%m/%Y  -  %H:%M:%S', recharge) AS rechargeF, CAST((julianday('now') - julianday(recharge)) AS INTEGER) AS recharges FROM " + TABLE_PORTALS;
        
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
        		
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null)
        	cursor.moveToFirst();
        return cursor;
    }
 
     // Updating single book
    public int updatePortal(Portal p) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("name", p.getName()); // get title 
        values.put("date", p.getDate()); // get author
        values.put("recharge", p.getRecharge()); // get author
 
        // 3. updating row
        /*int i = db.update(TABLE_PORTALS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(p.getId()) }); //selection args*/
        String query = "UPDATE portals SET recharge = '" + p.getRecharge() + "' WHERE " + KEY_ID + " = ?" + p.getId();
      
        db.execSQL(query);
        
        // 4. close
        db.close();
 
        return 0;
 
    }
 
    // Deleting single book
    public void deletePortal(int id) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_PORTALS,
                KEY_ID+" = ?",
                new String[] { String.valueOf(id) }); //selection args
 
        // 3. close
        db.close();
 
        Log.d("deleteBook", String.valueOf(id));
 
    }
}