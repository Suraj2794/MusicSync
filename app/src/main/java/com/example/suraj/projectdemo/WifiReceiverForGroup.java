package com.example.suraj.projectdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

public class WifiReceiverForGroup extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private CreateGroup mActivity;
    private WifiP2pManager.PeerListListener peerListener;
    private WifiP2pManager.ConnectionInfoListener connectionListener;
    public WifiReceiverForGroup(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                CreateGroup activity, WifiP2pManager.PeerListListener peerListener) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.peerListener=peerListener;
    }

    public WifiReceiverForGroup(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                CreateGroup activity, WifiP2pManager.ConnectionInfoListener connectionListener) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.connectionListener=connectionListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers

            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

    }
}
