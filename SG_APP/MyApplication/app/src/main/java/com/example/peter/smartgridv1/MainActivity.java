package com.example.peter.smartgridv1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    final byte delimiter = 33;
    int readBufferPosition = 0;

    public void sendBtMsg(String msgSend){
        //TODO be sure to change the UUID here.
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if(!mmSocket.isConnected()){
                mmSocket.connect();
            }

            String msg = msgSend;
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sectorStatus(boolean userCheckedBox){

        int messageResId = 0;
        if(userCheckedBox == true){
            messageResId = R.string.sectorOn;
        }
        else{
            messageResId = R.string.sectorOff;
        }
        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Handler handler = new Handler();

        //residential sectors
        final CheckBox residential_1 = (CheckBox) findViewById(R.id.res1);
        final CheckBox residential_2 = (CheckBox) findViewById(R.id.res2);
        final CheckBox residential_3 = (CheckBox) findViewById(R.id.res3);
        final CheckBox residential_4 = (CheckBox) findViewById(R.id.res4);
        final CheckBox residential_5 = (CheckBox) findViewById(R.id.res5);
        final CheckBox residential_6 = (CheckBox) findViewById(R.id.res6);
        final CheckBox residential_7 = (CheckBox) findViewById(R.id.res7);
        final CheckBox residential_8 = (CheckBox) findViewById(R.id.res8);
        final CheckBox residential_9 = (CheckBox) findViewById(R.id.res9);
        final CheckBox residential_10 = (CheckBox) findViewById(R.id.res10);
        final CheckBox residential_11 = (CheckBox) findViewById(R.id.res11);

        //commercial sectors
        final CheckBox commercial_1 = (CheckBox) findViewById(R.id.com1);
        final CheckBox commercial_2 = (CheckBox) findViewById(R.id.com2);
        final CheckBox commercial_3 = (CheckBox) findViewById(R.id.com3);
        final CheckBox commercial_4 = (CheckBox) findViewById(R.id.com4);
        final CheckBox commercial_5 = (CheckBox) findViewById(R.id.com5);
        final CheckBox commercial_6 = (CheckBox) findViewById(R.id.com6);

        //industrial sectors
        final CheckBox industrial_1 = (CheckBox) findViewById(R.id.ind1);

        //Acquire Bluetooth Adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final class workerThread implements Runnable{
            private String blueTMessage;

            public workerThread(String msg) {
                blueTMessage = msg;
            }
            private final InputStream mmInputStream = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException();
                }
            };
            @Override
            public void run() {
                sendBtMsg(blueTMessage);
                while(!Thread.currentThread().isInterrupted()){
                    int bytesAvailable;
                    boolean workDone = false;
                    try{
                        final InputStream mmInputStream;
                        mmInputStream = mmSocket.getInputStream();
                        //InputStream mmSocketInputStream = mmSocket.getInputStream();
                        bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0){
                            byte[] packetBytes = new byte[bytesAvailable];
                            Log.e("SmartGrid recv bt", "bytes available");
                            byte[] readBuffer = new byte[1024];
                            mmInputStream.read(packetBytes);

                            for(int i = 0; i <bytesAvailable; i++){
                                byte b = packetBytes[i];
                                if(b == delimiter){
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer,0,encodedBytes,0,encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
/*
*                                    handler.post(new Runnable(){
 *                                       public void run(){
  *                                          //mylabel.setText(data);
   *                                         //this section of code is where we will produce the data values.
*
 *                                       }
  *                                  });*/
                                    workDone = true;
                                    break;
                                }
                                else{
                                    readBuffer[readBufferPosition++] = b;
                                    mmInputStream.close();
                                }
                            }
                            if (workDone){
                                mmSocket.close();
                                break;
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //start handlers
        residential_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);

                }
                (new Thread(new workerThread("res1_tog"))).start();
            }
            });
        residential_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }

            }
        });
        residential_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }

            }
        });
        residential_4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }

            }
        });
        residential_5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }

            }
        });
        residential_6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        residential_7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        residential_8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        residential_9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        residential_10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        residential_11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        commercial_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        commercial_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        commercial_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        commercial_4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        commercial_5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        commercial_6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });
        industrial_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sectorStatus(true);
                }
                else{
                    sectorStatus(false);
                }
            }
        });

        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth,0);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            for(BluetoothDevice device : pairedDevices){
                if (device.getName().equals("raspberrypi-0")){
                    Log.e("SmartGrid",device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }

    }


}
