package com.example.suraj.projectdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.suraj.MusicFolder.MusicData;
import com.example.suraj.MusicFolder.SongService;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class RecieveService extends Service {

    String ownerIpAddress;
    Socket clientSocket;
    BroadcastReceiver reciever;
    public RecieveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ownerIpAddress=intent.getStringExtra("server_address");
        RecieveMsg rcm=new RecieveMsg(ownerIpAddress);
        rcm.start();
        Toast.makeText(getApplicationContext(), "conn", 1).show();
        return super.onStartCommand(intent, flags, startId);
    }

    class RecieveMsg extends Thread{
        String address;
        Socket clientSocket;
        BufferedReader bf;
        public RecieveMsg(String address)
        {
            this.address=address;
        }

        public void run()
        {
            try
            {
                Log.d("msg","ClientSocket");
                clientSocket=new Socket("192.168.49.1",5599);
                Log.d("msg","connected");
                bf=new BufferedReader(new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) ;
                String msg=bf.readLine();
                //Toast.makeText(getApplicationContext(),msg,1).show();
                Log.d("msg",msg);
            }
            catch (Exception ex)
            {

            }
        }
    }
}
