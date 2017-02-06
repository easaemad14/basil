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
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BASIL extends AppCompatActivity {
    //Our global variables
    private final static int MAX_CONNECTIONS = 5;
    private int numConnections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: Open the file with connection information and read
        //TODO: Use internal storage in lieu of this implementation
        //See https://developer.android.com/training/basics/data-storage/files.html
        File rDir = getFilesDir();
        File appFile;
        numConnections = getNumConnections(); //This will be set when we read our file
        List<Button> bList = new ArrayList<Button>();
        List<String> bName = new ArrayList<String>();

        //SOME TESTING BITS
        bName.add("MR2");
        bName.add("Tacoma");
        bList.add((Button) findViewById(R.id.Button0));
        bList.add((Button) findViewById(R.id.Button1));
        for(int i = 0; i < numConnections; i++){
            bList.get(i).setText(bName.get(i));
            bList.get(i).setVisibility(View.VISIBLE);

            bList.get(i).setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view){
                    //TODO: Give the user the ability to rename and delete connections

                    return true;
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_bluetooth_device);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.bluetooth_error, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /* May be implemented in the future
     * TODO: Study this to add options on long click and fab
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
        int cons = 6;

        if(cons > MAX_CONNECTIONS){
            cons = 2; //For testing
        }

        return cons;
    }

    //TODO: Complete the following methods to add and remove BT Connections
    private void addConnection(){ //This needs to have a Bluetooth parameter
        if(numConnections == MAX_CONNECTIONS){
            Toast.makeText(getBaseContext(), R.string.too_many_connections, Toast.LENGTH_LONG)
                .show();
            return;
        }

        //. . .
    }

    private void rmConnection(){ //Read our file and delete the line with this button name

    }
}
