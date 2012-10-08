/**
 * 
 */
package ua.stldev.pharm;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * @author sergey
 */
public class DatabaseWrapper extends SQLiteOpenHelper {

	public static final String COL_ID 		= "_id";
	public static final String COL_TITLE 		= "title";
	public static final String COL_GROUPS 	= "groups";
	public static final String COL_SHELF 		= "shelf";
	
	public static final String DATABASE_NAME = "pharm_items_dir";
    
	public static final String TABLE_ITEMS = "items";
	
	protected Context context;

    public DatabaseWrapper(Context context) {
    	super(context, DATABASE_NAME, null, 1);
    	this.context = context;
    }
    
    public long addItem(String title, String groups, String shelf) {
    	
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_GROUPS, groups);
        values.put(COL_SHELF, shelf);
        
        return getWritableDatabase().insert(TABLE_ITEMS, null, values);
    }
    
    public void removeItem(long id) {
    	getWritableDatabase().delete(TABLE_ITEMS, COL_ID + "=?", new String[]{ String.valueOf(id) });
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Toast.makeText(context, "Creating database", Toast.LENGTH_SHORT).show();
            db.execSQL("CREATE TABLE IF NOT EXISTS items (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title VARCHAR(50)," +
                    "groups VARCHAR(50)," +
                    "shelf VARCHAR(20))");
        } catch (Throwable t) {
            Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
        } 
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS items");
            onCreate(db);
    }
    
}
