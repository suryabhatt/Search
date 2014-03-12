package com.example.googlemystuff.db_helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.googlemystuff.beans.Item;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	// Database Version
    private static final int DB_VERSION = 1;
    
    // Database Name
    private static final String DB_NAME = "ItemDB";
    
    Context context;
	
	public MyDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		 
	}
	
	 // Items table name
    private static final String TABLE_ITEM = "items";    

    // Items Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_IMAGE = "image";

    private static final String[] COLUMNS = {KEY_ID,KEY_NAME,KEY_LOCATION,KEY_IMAGE};

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String CREATE_ITEM_TABLE = "CREATE TABLE "+ TABLE_ITEM +"( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "name TEXT, "+
                "location TEXT, " +
                "image BLOB )";
	 
		db.execSQL(CREATE_ITEM_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_ITEM);
		 
        // create fresh Items table
        this.onCreate(db);

	}
	
	public void addItem(Item item){
        
//		get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		
		// create ContentValues to add key "column"/value
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, item.getName()); 
		values.put(KEY_LOCATION, item.getLocation());
		values.put(KEY_IMAGE, convertBitmapToByteArray(item.getImage()));
		
		// insert
		long insert = db.insert(TABLE_ITEM, null, values); 
		if(insert == -1){
			Toast.makeText(context, "Failed to save to the database", Toast.LENGTH_LONG);
		}
		// close
		db.close(); 
	}
	
	 public Item getItemById(int id){
		 
	        // 1. get reference to readable DB
	        SQLiteDatabase db = this.getReadableDatabase();
	 
	        // 2. build query
	        Cursor cursor = 
	                db.query(TABLE_ITEM,
	                COLUMNS,
	                " id = ?",  
	                new String[] { String.valueOf(id) }, 
	                null, 
	                null, 
	                null, 
	                null);
	 
	        // 3. if we got results get the first one
	        if (cursor != null)
	            cursor.moveToFirst();
	 
	        // 4. build Item object
	        Item item = new Item();
	        
	        item.setId(Integer.parseInt(cursor.getString(0)));
	        item.setName(cursor.getString(1));
	        item.setLocation(cursor.getString(2));
	        item.setImage(convertByteArrayToBitmap(cursor.getBlob(3)));
	         
	        
	        return item;
	    }
	 
	 public Item getItemByName(String name){
		 
	        // 1. get reference to readable DB
	        SQLiteDatabase db = this.getReadableDatabase();
	 
	        // 2. build query
	        Cursor cursor = 
	                db.query(TABLE_ITEM,
	                COLUMNS,
	                " name = ?",  
	                new String[]{name}, 
	                null, 
	                null, 
	                null, 
	                null);
	 
	        // 3. if we got results get the first one
	        if (cursor != null)
	            cursor.moveToFirst();
	 
	        // 4. build Item object
	        Item item = new Item();
	        
	        item.setId(Integer.parseInt(cursor.getString(0)));
	        item.setName(cursor.getString(1));
	        item.setLocation(cursor.getString(2));
	        item.setImage(convertByteArrayToBitmap(cursor.getBlob(3)));
	         
	        
	        return item;
	    }
	 
	  
	    // Get All Items
	    public List<Item> getAllItems(String search_key) {
	        List<Item> items = new ArrayList<Item>();
	 
	        //  build the query
	        String query = "SELECT  * FROM " + TABLE_ITEM + " WHERE name LIKE '" + search_key + "%' " ;
	 
	        //  get reference to writable DB
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(query, null);
	 
	        // 3. go over each row, build Item and add it to list
	        Item item = null;
	        if (cursor.moveToFirst()) {
	            do {
	                item = new Item();
	                item.setId(Integer.parseInt(cursor.getString(0)));
	                item.setName(cursor.getString(1));
	                item.setLocation(cursor.getString(2));
	                item.setImage(convertByteArrayToBitmap(cursor.getBlob(3)));
	 
	                // Add Item to Items
	                items.add(item);
	            } while (cursor.moveToNext());
	        }
	 
	        
	 
	        // return Items
	        return items;
	    }
	 
	     // Updating single Item
	    public Item updateItem(Item item) {
	 
	    	SQLiteDatabase db = this.getWritableDatabase();
			
			// create ContentValues to add key "column"/value
			ContentValues values = new ContentValues();
			values.put(KEY_NAME, item.getName()); 
			values.put(KEY_LOCATION, item.getLocation());
			values.put(KEY_IMAGE, convertBitmapToByteArray(item.getImage()));

			
//			String query = "UPDATE" + TABLE_ITEM + "SET name = '" + item.getName()
//				+ ", location = '" + item.getLocation() + ", image = "
//				+ item.getImage() + " WHERE id = " + item.getId();
			
			// insert
			int i = db.update(TABLE_ITEM, values, KEY_ID+"= ?", new String[]{String.valueOf(item.getId())} );
			
			Log.d("number of rows updated:", i+"");
			// close
			db.close();
			
	       return null;
	 
	    }
	 
	    // Deleting single Item
	    public int deleteItemById(String itemId) {
	 
	        // 1. get reference to writable DB
	        SQLiteDatabase db = this.getWritableDatabase();
	 
	        // 2. delete
	        int result = db.delete(TABLE_ITEM,
	                KEY_ID+" = ?",
	                new String[] { itemId });
	 
	        // 3. close
	        db.close();	 
	        return result;
	 
	    }
	
	public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		} else {
			byte[] b = null;
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 0, byteArrayOutputStream);
				b = byteArrayOutputStream.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return b;
		}
	}

	public static Bitmap convertByteArrayToBitmap(byte[] b){
		if(b == null){
			return null;
		}
		
		ByteArrayInputStream imageStream = new ByteArrayInputStream(b);
		Bitmap theImage = BitmapFactory.decodeStream(imageStream);
		return theImage;
	}

}
