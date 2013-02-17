package com.zachlatta.frc_scout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Notes database access helper class. Defines basic CRUD operations for the notes and gives the ability to list all
 * notes as well as retrieve or modify a specific note.
 */
public class NotesDbAdapter
{
    public static final String KEY_NAME = "name";
    public static final String KEY_NUM = "number";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "NotesDBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_CREATE = "create " + DATABASE_NAME + " " + DATABASE_TABLE + " (" + KEY_ROWID
            + " integer primary key " + "autoincrement, " + KEY_NAME + " " + KEY_NUM + " " + KEY_NOTES
            + " not null, body text not null);";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy "
                    + "all old data.") ;
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be opened/created.
     *
     * @param ctx The Context in which to work.
     */
    public NotesDbAdapter(Context ctx)
    {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new instance of the database. If it cannot be
     * created, throw an exception to signal the failure.
     *
     * @return this Self reference, allowing this to be chained in an initialization call.
     * @throws SQLException If the database could be neither opened or created.
     */
    public NotesDbAdapter open() throws SQLException
    {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    /**
     * Create a new note using the team name, number, and notes provided. If the note is successfully created, then
     * return the new rowId for that note, otherwise return -1 to indicate failure.
     *
     * @param num The name of the team.
     * @param num The team's number.
     * @param notes The notes of the team.
     * @return rowId or -1 if failed.
     */
    public long createNote(String name, String num, String notes)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_NUM, num);
        initialValues.put(KEY_NOTES, notes);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId Id of note to delete.
     * @return True if deleted, false otherwise.
     */
    public boolean deleteNote(long rowId)
    {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database.
     *
     * @return Cursor over all notes.
     */
    public Cursor fetchAllNotes()
    {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_NUM, KEY_NOTES}, null, null, null, null,
                null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId Id of note to retrieve.
     * @return Cursor positioned to match note, if found.
     * @throws SQLException If note could not be found/retrieved.
     */
    public Cursor fetchNote(long rowId) throws SQLException
    {
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_NOTES}, KEY_ROWID + "=" + rowId,
                        null, null, null, null, null);
        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * Update the note using the details provided. The note to be updated is specified using the rowId, and is altered
     * to use the title and body values passed in.
     *
     * @param rowId Id of note to update.
     * @param name  Value to set team name to.
     * @param num   Value to set team number to.
     * @param notes Values to set team notes to.
     * @return True if the note was successfully updated, false otherwise.
     */
    public boolean updateNote(long rowId, String name, String num, String notes)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_NUM, num);
        args.put(KEY_NOTES, notes);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
