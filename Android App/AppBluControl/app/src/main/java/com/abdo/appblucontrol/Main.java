package com.abdo.appblucontrol;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class Main extends AppCompatActivity {
    public static final String EXTRA_ADDRESS = "Device_Address" ;
    ImageView search_bt;
    TextView statustextView,paired;
    TextView bt;
    ListView listView;
    Button searchButton;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private OutputStream outputStream = null;
    ArrayList<String> stringArrayList;
    ArrayAdapter<String> arrayAdapter;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action",action);
            if (myBluetooth.ACTION_DISCOVERY_FINISHED.equals(action)){
                statustextView.setText("Finish");
                searchButton.setEnabled(true); } }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        search_bt = findViewById(R.id.search);
        bt = findViewById(R.id.bt);
        searchButton = findViewById(R.id.searchButton);
        listView =findViewById(R.id.listView);
        statustextView = findViewById(R.id.statustextView);
        paired = findViewById(R.id.paired);
     //Enable Bluetooth
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
        startActivity(discoverableIntent);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statustextView.setText("Searching.....");
                pairedDevicesList(); //method that will be called
            }
        });
    }
    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent i = new Intent(Main.this, Direction.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS,address); //this will be received at Direction (class) Activity
            startActivity(i);

        }
    };
}
