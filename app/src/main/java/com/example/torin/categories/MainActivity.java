package com.example.torin.categories;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.physical_web.physicalweb.NearbyBeaconsFragment;
import org.physical_web.physicalweb.NearbyBeaconsFragment.NearbyBeaconsAdapter;
import org.physical_web.physicalweb.ScreenListenerService;

public class MainActivity extends AppCompatActivity
{
    ListView listView;
    NearbyBeaconsFragment fragment;
    NearbyBeaconsAdapter adapter;
    public final static String URL = "http://ecs.utdallas.edu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = this.findViewById(android.R.id.content);
        listView = (ListView) view.findViewById(R.id.listView2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check device for bluetooth
        BluetoothManager btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager != null ? btManager.getAdapter() : null;
        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    R.string.error_bluetooth_support, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ensureBluetoothIsEnabled(btAdapter);
        getFragmentManager().beginTransaction()
                .add(NearbyBeaconsFragment.newInstance(), "NBF")
                .commit();
        getFragmentManager().executePendingTransactions();
        fragment = ((NearbyBeaconsFragment) getFragmentManager().findFragmentByTag("NBF"));
        adapter = fragment.getAdapter();
        adapter.notifyDataSetChanged();
        Intent intent = new Intent(this, ScreenListenerService.class);
        startService(intent);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                intent.putExtra(MainActivity.URL, adapter.getList().getItem(position).getUrl());
                try {
                    startActivity(intent);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ensureBluetoothIsEnabled(BluetoothAdapter bluetoothAdapter) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
    }

    public void onFilter(View view)
    {
        Button button = (Button) view;
        String category = button.getText().toString();
        adapter.getFilter().filter(category);
    }
}
