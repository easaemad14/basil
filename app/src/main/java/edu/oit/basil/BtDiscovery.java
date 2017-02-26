package edu.oit.basil;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity will search for discoverable devices, allowing the user to
 * decide which one they want to add, and then return the Name/Address of
 * the connection in the form of a string which will allow the main activity
 * to write this to file.
 */
public class BtDiscovery extends AppCompatActivity {
    private final static int MAX_DEVICES = 5;
    private int numDiscovered = 0;
    BluetoothAdapter btAdapter;
    List<Button> btList = new ArrayList<Button>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_discovery);

        // Implementation of a 5 minute CountDownTimer so that we don't search for too long
        new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
                // TODO: Display some cool animation so the user knows we're searching
            }
            public void onFinish() {
                Toast.makeText(getBaseContext(), R.string.bluetooth_timeout,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }.start();

        /*
        // Let's start searching for discoverable devices
        if(!btAdapter.startDiscovery()) {
            Toast.makeText(getBaseContext(), R.string.bluetooth_discover_error,
                    Toast.LENGTH_LONG).show();
            return;
        }
        */

        //addConnection("MR2/12:34:56:78");
    }

    private void addConnection(String connToAdd){
        Intent putConnection = new Intent();
        putConnection.putExtra("RETURNED_CONNECTION", connToAdd);
        setResult(RESULT_OK, putConnection);
        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
