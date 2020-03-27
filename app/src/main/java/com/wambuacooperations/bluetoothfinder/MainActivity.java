package com.wambuacooperations.bluetoothfinder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button searchButton;
    TextView statusTextView;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> devices =new ArrayList<>();
    ArrayList<String> addresses =new ArrayList<>();
    ArrayAdapter adapter;

    AlertDialog.Builder alertDialog;
    private final BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            Log.i("Action",action);

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                statusTextView.setText("Finished");
                searchButton.setEnabled(true);
                if(devices.isEmpty()){
                    alertDialog
                            .setTitle("Sorry!")
                            .setMessage("No bluetooth devices found")
                            .show();
                }
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)){  //A bluetooth device was found
                //Extract bluetooth device
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name=device.getName();
                String address=device.getAddress();

                //Getting the RSSI- to find out how strong our signal is to the device
                String rssi=Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));

//                Log.i("Device found","Name: "+name+" Address: "+address+" RSSI: "+rssi);

                if(!addresses.contains(address)){
                    addresses.add(address);
                    String deviceString="";
                    if (name==null||name.equals("")){
                        deviceString =address+" --RSSI "+ rssi +"dBm";
                    }
                    else{
                        deviceString=name+" --RSSI "+ rssi +"dBm";
                    }

                    devices.add(deviceString);
                    adapter.notifyDataSetChanged();
                }


            }
        }
    };


    public void searchClicked(View view){
        devices.clear();
        addresses.clear();
        statusTextView.setText("Searching...");
        searchButton.setEnabled(false);

        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listView);
        searchButton=findViewById(R.id.searchButton);
        statusTextView=findViewById(R.id.statusTextView);

        alertDialog=new AlertDialog.Builder(this);

        adapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,devices);
        listView.setAdapter(adapter);


        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(broadcastReceiver,intentFilter);

    }
}
