package edu.oit.basil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private int MAX_CONNECTIONS = 5;
    private int num_connections = 0;
    boolean TIMER_CANCELLED = false; // Don't display Toast if cancelled
    List<Button> btList = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_discovery);
        BluetoothAdapter bluetoothAdapter;

        // TODO: Next term make this a ListView to be dynamic
        btList.add((Button) findViewById(R.id.ConBut0));
        btList.add((Button) findViewById(R.id.ConBut1));
        btList.add((Button) findViewById(R.id.ConBut2));
        btList.add((Button) findViewById(R.id.ConBut3));
        btList.add((Button) findViewById(R.id.ConBut4));


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Let's start searching for discoverable devices
        // The connections are handled by the BroadcastReceiver
        if(!bluetoothAdapter.startDiscovery()) {
            Toast.makeText(getBaseContext(), R.string.bluetooth_discover_error,
                    Toast.LENGTH_LONG).show();
            return;
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btReceiver, filter);

        startTimer(true);
    }

    // BroadcastReceiver for BT Devices found
    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice tDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = tDevice.getName();
                String deivceHWAddress = tDevice.getAddress();

                listConnection(deviceName + "/" + deivceHWAddress);
            }
        }
    };

    // The following Activity Lifecycles handle the countDownTimer and BroadcastReceiver
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        startTimer(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TIMER_CANCELLED = true;
        startTimer(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TIMER_CANCELLED = true;
        startTimer(false);

        unregisterReceiver(btReceiver);
    }


    // Implementation of a 5 minute CountDownTimer so that we don't search for too long
    private void startTimer(boolean start) {
        // We fancy with them progress bars
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        CountDownTimer cdt = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (300000 - millisUntilFinished) / 1000);
            }

            @Override
            public void onFinish() {
                if(!TIMER_CANCELLED) {
                    Toast.makeText(getBaseContext(), R.string.bluetooth_timeout,
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        };

        if(start) {
            cdt.start();
        }
        else {
            cdt.cancel();
        }
    }

    private void listConnection(String connToList) {
        if(num_connections >= MAX_CONNECTIONS)
            return;
        final Button button = btList.get(num_connections);

        button.setText(connToList);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendConnection(button.getText().toString());
            }
        });

        ++num_connections;
    }

    private void sendConnection(String connToAdd) {
        Intent putConnection = new Intent();
        putConnection.putExtra("RETURNED_CONNECTION", connToAdd);
        setResult(RESULT_OK, putConnection);
        finish();
    }
}
