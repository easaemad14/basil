package edu.oit.basil;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BASIL extends AppCompatActivity {
    //Our global variables
    private final static int MAX_CONNECTIONS = 5;
    private int numConnections;
    List<Button> butList = new ArrayList<Button>();

    // If File information ever changes, there's a potential to lose data.
    // In this event, be sure to adjust accordingly.
    File conFile;
    String conFileName = "myConnections.txt";
    FileInputStream conInStream;
    FileOutputStream conOutStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // I don't know a better way to do this
        butList.add((Button) findViewById(R.id.Button0));
        butList.add((Button) findViewById(R.id.Button1));
        butList.add((Button) findViewById(R.id.Button2));
        butList.add((Button) findViewById(R.id.Button3));
        butList.add((Button) findViewById(R.id.Button4));

        if((numConnections = getNumConnections()) < 0){ //Halt and catch fire!
            Toast.makeText(getBaseContext(), R.string.fio_error, Toast.LENGTH_LONG);
            try{
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            System.exit(0);
        }

        //TODO: Write a file to test the ability to do so and check our getNumConnections functionality

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
        int cons = 0;
        String conLine;
        String[] conInfo;
        conFile = new File(getBaseContext().getFilesDir(), conFileName);

        if(!conFile.exists()){
            try{
                conFile.createNewFile();
            } catch (IOException | SecurityException e){
                e.printStackTrace();
            }
        }

        try{
            conInStream = openFileInput(conFileName);
            BufferedReader buf = new BufferedReader(new InputStreamReader(conInStream));
            conLine = buf.readLine();

            while(conLine != null){
                conInfo = conLine.split("/");
                butList.get(cons).setText(conInfo[0]); //"Name/UID"
                butList.get(cons).setVisibility(View.VISIBLE);
                cons++;
            }

            conInStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        if(cons >= MAX_CONNECTIONS){
            Toast.makeText(getBaseContext(), R.string.too_many_connections, Toast.LENGTH_LONG).show();
            cons = MAX_CONNECTIONS - 1;
        }

        return ++cons;
    }

    //TODO: Complete the following methods to add and remove BT Connections
    private void addConnection(){ //This needs to have a Bluetooth parameter
        if(numConnections >= MAX_CONNECTIONS){ //Don't add if you've reached max (or beyond)
            Toast.makeText(getBaseContext(), R.string.too_many_connections, Toast.LENGTH_LONG).show();
            return;
        }

        //. . .
    }

    private void rmConnection(){ //Read our file and delete the line with this button name

    }
}
