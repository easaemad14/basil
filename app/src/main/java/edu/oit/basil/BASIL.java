package edu.oit.basil;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.BOND_BONDED;


public class BASIL extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    // If we turned on BT for our app, turn off when done
    private boolean btToggled = false;
    private enum state { UNKNOWN, LOCKED, UNLOCKED };
    state btState;
    Button button;
    BluetoothAdapter btAdapter = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    BluetoothDevice btDevice;
    String btAddress;
    private final static String name = "BASIL";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btState = state.UNKNOWN;
        button = (Button) findViewById(R.id.Button);

        // We need to set up our BlueTooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
            Toast.makeText(getBaseContext(), R.string.no_bluetooth,
                    Toast.LENGTH_LONG).show();
            button.setVisibility(View.GONE);
        }
        else if (!btAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
        else {
            // We are enabled; let's check connections
            btState = checkConnection();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode != RESULT_OK){
                    Toast.makeText(this, R.string.bt_mode_disabled,
                            Toast.LENGTH_LONG).show();
                    button.setBackgroundColor(Color.GRAY);
                    button.setVisibility(View.VISIBLE);
                    button.setClickable(false);
                }
                else {
                    btToggled = true;
                    btState = checkConnection();
                }
                break;
            default:
                Toast.makeText(getBaseContext(), R.string.unknown_result,
                        Toast.LENGTH_SHORT).show();
        }
    }

    /* this is disabled when the second activity is called
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
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(btAdapter != null && btAdapter.isEnabled() && btToggled) {
            btAdapter.disable(); // This is good manners
        }
    }

    public void btControl(View view) {
        if(!isBtConnected) {
            new ConnectBT().execute();
        }
        else if(btState == state.LOCKED) {
            unlockDevice();
        }
        else if(btState == state.UNLOCKED) {
            lockDevice();
        }
        else {
            Toast.makeText(getBaseContext(), R.string.unknown_state, Toast.LENGTH_LONG).show();
        }
    }

    private state checkConnection() {
        if(btAdapter == null || !btAdapter.isEnabled()) {
            Toast.makeText(getBaseContext(), R.string.bt_mode_disabled,
                    Toast.LENGTH_LONG).show();
            button.setBackgroundColor(Color.GRAY);
            button.setVisibility(View.GONE);
            return state.UNKNOWN;
        }

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                if (deviceName.compareTo(name) == 0) {
                    // We found our device
                    btAddress = device.getAddress();
                    button.setBackgroundColor(Color.RED);
                    button.setVisibility(View.VISIBLE);
                    return state.LOCKED;
                }
            }
        }
        return state.UNKNOWN;
    }

    private void unlockDevice()
    {
        if(btSocket != null) {
            try {
                btSocket.getOutputStream().write("U".toString().getBytes());
            }
            catch (IOException e) {
                Toast.makeText(getBaseContext(), R.string.write_error, Toast.LENGTH_LONG).show();
            }
            btState = state.UNLOCKED;
            button.setBackgroundColor(Color.GREEN);
        }
    }

    private void lockDevice()
    {
        if(btSocket != null) {
            try {
                btSocket.getOutputStream().write("L".toString().getBytes());
            }
            catch (IOException e) {
                Toast.makeText(getBaseContext(), R.string.write_error, Toast.LENGTH_LONG).show();
            }
            btState = state.LOCKED;
            button.setBackgroundColor(Color.RED);
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean success = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(BASIL.this, "Connecting...", "Please wait!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    btDevice = btAdapter.getRemoteDevice(btAddress);
                    btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if(!success) {
                Toast.makeText(getBaseContext(), R.string.unable_to_connect,
                        Toast.LENGTH_LONG).show();
                button.setBackgroundColor(Color.RED);
                isBtConnected = false; // Just in case
                btState = state.UNKNOWN;
            }
            else {
                Toast.makeText(getBaseContext(), R.string.connected, Toast.LENGTH_LONG).show();
                button.setBackgroundColor(Color.GREEN);
                button.setClickable(true);
                btState = state.UNLOCKED;
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}