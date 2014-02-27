package edu.sjsu.movemonitor.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

/**
 * Created by jonguan on 2/23/14.
 */
public class DatabaseAdapter {

    public static final String KEY_DATE = "date";
    public static final String KEY_STEPS = "steps";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "DatabaseAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_NAME = "dataStorage";
    private static final String DATABASE_TABLE = "stepTable";


    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, " + KEY_DATE + " text not null, " + KEY_STEPS + " text not null);";


    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public DatabaseAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DatabaseAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param date the title of the note
     * @param steps the body of the note
     * @return rowId or -1 if failed
     */
    public long addRecord(String date, String steps) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_STEPS, steps);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteRecord(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllRecords() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DATE,
                KEY_STEPS}, null, null, null, null, KEY_DATE + " DESC" );
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor getRecord(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_DATE, KEY_STEPS}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param date value to set note title to
     * @param steps value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateRecord(long rowId, String date, String steps) {
        ContentValues args = new ContentValues();
        args.put(KEY_DATE, date);
        args.put(KEY_STEPS, steps);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}