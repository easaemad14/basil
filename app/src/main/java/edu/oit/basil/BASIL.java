package edu.oit.basil;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class BASIL extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        File appFile;
        int numConnections = getNumConnections();
        appFile = new File(Environment.getDataDirectory() + File.separator + R.string.app_name);

        try{
            appFile.createNewFile();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        //TODO: Use getNumConnections to determine the number of buttons that should be visible

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_bluetooth_device);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Bluetooth Device not implemented", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_basil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO: Make sure non-visible buttons can't be clicked, or handle this
    /**
     * This is the method that is called when any (visible) button is clicked. Need
     * to use R class(?) to differentiate which bt connection is being made and handled.
     */
    public void btControl(View view){
        //TODO: implement connection check and implement a fragment(?) for (un)lock functions
    }
    /**
     * This will return the number of stored Bluetooth Connections that are in our
     * "database" of Bluetooth devices that is read from the BASIL data file.
     *
     * Each connection corresponds to a Button on the main activity, which a user can use to
     * connect to that device and take appropriate action.
     */
    private int getNumConnections(){
        return 2; //For testing
    }

    //TODO: Complete the following methods to add and remove BT Connections
    private void addConnection(){ //This needs to have a Bluetooth parameter

    }

    private void rmConnection(){ //This needs to have a Bluetooth parameter

    }
}
