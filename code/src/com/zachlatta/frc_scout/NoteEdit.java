package com.zachlatta.frc_scout;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class NoteEdit extends Activity
{
    private EditText mNameText;
    private EditText mNumberText;
    private CheckBox mGameplayShooting;
    private CheckBox mGameplayClimbing;
    private CheckBox mGameplayDefense;
    private EditText mNotesText;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mNameText = (EditText) findViewById(R.id.name);
        mNumberText = (EditText) findViewById(R.id.number);
        mGameplayShooting = (CheckBox) findViewById(R.id.gameplay_shooting);
        mGameplayClimbing = (CheckBox) findViewById(R.id.gameplay_climbing);
        mGameplayDefense = (CheckBox) findViewById(R.id.gameplay_defense);
        mNotesText = (EditText) findViewById(R.id.notes);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);

        if(mRowId == null)
        {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        }

        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void populateFields()
    {
        if (mRowId != null)
        {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mNameText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NAME)));
            mNumberText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NUMBER)));

            mGameplayShooting.setChecked(
                    note.getInt(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_GAMEPLAY_SHOOTING)) > 0 ? true : false);
            mGameplayClimbing.setChecked(
                    note.getInt(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_GAMEPLAY_CLIMBING)) > 0 ? true : false);
            mGameplayDefense.setChecked(
                    note.getInt(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_GAMEPLAY_DEFENSE)) > 0 ? true : false);
            mNotesText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTES)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        populateFields();
    }

    private void saveState()
    {
        String name = mNameText.getText().toString();
        String number = mNumberText.getText().toString();
        boolean gameplayShooting = mGameplayShooting.isChecked();
        boolean gameplayClimbing = mGameplayClimbing.isChecked();
        boolean gameplayDefense = mGameplayDefense.isChecked();
        String notes = mNotesText.getText().toString();

        if (mRowId == null)
        {
            long id = mDbHelper.createNote(name, number, gameplayShooting, gameplayClimbing, gameplayDefense, notes);

            if (id > 0)
            {
                mRowId = id;
            }
        }
        else
        {
            mDbHelper.updateNote(mRowId, name, number, gameplayShooting, gameplayClimbing, gameplayDefense, notes);
        }
    }
}
