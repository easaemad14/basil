package edu.oit.basil;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BASIL extends AppCompatActivity {
    private final static int MAX_CONNECTIONS = 5;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_CONTROL_MOTOR = 2;
    private final static int NEW_CONNECTION = 3;
    private int btToggled = 0; // If we turned on BT for our app, turn off when done
    private int numConnections = 0;
    List<Button> butList = new ArrayList<Button>();
    BluetoothAdapter btAdapter;

    // If File information ever changes, there's a potential to lose data.
    // In this event, be sure to adjust accordingly.
    File conFile;
    final String conFileName = "myConnections.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This represents the 5 possible usable buttons
        // I could do this dynamically with a ViewGroup
        butList.add((Button) findViewById(R.id.Button0));
        butList.add((Button) findViewById(R.id.Button1));
        butList.add((Button) findViewById(R.id.Button2));
        butList.add((Button) findViewById(R.id.Button3));
        butList.add((Button) findViewById(R.id.Button4));

        numConnections = getNumConnections();
        if(numConnections < 0) { //Halt and catch fire!
            Toast.makeText(getBaseContext(), R.string.fio_error,
                    Toast.LENGTH_LONG).show();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

        // Set up Long Click Listeners for buttons
        for(final Button bts : butList){
            bts.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // TODO: Fix this to give the user the option
                    // Create menu inflater to delete or rename connection
                    String connToRemove = bts.getText().toString();
                    rmConnection(connToRemove);
                    numConnections = getNumConnections(); // Rewrite UI
                    return true;
                }
            });
        }

        // We need to set up our BlueTooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
            Toast.makeText(getBaseContext(), R.string.no_bluetooth,
                    Toast.LENGTH_LONG).show();
        }
        else if(!btAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_bluetooth_device);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numConnections >= MAX_CONNECTIONS) { //Don't add if you've reached max
                    Toast.makeText(getBaseContext(), R.string.too_many_connections,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Check to see if we are running in BlueTooth mode
                if(btAdapter == null || !btAdapter.isEnabled()) {
                    Toast.makeText(getBaseContext(), R.string.bt_mode_disabled,
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Intent getNewConnection = new Intent(getBaseContext(), BtDiscovery.class);
                    startActivityForResult(getNewConnection, NEW_CONNECTION);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode != RESULT_OK){
                    Toast.makeText(this, R.string.bt_mode_disabled,
                            Toast.LENGTH_LONG).show();
                }
                else {
                    btToggled = 1;
                }
                break;
            case REQUEST_CONTROL_MOTOR:
                /**
                 * I need to have a connectible device that has the ability to communicate
                 * with in order to test this.
                 *
                 * The firmware will need to know the state of the lock (locked or unlocked)
                 * so that the user will know the state before toggling.
                 */
                break;
            case NEW_CONNECTION: // Write the new connection to file and make visible
                if(resultCode == RESULT_OK) {
                    addConnection(data.getStringExtra("RETURNED_CONNECTION"));
                }
                break;
            default:
                // More will be needed when I implement additional activities
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(btAdapter != null && btToggled == 1) {
            btAdapter.disable();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(btAdapter != null && btToggled == 1) {
            btAdapter.enable();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(btAdapter != null && btToggled == 1) {
            btAdapter.disable();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.information) {
            return true;
        }
        else if(id == R.id.clear_connections){
            clearAllCons();
        }

        return super.onOptionsItemSelected(item);
    }

    public void btControl(View view) {
        // TODO: Decide how to handle (un)lock functionality
    }

    /**
     * This will return the number of stored Bluetooth Connections that are in our
     * "database" of Bluetooth devices that is read from the BASIL data file.
     *
     * Each connection corresponds to a Button on the main activity, which a user can use to
     * connect to that device and take appropriate action.
     */
    private int getNumConnections() {
        int cons = 0;
        String conLine;
        String[] conInfo;
        FileInputStream conInStream;
        conFile = new File(getBaseContext().getFilesDir(), conFileName);

        // I will use this to redraw the UI, so let's make all buttons invisible here
        for(Button bts : butList){
            bts.setVisibility(View.INVISIBLE);
        }

        if(!conFile.exists()) {
            try {
                if(conFile.createNewFile()) {
                    Toast.makeText(getBaseContext(), R.string.create_file,
                            Toast.LENGTH_LONG).show();
                }
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
            return cons;
        }

        try {
            conInStream = openFileInput(conFileName);
            BufferedReader buf = new BufferedReader(new InputStreamReader(conInStream));
            conLine = buf.readLine();

            while(conLine != null && cons < MAX_CONNECTIONS) {
                conInfo = conLine.split("/");

                // Set Button names and make them visible
                butList.get(cons).setText(conInfo[0]); //"Name/Address"
                butList.get(cons).setVisibility(View.VISIBLE);

                conLine = buf.readLine();
                ++cons;
            }
            conInStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cons;
    }

    private void addConnection(String connToAdd) {
        if(connToAdd == null || connToAdd.isEmpty()){
            Toast.makeText(getBaseContext(), R.string.nothing_to_add, Toast.LENGTH_LONG).show();
            return;
        }

        // First, let's make sure the connection doesn't exist
        try {
            String conLine;
            String[] conInfo, conToAddInfo;
            conToAddInfo = connToAdd.split("/");
            FileInputStream conInStream;

            conInStream = openFileInput(conFileName);
            BufferedReader buf = new BufferedReader(new InputStreamReader(conInStream));
            conLine = buf.readLine();

            while(conLine != null) {
                conInfo = conLine.split("/");

                if(conInfo[1].equals(conToAddInfo[1])) {
                    Toast.makeText(getBaseContext(), R.string.connection_exists,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                conLine = buf.readLine();
            }
            conInStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Make sure to append newline when writing
        connToAdd += System.getProperty("line.separator");

        if(!conFile.canWrite()) {
            Toast.makeText(getBaseContext(), R.string.fio_error,
                    Toast.LENGTH_LONG).show();
        }
        else {
            FileOutputStream conOutStream;
            try {
                conOutStream = openFileOutput(conFileName, getBaseContext().MODE_APPEND);
                conOutStream.write(connToAdd.getBytes());
                conOutStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        numConnections = getNumConnections(); // Increment and add make button visible
    }

    private void rmConnection(String ctr) { // Delete the line with this info (name)
        String tmpFileName = "tmp.txt";
        String conLine;
        String[] conInfo;
        FileInputStream conInStream;
        FileOutputStream conOutStream;
        File tmpFile = new File(getBaseContext().getFilesDir(), tmpFileName);

        if(ctr == null || ctr.isEmpty()){
            Toast.makeText(getBaseContext(), R.string.nothing_to_remove, Toast.LENGTH_LONG).show();
            return;
        }

        // Read each line and write the line to the tmp file if it doesn't match
        try {
            conInStream = openFileInput(conFileName);
            conOutStream = openFileOutput(tmpFileName, getBaseContext().MODE_PRIVATE);
            BufferedReader buf = new BufferedReader(new InputStreamReader(conInStream));
            conLine = buf.readLine();

            while(conLine != null) {
                conInfo = conLine.split("/");

                if(!conInfo[0].equals(ctr)) {
                    conLine += System.getProperty("line.separator");
                    conOutStream.write(conLine.getBytes());

                    return;
                }
                conLine = buf.readLine();
            }
            conInStream.close();
            conOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete our current file and replace with our tmpFile
        if(conFile.exists()) {
            try {
                if(!conFile.delete()) {
                    Toast.makeText(getBaseContext(), R.string.delete_file,
                            Toast.LENGTH_LONG).show();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        tmpFile.renameTo(conFile);
    }

    private void clearAllCons() { //Used to reset the connection database from menu
        if (conFile.exists()) {
            try {
                if (!conFile.delete()) {
                    Toast.makeText(getBaseContext(), R.string.delete_file,
                            Toast.LENGTH_LONG).show();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            // Now create the empty file
            try {
                conFile.createNewFile();
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }

        numConnections = getNumConnections();
    }
}