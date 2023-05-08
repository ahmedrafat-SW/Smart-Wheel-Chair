package com.abdo.appblucontrol;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Direction extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mtoggle;
    Button forward_btn, left_btn, right_btn, reverse_btn, stop;
    private NavigationView navigationView;
    //
    String address = null;
    public static final String EXTRA_ADDRESS = "Device_Address" ;
    private ProgressDialog progress;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothDevice myBlu;
    private BluetoothSocket socket = null;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean isBtConnected = false;
    String TAG;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //
    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }
    private class ConnectBT extends AsyncTask<Void, Void, Void>  {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Direction.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }
        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        { try { if (socket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    myBlu = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    socket = myBlu.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    socket.connect();//start connection
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
            progress.dismiss();
        }}
    private BluetoothSocket createBluetoothSocket(BluetoothDevice myBlu) throws IOException {
        return myBlu.createRfcommSocketToServiceRecord(myUUID);
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newint = getIntent();
        address = newint.getStringExtra(Main.EXTRA_ADDRESS);
        setContentView(R.layout.activity_direction);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        myBlu = myBluetooth.getRemoteDevice(address);
        try { if (BluetoothAdapter.checkBluetoothAddress(address)) {
                //It is a valid MAC address.
                myBlu = myBluetooth.getRemoteDevice(address);
                socket = createBluetoothSocket(myBlu);
            } else { msg("Invalid MAC: Address"); }
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        //
        new ConnectBT().execute();
        //Direction Icons
        forward_btn=findViewById(R.id.forward_btn);
        reverse_btn=findViewById(R.id.reverse_btn);
        stop=findViewById(R.id.stop);
        right_btn=findViewById(R.id.right_btn);
        left_btn=findViewById(R.id.left_btn);
        //NavigationView
        navigationView = findViewById(R.id.drawer_nav);
        drawerLayout =findViewById(R.id.drawerLayout);
        mtoggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_drawer_open,R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(mtoggle);
        mtoggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setCheckedItem(R.id.nav_direction);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_direction:
                        msg("Direction");
                        break;
                    case R.id.nav_voice:
                        msg("Voice");
                        String addressVoice = address;
                        Intent i= new Intent(Direction.this,Voice.class);
                        i.putExtra(EXTRA_ADDRESS, addressVoice);
                        startActivity(i);

                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //Button Action
        forward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBtConnected==true){
                    forward();
                }
                else{ msg("First Connect to Arduino"); }
            }
        });
        reverse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBtConnected==true){
                    reserve();
                }
                else{ msg("First Connect to Arduino"); }
            }
        });
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBtConnected==true){
                    left();
                }
                else{ msg("First Connect to Arduino"); }
            }
        });
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBtConnected==true){
                    right();
                }
                else{ msg("First Connect to Arduino"); }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBtConnected==true){
                    stop();
                }
                else{ msg("First Connect to Arduino"); }
            }
        });
    }
    private void forward() {
        if (socket!=null)
        {
            try
            {
                socket.getOutputStream().write("1".toString().getBytes());
                msg("Forward");
            }
            catch (IOException e)
            {
                msg("Error occurred when sending Data");
            }
        }
    }
    private void reserve() {
        if (socket!=null)
        {
            try
            {
                socket.getOutputStream().write("2".toString().getBytes());
                msg("Reverse");
            }
            catch (IOException e)
            {
                msg("Error occurred when sending Data");
            }
        }
    }
    private void right() {
        if (socket!=null)
        {
            try
            {
                socket.getOutputStream().write("3".toString().getBytes());
                msg("Right");
            }
            catch (IOException e)
            {
                msg("Error occurred when sending Data");
            }
        }
    }
    private void left() {
        if (socket!=null)
        {
            try
            {
                socket.getOutputStream().write("4".toString().getBytes());
                msg("Left");
            }
            catch (IOException e)
            {
                msg("Error occurred when sending Data");
            }
        }
    }
    private void stop() {
        if (socket!=null)
        {
            try
            {
                socket.getOutputStream().write("5".toString().getBytes());
                msg("Stop");
            }
            catch (IOException e)
            {
                msg("Error occurred when sending Data");
            }
        }
    }

        @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mtoggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
