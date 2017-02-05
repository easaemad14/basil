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

public class BASIL extends AppCompatActivity {
    //This is the maximum number of connections we will allow at a time
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

        //TODO: Use getNumConnections to determine the number of buttons that should be visible
        if ((numConnections = getNumConnections()) >= MAX_CONNECTIONS){
            Toast.makeText(getBaseContext(), R.string.too_many_connections, Toast.LENGTH_LONG);
        }

        //TODO: Create a vector for the buttons to easily iterate
        //TODO: Delete these after you create vector and iterate
        Button button0 = (Button) findViewById(R.id.Button0);
        Button button1 = (Button) findViewById(R.id.Button1);
        Button button2 = (Button) findViewById(R.id.Button2);
        Button button3 = (Button) findViewById(R.id.Button3);
        Button button4 = (Button) findViewById(R.id.Button4);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_bluetooth_device);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.bluetooth_error, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        switch (numConnections){
            case 5:
                button4.setVisibility(View.VISIBLE);
            case 4:
                button3.setVisibility(View.VISIBLE);
            case 3:
                button2.setVisibility(View.VISIBLE);
            case 2:
                button1.setVisibility(View.VISIBLE);
            case 1:
                button0.setVisibility(View.VISIBLE);
            default:
                break;
        }
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
        return 3; //For testing
    }

    //TODO: Complete the following methods to add and remove BT Connections
    private void addConnection(){ //This needs to have a Bluetooth parameter
        if(numConnections == MAX_CONNECTIONS){
            Toast.makeText(getBaseContext(), R.string.too_many_connections, Toast.LENGTH_LONG);
            return;
        }

        //. . .
    }

    private void rmConnection(){ //Read our file and delete the line with this button name

    }
}
