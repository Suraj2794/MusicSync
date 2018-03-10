package com.example.suraj.projectdemo;

import android.content.Context;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinAct extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener{

    private final IntentFilter intentFilter = new IntentFilter();
    final HashMap<String, String> group = new HashMap<String, String>();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    private List<String> groupOwners=new ArrayList<>();
    private WifiP2pManager.PeerListListener peerListUpdate;
    private List<WifiP2pDevice> groupDevices=new ArrayList<WifiP2pDevice>();
    ArrayAdapter<String> adapter;
    WifiP2pConfig config = new WifiP2pConfig();
    WifiP2pGroup grp;
    ListView lv;
    WifiReceiver wifi;
    WifiP2pDnsSdServiceRequest serviceRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        lv=(ListView)findViewById(R.id.availableGroups);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mManager.requestConnectionInfo(mChannel,this);
        discoverService();
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });

        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                config.deviceAddress=groupDevices.get(i).deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(JoinAct.this,"connection created succesfully",1).show();
                    }

                    @Override
                    public void onFailure(int i) {

                    }
                });

            }
        });
    }



    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        InetAddress groupOwnerAddress = info.groupOwnerAddress;
        if(info.groupFormed && info.isGroupOwner) {

        }

    }









    private void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */

            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map record, WifiP2pDevice device) {
                Log.i("DnsSdTxtRecord " , record.toString());
                record.put(device.deviceAddress, record.get("name"));
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {

                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                resourceType.deviceName = group
                        .containsKey(resourceType.deviceAddress) ? group
                        .get(resourceType.deviceAddress) : resourceType.deviceName;

                // Add to the custom adapter defined specifically for showing
                // wifi devices.
                /*WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
                        .findFragmentById(R.id.frag_peerlist);
                WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
                        .getListAdapter());*/
                groupDevices.add(resourceType);
                groupOwners.add(resourceType.deviceName);
                adapter = new ArrayAdapter<String>(JoinAct.this, android.R.layout.simple_list_item_1, groupOwners);
                lv.setAdapter(adapter);
                lv.deferNotifyDataSetChanged();
                adapter.notifyDataSetChanged();
                Log.i("onBonjourService " , instanceName);
            }
        };
        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mManager.removeServiceRequest(mChannel,serviceRequest,null);

    }


}
