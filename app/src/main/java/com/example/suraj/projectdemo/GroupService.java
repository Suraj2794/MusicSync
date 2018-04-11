package com.example.suraj.projectdemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.rtp.RtpStream;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupService extends Service implements WifiP2pManager.GroupInfoListener {
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    private List<WifiP2pDevice> peersList=new ArrayList<WifiP2pDevice>();
    private WifiP2pManager.PeerListListener peerListUpdate;
    ArrayAdapter<WifiP2pDevice> adapter;
    WifiP2pConfig config = new WifiP2pConfig();
    WifiP2pGroup grp;
    //ListView lv;
    WifiReceiverForGroup wifi;
    WifiP2pDnsSdServiceInfo serviceInfo;
    ServerSocket serverSocket;
    ArrayList<Socket> connectedClients=new ArrayList<Socket>();
    String ipaddress;
    public GroupService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        config.groupOwnerIntent=15;
        startRegistration();

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
                peersList.clear();
                peersList.addAll(receivedList);
                adapter = new ArrayAdapter<WifiP2pDevice>(getApplicationContext(),android.R.layout.simple_list_item_1,peersList);
                //CreateGroup.lv.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
                //Toast.makeText(getApplicationContext(),p2pList.getDeviceList().toString(),1).show();

            }
        };

        wifi=new WifiReceiverForGroup(mManager,mChannel,peerListUpdate);
        registerReceiver(wifi,intentFilter);
        /*mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList p2pList) {
                List<WifiP2pDevice> receivedList= new ArrayList<WifiP2pDevice>(p2pList.getDeviceList());

                peersList.clear();
                peersList.addAll(receivedList);
                adapter = new ArrayAdapter<WifiP2pDevice>(getApplicationContext(),android.R.layout.simple_list_item_1,receivedList);
                CreateGroup.lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifi);
        mManager.removeGroup(mChannel,null);
        mManager.removeLocalService(mChannel,serviceInfo,null);
        try {
            serverSocket.close();
        }
        catch(Exception ex)
        {
            Log.d("msg",ex.getMessage());
        }
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
                        Toast.makeText(getApplicationContext(),"Group created succesfully",1).show();

                        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                            @Override
                            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {

                            }


                        });

                        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                            @Override
                            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                                if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner)
                                {
                                    ipaddress=wifiP2pInfo.groupOwnerAddress.getHostAddress();
                                    AcceptSocketConnections acp=new AcceptSocketConnections(ipaddress);
                                    acp.start();
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(int i) {

                        Log.i("errorGroup",i+"");
                        Toast.makeText(getApplicationContext(),"Group not created succesfully---"+i,1).show();
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
    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {

    }

    class AcceptSocketConnections extends Thread
    {
        String ipAddress;
        public AcceptSocketConnections(String ipAddress)
        {
            this.ipAddress=ipAddress;
        }

        @Override
        public void run() {
            try {
                InetAddress addr = InetAddress.getByName(ipAddress);
                serverSocket = new ServerSocket(5599,5000,addr);

                Log.d("msg","SocketCreated"+serverSocket.getInetAddress());
                //Toast.makeText(getApplicationContext(),"ServerCreated",1).show();
                while(true)
                {
                    if(serverSocket!=null && !serverSocket.isClosed()) {

                        Log.d("msg","socketListening");
                        Socket client = serverSocket.accept();

                        //Toast.makeText(getApplicationContext(),"ConnectionAccepted",1).show();

                        Log.d("msg", "ClientCOnnected");
                        connectedClients.add(client);
                        PerClient perClient = new PerClient(client);
                        perClient.start();
                    }
                    else
                    {
                        Log.d("msg","failed");
                    }
                } }
            catch(Exception ex){
                Log.d("msg",ex.getMessage());
                //Toast.makeText(getApplicationContext(),"ConnectionAccepted",1).show();
            }
        }
    }
    class PerClient extends Thread
    {
        Socket client;
        MediaPlayer mp;
        public PerClient(Socket client)
        {
            this.client=client;
            Toast.makeText(getApplicationContext(),"ClientThreadCreated",1).show();
            //this.mp=mp;
        }

        @Override
        public void run() {
            try {
                PrintWriter pw=new PrintWriter(client.getOutputStream());
                pw.print("Hello");
                pw.flush();
            }catch (Exception ex){}
        }
    }
}
