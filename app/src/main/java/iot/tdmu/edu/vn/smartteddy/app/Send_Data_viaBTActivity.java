package iot.tdmu.edu.vn.smartteddy.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class Send_Data_viaBTActivity extends AppCompatActivity {

    Button btnSend;
    TextView txtString;

    Handler bluetoothIn;

    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;
    final int RECIEVE_MESSAGE = 1;

    // SPP UUID service - this should work for most devices
    final private static UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send__data_via_bt);
        btnSend = (Button) findViewById(R.id.btnSend);
        txtString = (TextView) findViewById(R.id.txtString);

        intent = getIntent();
        //final String uuids = intent.getStringExtra("UUID");
        address = intent.getStringExtra("ADDRESS");

        //BTMODULEUUID = UUID.fromString(uuids);

        bluetoothIn = new Handler(){
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:													// if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);					// create string from bytes array
                        recDataString.append(strIncom);												// append string
                        int endOfLineIndex = recDataString.indexOf("\r\n");
                        if (endOfLineIndex > 0) { 											// if end-of-line,
                            String sbprint = recDataString.substring(0, endOfLineIndex);				// extract string
                            recDataString.delete(0, recDataString.length());										// and clear
                            txtString.setText("Data from Raspberry Pi: " + " " + sbprint); 	        // update TextView
                            btnSend.setEnabled(true);
                        }
                        break;
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        //send data
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                int ma = intent.getIntExtra("MA",0);
                String id = intent.getStringExtra("SSID");
                String pass =  intent.getStringExtra("PASS");
                String wifiString = intent.getStringExtra("SSID_QR");
                if(ma == 1){
                    mConnectedThread.write("wifi|" + id + "|" + pass);
                } else if (ma == 2){
                    mConnectedThread.write("wifi|" + wifiString);
                }

            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        if(Build.VERSION.SDK_INT >= 17){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
                return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Fail", "Could not create Insecure RFComm Connection",e);
            }
        }

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    protected void onResume() {
        super.onResume();

        //create device and set the MAC address
        //BluetoothDevice device = btAdapter.getRemoteDevice(address);
        BluetoothDevice device = btAdapter.getRemoteDevice("B8:27:EB:C1:7C:9A");

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
            Toast.makeText(getBaseContext(), "Connecting OK...", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                Toast.makeText(getBaseContext(), "Socket Close...", Toast.LENGTH_LONG).show();
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_SHORT).show();
                //finish();

            }
        }
    }
}
