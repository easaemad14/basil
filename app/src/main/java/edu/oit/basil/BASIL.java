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
    final String conFileName = "myConnections.txt";

    // TODO: Move these variables to their respective methods; don't need to be global
    FileInputStream conInStream;
    FileOutputStream conOutStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // I don't know a dynamic way to do this
        butList.add((Button) findViewById(R.id.Button0));
        butList.add((Button) findViewById(R.id.Button1));
        butList.add((Button) findViewById(R.id.Button2));
        butList.add((Button) findViewById(R.id.Button3));
        butList.add((Button) findViewById(R.id.Button4));

        numConnections = getNumConnections();
        if(numConnections < 0){ //Halt and catch fire!
            Toast.makeText(getBaseContext(), R.string.fio_error, Toast.LENGTH_LONG).show();
            try{
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            System.exit(0);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_bluetooth_device);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This is done for testing purposes. Checking the ability to read and write files
                numConnections = addConnection();
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
                if(conFile.createNewFile()){
                    Toast.makeText(getBaseContext(), R.string.create_file, Toast.LENGTH_LONG).show();
                }
            } catch (IOException | SecurityException e){
                e.printStackTrace();
            }
        }

        try{
            conInStream = openFileInput(conFileName);
            BufferedReader buf = new BufferedReader(new InputStreamReader(conInStream));
            conLine = buf.readLine();

            while(conLine != null && cons < MAX_CONNECTIONS){
                conInfo = conLine.split("/");
                butList.get(cons).setText(conInfo[0]); //"Name/UID"
                butList.get(cons).setVisibility(View.VISIBLE);
                conLine = buf.readLine();
                cons++;
            }
            conInStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return cons;
    }

    //TODO: Complete the following methods to add and remove BT Connections
    private int addConnection(){ //This needs to have a Bluetooth parameter
        if(numConnections >= MAX_CONNECTIONS) { //Don't add if you've reached max (or beyond)
            Toast.makeText(getBaseContext(), R.string.too_many_connections, Toast.LENGTH_LONG).show();
            return numConnections;
        }
        else{

            //TODO: Implement a check for Name and UID (conInfo[0] and conInfo[1], respectively)

            int cons = numConnections;

            //Testing purposes
            String toyota = "MR2/12:34:56:78:43:AA";

            if(!conFile.canWrite()){
                Toast.makeText(getBaseContext(), R.string.fio_error, Toast.LENGTH_LONG).show();
                return cons;
            }
            else{
                try{
                    conOutStream = openFileOutput(conFileName, getBaseContext().MODE_APPEND);
                    conOutStream.write(toyota.getBytes());
                    conOutStream.close();
                    cons++;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            //TODO: Figure out why cons < numConnections
            numConnections = getNumConnections(); //This will also add the button
            if(cons <= numConnections) //Sanity check
                return cons;
            return numConnections;
        }
    }

    private void rmConnection(){ //Read our file and delete the line with this button name
        //TODO: Implement the ability to remove a connection
    }

    private void clearAllCons(){ //Used to reset the connection database from menu
        //TODO: Add the option to delete the file and start from scratch
    }
}
