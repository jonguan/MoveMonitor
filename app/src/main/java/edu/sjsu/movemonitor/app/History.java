package edu.sjsu.movemonitor.app;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class History extends ListActivity {

    private DatabaseAdapter dbAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
        fillData();


        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
//                Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = dbAdapter.fetchAllRecords();
//        startManagingCursor(c);

        String[] from = new String[] { dbAdapter.KEY_DATE, dbAdapter.KEY_STEPS };
        int[] to = new int[] { R.id.historyCellDate, R.id.historyCellSteps };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter records =
                new SimpleCursorAdapter(this, R.layout.historycell, c, from, to, 0);
        setListAdapter(records);
    }

}
