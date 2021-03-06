package com.freecsarsalaan99.learningbluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;
    Button btnEnableDisable_Discoverable;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;


   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
    ...

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    } */

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver  mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {

                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;

                }

            }
        }
    };

    private final BroadcastReceiver  mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {


                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in dicoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                      Toast toast1 =  Toast.makeText(getApplicationContext()," Discoverability Enabled.",Toast.LENGTH_SHORT);
                      toast1.setGravity(Gravity.CENTER,0,300);
                      toast1.show();
                        break;
                    //Device is not in dicoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled. Able to receive Connections.");
                        Toast toast2 =  Toast.makeText(getApplicationContext()," Discoverability Enabled. Able to receive Connections.",Toast.LENGTH_SHORT);
                        toast2.setGravity(Gravity.CENTER,0,300);
                        toast2.show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not Able to receive Connections.");
                        Toast toast3 =  Toast.makeText(getApplicationContext()," Discoverability Disabled. Not Able to receive Connections.",Toast.LENGTH_SHORT);
                        toast3.setGravity(Gravity.CENTER,0,300);
                        toast3.show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                      Toast toast4 = Toast.makeText(getApplicationContext()," Connecting....",Toast.LENGTH_SHORT);
                        toast4.setGravity(Gravity.CENTER,0,300);
                        toast4.show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        Toast toast5 = Toast.makeText(getApplicationContext()," Connected.",Toast.LENGTH_SHORT);
                        toast5.setGravity(Gravity.CENTER,0,300);
                        toast5.show();
                        break;

                }
            }
        }
        };


    // broadcast receiver for listing devices that are not paired yet. executed by btnDiscover() method.


    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
              Toast toast =  Toast.makeText(getApplicationContext(),"Device Found.",Toast.LENGTH_SHORT);
              toast.setGravity(Gravity.CENTER,0,150);
              toast.show();
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

   private  final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String PIN = "0000";

                try {
                 byte[] pin;
                    pin = (byte[])BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, PIN);
                    BluetoothDevice.class.getMethod("setPin", byte[].class).invoke(mDevice, (Object) pin);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
                    e.printStackTrace();
                }

                //case 1 already bonded
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED. ");
                }
                //case 2 creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING. ");
                }
                //case 3 breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };




    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnONOFF = findViewById (R.id.btnONOFF);
        btnEnableDisable_Discoverable = findViewById(R.id.btnDiscoverable_on_off);
        lvNewDevices = findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        // broadcast when bond change pairing
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4,filter);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices.setOnItemClickListener(MainActivity.this);

        btnONOFF.setOnClickListener(view -> {
                    Log.d(TAG, "onClick: Enanling/Disabling Bluetooth.");
                    enableDisableBT();
                });
    }



    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does Not Have/Support Bluetooth");
            Toast.makeText(getApplicationContext()," Does Not Have/Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: Enabling Bluetooth.");
            Toast.makeText(getApplicationContext(),"Enabling Bluetooth.",Toast.LENGTH_SHORT).show();
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: Disabling Bluetooth.");
            Toast.makeText(getApplicationContext(),"Disabling Bluetooth.",Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }




    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making your device discoverable for 300 seconds.");
        Toast.makeText(getApplicationContext()," Making your device discoverable for 300 seconds.",Toast.LENGTH_SHORT).show();
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);
    }

    public void btnDiscover (View view){
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Cancelling Discovery.");
            Toast.makeText(getApplicationContext()," Cancelling Discovery.",Toast.LENGTH_SHORT).show();
            // check permission in manifest
            checkBTPermissions();
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }

         else if(!mBluetoothAdapter.isDiscovering()){
            checkBTPermissions();
            mBluetoothAdapter.startDiscovery();
             Log.d(TAG, "btnDiscover: Start Discovery.");
             Toast.makeText(getApplicationContext()," Start Discovery.",Toast.LENGTH_SHORT).show();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    private void checkBTPermissions() {
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if(permissionCheck !=0){
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1001); // Any Number
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override

    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        //first cancel discovery   i=position  id=l
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked a Device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: " + deviceName);
        Log.d(TAG, "onItemClick: " + deviceAddress);




    }


}
