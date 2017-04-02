package edu.oit.basil;

import android.app.Notification;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Handler;

public class BtService {

    private class ConnectThread extends Thread {
        private BluetoothSocket socket;
        private OutputStream message;

        public ConnectThread(BluetoothSocket sock) {
            socket = sock;

            try {
                message = socket.getOutputStream();
            } catch (IOException e) {
                // Uable to get output stream
            }
        }

        public void write(byte[] mess) {
            try {
                message.write(mess);
            } catch (IOException e) {
                // Unable to write to device! What to do?
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                // Unable to close socket!
            }
        }
    }
}
