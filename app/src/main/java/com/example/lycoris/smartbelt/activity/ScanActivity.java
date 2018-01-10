package com.example.lycoris.smartbelt.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lycoris.smartbelt.R;

import java.util.ArrayList;

/**
 * Created by Eclair D'Amour on 2016/8/28.
 */
public class ScanActivity extends ListActivity {
    // Fire an intent request code to open bluetooth switch
    private static final int REQUEST_ENABLE_BT=1;

    // The tag of the extra data in intent
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    // Construct the object of the adpater which is defined as inner class
    private LeDeviceListAdapter leDeviceListAdapter;

    // Construct the bluetooth adpater to catch the information of the bluetooth advice
    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;

    private Handler handler;

    // Device scanning callback
    private BluetoothAdapter.LeScanCallback leScanCallback=
            new  BluetoothAdapter.LeScanCallback(){

                @Override
                public void onLeScan(final BluetoothDevice bluetoothDevice,
                                     int rssi, byte[] scanRecord){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            leDeviceListAdapter.addDevice(bluetoothDevice);
                            leDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().setTitle(R.string.title_devices);
        handler=new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    // Setup the top menu to control the scanning motion
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        if(!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            /*test*/
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }else{
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_scan_intermediate);
        }
        return true;
    }

    // Setup the options of the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            //clear all temp devices and restart the scan method
            case R.id.menu_scan:
                leDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
            default:
                break;
        }
        return true;
    }

    // After onCreate method is execuate
    @Override
    protected void onResume(){
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Initializes list view adapter.
        leDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(leDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        leDeviceListAdapter.clear();
    }

    // set the listener of the listitem and build the intent to switch to the mainactivity
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        final BluetoothDevice device = leDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(ScanActivity.this, MainFragmentActivity.class);
        intent.putExtra(EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            bluetoothAdapter.stopLeScan(leScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }

    // scanning method
    private void scanLeDevice(final boolean enable){
        if(enable){
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning=false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    invalidateOptionsMenu();
                }
            },SCAN_PERIOD);

            mScanning=true;

            /*
            If the SDK is up API 18
            change the start action to
            bluetoothAdapter.getBluetoothLeScanner().startScan(leScanCallback);
             */
            bluetoothAdapter.startLeScan(leScanCallback);
        }else{
            mScanning=false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
        invalidateOptionsMenu();
    }

    static class ViewHolder{
        TextView deviceName;
        TextView deviceAddress;
    }

    private class LeDeviceListAdapter extends BaseAdapter{
        private ArrayList<BluetoothDevice> mLeDevices;

        private LayoutInflater mInflator;

        public LeDeviceListAdapter(){
            super();
            mLeDevices=new ArrayList<BluetoothDevice>();
            mInflator=ScanActivity.this.getLayoutInflater();
        }

        // Addtion method of adpater
        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        //Getter method of adpater
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        // Some useful methods to get the information of the device list
        public void clear() {
            mLeDevices.clear();
        }
        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.listitem_scan, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.txt_scanitem_deviceaddress);
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.txt_scanitem_devicename);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BluetoothDevice bluetoothDevice=mLeDevices.get(position);
            final String deviceName=bluetoothDevice.getName();

            if(deviceName!=null&& deviceName.length()>0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText("Unknown device");
            viewHolder.deviceAddress.setText(bluetoothDevice.getAddress());

            return convertView;
        }


    }


}
