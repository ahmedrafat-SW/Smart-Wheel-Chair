package com.abdo.appblucontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Voice extends AppCompatActivity {
    ImageView voice_Btn;
    TextView txvResult;
     String addressVoice = null ;
    private ProgressDialog progress;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothDevice myBlue;
    private BluetoothSocket soocket = null;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean isBtConnected = true;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final int REQ_CODE_SPEECH_INPUT = 100;
    //
    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }
   /* private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Voice.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }
        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        { try { if (soocket == null || !isBtConnected) {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
            myBlue = myBluetooth.getRemoteDevice(addressVoice);//connects to the device's address and checks if it's available
            soocket = myBlue.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            soocket.connect();//start connection
        }
        } catch (IOException e) { ConnectSuccess = false;//if the try failed, you can check the exception here
        }return null; }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        { super.onPostExecute(result);
            if (!ConnectSuccess) { msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else { msg("Connected.");
                isBtConnected = true;
            }
           */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice myBlu) throws IOException {
        return myBlu.createRfcommSocketToServiceRecord(myUUID);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newInt = getIntent();
         addressVoice =newInt.getStringExtra(Direction.EXTRA_ADDRESS);
        setContentView(R.layout.activity_voice);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        myBlue = myBluetooth.getRemoteDevice(addressVoice);
        try {
            if (BluetoothAdapter.checkBluetoothAddress(addressVoice)) {
                myBlue = myBluetooth.getRemoteDevice(addressVoice);
                soocket = createBluetoothSocket(myBlue);
            } else { msg("Invalid MAC: Address"); }
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        voice_Btn= findViewById(R.id.voice_Btn);
        txvResult =findViewById(R.id.txvResult);
        voice_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               msg("Say Something...");
                if(isBtConnected==true){
                    promptSpeechInput();
                }
                else{ msg("First Connect to Arduino"); }
            }
        });
        //new ConnectBT().execute();
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            try { startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) { msg("Your Device Doesn't Support Speech Intent"); }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            if (spokenText.contains("forward")){
                if (soocket!=null)
                {
                    try
                    {
                        soocket.getOutputStream().write("1".toString().getBytes());
                        msg("Forward");
                    }
                    catch (IOException e)
                    {
                        msg("Error occurred when sending Data");
                    }
                }
            }
            else if (spokenText.contains("reverse")){
                if (soocket!=null) {
                    try
                    {
                        soocket.getOutputStream().write("2".toString().getBytes());
                        msg("Reserve");
                    }
                    catch (IOException e)
                    {
                        msg("Error occurred when sending Data");
                    }
                }
            }
            else if (spokenText.contains("right")){
                if (soocket!=null)
                {
                    try
                    {
                        soocket.getOutputStream().write("3".toString().getBytes());
                        msg("Right");
                    }
                    catch (IOException e)
                    {
                        msg("Error occurred when sending Data");
                    }
                }
            }
            else if (spokenText.contains ("left")){
                if (soocket!=null)
                {
                    try
                    {
                        soocket.getOutputStream().write("4".toString().getBytes());
                        msg("Left");
                    }
                    catch (IOException e)
                    {
                        msg("Error occurred when sending Data");
                    }
                }
            }
            else if (spokenText.contains("stop")){
                if (soocket!=null)
                {
                    try
                    {
                        soocket.getOutputStream().write("5".toString().getBytes());
                        msg("Stop");
                    }
                    catch (IOException e)
                    {
                        msg("Error occurred when sending Data");
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}