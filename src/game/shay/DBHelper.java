package game.shay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "game";
	public static final int DB_VERSION = 1;
	
	public static final String TABLE = "numero";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NUMERO = "numero";	

	private static final String CREATE_SQL = 
			"CREATE TABLE "+TABLE+" ("+COLUMN_ID+" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + 
					COLUMN_NUMERO+" INTEGER NOT NULL)";

	private static final String DELETE_SQL = 
			"DROP TABLE " + TABLE;
		
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SQL);	
		
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_ID, 1);
		cv.put(COLUMN_NUMERO, 0);
		db.insert(TABLE, null, cv);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DELETE_SQL);
		db.execSQL(CREATE_SQL);
	}
	
	public int getNumero() {
		SQLiteDatabase myDB = this.getReadableDatabase();
		Cursor myCursor = myDB.query(TABLE, null, COLUMN_ID+"=1", null, null, null, null);
		
		if(myCursor.moveToFirst()) {
			int index = myCursor.getColumnIndex(COLUMN_NUMERO);
			int value = myCursor.getInt(index);
			myCursor.close();
			myDB.close();
        	return value;
		}
		
		myCursor.close();
		myDB.close();
        return -1;
	}
	
	public void updateNumero(int numero) {
		SQLiteDatabase myDB = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NUMERO, numero);
		myDB.update(TABLE, cv, COLUMN_ID+"=1", null);
		myDB.close();
	}
	
	public int getTableCount() {
		SQLiteDatabase myDB = this.getReadableDatabase();
		Cursor myCursor = myDB.query(TABLE, null, null, null, null, null, null);
		int count = myCursor.getCount();
		myCursor.close();
		myDB.close();
		return count;
	}
}
