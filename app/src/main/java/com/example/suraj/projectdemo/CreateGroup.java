package com.example.suraj.projectdemo;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroup extends AppCompatActivity implements WifiP2pManager.GroupInfoListener {
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    private List<WifiP2pDevice> peersList=new ArrayList<WifiP2pDevice>();
    private WifiP2pManager.PeerListListener peerListUpdate;
    ArrayAdapter<WifiP2pDevice> adapter;
    WifiP2pConfig config = new WifiP2pConfig();
    WifiP2pGroup grp;
    ListView lv;
    WifiReceiverForGroup wifi;
    WifiP2pDnsSdServiceInfo serviceInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        lv=(ListView)findViewById(R.id.connectedPeers);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        config.groupOwnerIntent=15;
        startRegistration();
        //startRegistration();
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });

        peerListUpdate = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList p2pList) {
                List<WifiP2pDevice> receivedList= new ArrayList<WifiP2pDevice>(p2pList.getDeviceList());
                /*
                peersList.clear();
                peersList.addAll(receivedList);
                adapter = new ArrayAdapter<WifiP2pDevice>(CreateGroup.this,android.R.layout.simple_list_item_1,receivedList);
                lv.setAdapter(adapter);
                lv.deferNotifyDataSetChanged();*/

            }
        };

    }



    private void startRegistration() {
        //  Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("listenport", String.valueOf(2341));
        record.put("name", "Suraj" + 2794);
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.

                mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(CreateGroup.this,"Group created succesfully",1).show();

                        mManager.requestGroupInfo(mChannel,CreateGroup.this);
                    }

                    @Override
                    public void onFailure(int i) {

                        Log.i("errorGroup",i+"");
                        Toast.makeText(CreateGroup.this,"Group not created succesfully---"+i,1).show();
                    }
                });
            }

            @Override
            public void onFailure(int arg0) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        wifi=new WifiReceiverForGroup(mManager,mChannel,this,peerListUpdate);
        registerReceiver(wifi,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifi);
        mManager.removeGroup(mChannel,null);
        mManager.removeLocalService(mChannel,serviceInfo,null);
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
        if(wifiP2pGroup!=null) {
            peersList.addAll(wifiP2pGroup.getClientList());
            adapter = new ArrayAdapter<WifiP2pDevice>(CreateGroup.this, android.R.layout.simple_list_item_1, peersList);
            lv.setAdapter(adapter);
            lv.deferNotifyDataSetChanged();
        }
    }
}
