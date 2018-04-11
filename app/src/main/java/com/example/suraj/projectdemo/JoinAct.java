package com.example.suraj.projectdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinAct extends AppCompatActivity {

    private final IntentFilter intentFilter = new IntentFilter();
    final HashMap<String, String> group = new HashMap<String, String>();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    private List<String> groupOwners = new ArrayList<>();
    private WifiP2pManager.PeerListListener peerListUpdate;
    private List<WifiP2pDevice> groupDevices = new ArrayList<WifiP2pDevice>();
    ArrayAdapter<String> adapter;
    WifiP2pConfig config = new WifiP2pConfig();
    WifiP2pGroup grp;
    ListView lv;
    WifiReceiver wifi;
    WifiP2pDnsSdServiceRequest serviceRequest;
    WifiP2pManager.ConnectionInfoListener info;
    Button ds;
    Intent intent1;
    ////////////////////////////////////////////////////////////////////////////////////////
    WifiManager manager;
    List<ScanResult> wifiScanResultList;
    List<ScanResult> requiredList = new ArrayList<>();
    List<String> names = new ArrayList<>();
    WifiConfiguration wconf = new WifiConfiguration();
    private static boolean CONNECTED = false;
    private static final String WIFI_SSID = "SynMusic";
    int res;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = (ListView) findViewById(R.id.availableGroups);
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        manager.setWifiEnabled(true);
        manager.startScan();

        filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
                        && !CONNECTED) {
                    wifiScanResultList = manager.getScanResults();
                    for (ScanResult scanResult : wifiScanResultList) {
                        if (scanResult.SSID.equals(WIFI_SSID )) {
                            requiredList.add(scanResult);
                            names.add(scanResult.SSID);
                        }
                    }

                    adapter = new ArrayAdapter<String>(JoinAct.this, android.R.layout.simple_list_item_1, names);
                    lv.setAdapter(adapter);
                    lv.deferNotifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                    for (int i = 0; i < wifiScanResultList.size(); i++) {
                        String hotspot = (wifiScanResultList.get(i)).toString();
                        Log.d("msg", hotspot);
                    }

                }
            }
        }, filter);



        adapter = new ArrayAdapter<String>(JoinAct.this, android.R.layout.simple_list_item_1, names);
        lv.setAdapter(adapter);
        lv.deferNotifyDataSetChanged();
        adapter.notifyDataSetChanged();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                wconf.SSID = requiredList.get(i).SSID;
                wconf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wconf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wconf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wconf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                res = manager.addNetwork(wconf);
                WifiConfiguration tempConf = null;
                for (WifiConfiguration conf : manager.getConfiguredNetworks()) {
                    if (null != conf.SSID
                            && ("\"" + WIFI_SSID + "\"").equalsIgnoreCase(conf.SSID)) {
                        tempConf = conf;
                    }
                }
                manager.setWifiEnabled(true);
                manager.disconnect();
                manager.enableNetwork(tempConf.networkId, true);
                manager.reconnect();
                IntentFilter filter=new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);

                if(manager.getConnectionInfo()!=null)
                {
                    if(manager.getConnectionInfo().getSSID().equals("\"" + WIFI_SSID + "\"") || manager.getConnectionInfo().getSSID().equals( WIFI_SSID ))
                    {
                    }
                }
                CONNECTED = true;
                intent1=new Intent(JoinAct.this,RService.class);
                startService(intent1);
            }
        });
        ds = (Button) findViewById(R.id.ds);
        ds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                manager.disconnect();
                manager.removeNetwork(res);
                manager.setWifiEnabled(false);
                if(intent1!=null) {
                    stopService(intent1);
                }
                CONNECTED=false;
                //unregisterReceiver();
            }
        });

    }

@Override
protected void onPause(){
        super.onPause();
        //mManager.removeServiceRequest(mChannel,serviceRequest,null);
        }
}
