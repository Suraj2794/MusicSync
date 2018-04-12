package com.example.suraj.MusicFolder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.suraj.projectdemo.CreateGroup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SongService extends Service implements SeekBar.OnSeekBarChangeListener {
    MediaPlayer mp=new MediaPlayer();
    SeekBar seek;
    BroadcastReceiver reciever;
    Parcelable[] parc;
    Intent intent;
    ArrayList<MusicData> list;
    ServerSocket serverSocket,control_server;
    WifiManager wifiManager;
    /*AudioManager audio =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    AudioStream audioStream;*/
    String filePath;
    List<Socket> connectedClients=new ArrayList<Socket>();
    List<Socket> control_clients=new ArrayList<Socket>();
    Handler handler=new Handler();
    boolean isFirstTime=true;
    private static final String PLAY="C:PLAY";
    private static final String PAUSE="C:PAUSE";
    private static final String SEEKER="C:SEEKER";
    private static final String NEW="C:NEW";
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        intent=arg0;
        return null;
    }
    public SongService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub

        wifiManager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();   //Get all declared methods in WifiManager class
        boolean methodFound=false;
        for(Method method: wmMethods)
        {
            if(method.getName().equals("setWifiApEnabled"))
            {
                methodFound=true;
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = "SynMusic";
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                try {
                    Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled",  WifiConfiguration.class, boolean.class);
                    boolean apstatus=(Boolean) setWifiApMethod.invoke(wifiManager, netConfig,true);

                    Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
                    while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){};
                    Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
                    int apstate=(Integer)getWifiApStateMethod.invoke(wifiManager);
                    Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                    netConfig=(WifiConfiguration)getWifiApConfigurationMethod.invoke(wifiManager);
                    Log.e("CLIENT", "\nSSID:"+netConfig.SSID+"\nPassword:"+netConfig.preSharedKey+"\n");
                    new Broadcast_Song().start();
                    new Control_Server().start();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),1).show();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),1).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),1).show();
                }
            }
        }



        super.onCreate();
        seek= CreateGroup.seek;
        reciever=new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // TODO Auto-generated method stub

                String action=arg1.getAction();
                if(action.equals("com.example.musicplayer.play"))
                {
                    mp.start();
                    control_functions(PLAY);
                }
                else if(action.equals("com.example.musicplayer.pause"))
                {
                    mp.pause();
                    control_functions(PAUSE);
                }
                else if(action.equals("com.example.musicplayer.next"))
                {
                    try
                    {
                        String name=arg1.getStringExtra("name");
                        Uri uri = Uri.parse(name);//Environment.getExternalStorageDirectory().getPath()+"/songs/"+name);
                        mp.reset();
                        mp.setDataSource(arg0, uri);
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mp.setLooping(true);
                        mp.prepare();
                        mp.start();seek.setMax(mp.getDuration());
                    }catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                else if(action.equals("com.example.musicplayer.previous"))
                {
                    try
                    {
                        String name=arg1.getStringExtra("name");
                        Uri uri = Uri.parse(name);//Environment.getExternalStorageDirectory().getPath()+"/songs/"+name);
                        mp.reset();
                        mp.setDataSource(arg0, uri);
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mp.setLooping(true);
                        mp.prepare();
                        mp.start();
                        seek.setMax(mp.getDuration());
                    }catch (Exception e) {
                        // TODO: handle exception
                    }
                }

                else if(action.equals("com.example.musicplayer.frmList"))
                {
                    //Toast.makeText(SongService.this, "previous", 1).show();
                    try
                    {
                        String name=arg1.getStringExtra("name");
                        Uri uri = Uri.parse(name);//Environment.getExternalStorageDirectory().getPath()+"/songs/"+name);
                        filePath=uri.getPath();
                        //Toast.makeText(getApplicationContext(),name,1).show();
                        mp.reset();
                        mp.setDataSource(arg0, uri);
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mp.setLooping(true);
                        mp.prepare();
                        mp.start();
                        seek.setMax(mp.getDuration());
                        seek.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) arg0);
                        control_functions(NEW);
                        new sendSong().execute();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                else if(action.equals("com.example.musicplayer.frwd"))
                {
                    if(mp.getDuration()-mp.getCurrentPosition()>1250)
                    {
                        mp.seekTo(mp.getCurrentPosition()+1250);
                    }
                    else
                    {
                        mp.seekTo(mp.getDuration());
                    }
                }
                else if(action.equals("com.example.musicplayer.rvrse"))
                {
                    if(mp.getCurrentPosition()>1250)
                    {
                        mp.seekTo(mp.getCurrentPosition()-1250);
                    }
                    else
                    {
                        mp.seekTo(0);
                    }
                }
                else if(action.equals("com.example.musicplayer.jststarted"))
                {
                    if(mp.isPlaying())
                    {
                        CreateGroup.play_pause.setText("PAUSE");
                    }
                }
                else if(action.equals("com.example.musicplayer.seekBar"))
                {
                    seek=CreateGroup.seek;
                    seek.setMax(mp.getDuration());
                    seek.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) arg0);
                    //seek.setProgress(0);
                }
                else if(action.equals("com.example.musicplayer.addlist"))
                {
                    ArrayList<MusicData> list=arg1.getParcelableArrayListExtra("list");
                }
                else if(action.equals("com.example.musicplayer.seekBarComp"))
                {
                    try
                    {
                        String name=arg1.getStringExtra("name");
                        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/songs/"+name);
                        mp.reset();
                        mp.setDataSource(arg0, uri);
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mp.setLooping(true);
                        mp.prepare();
                        mp.start();seek.setMax(mp.getDuration());
                    }catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        };
        IntentFilter intentfilter=new IntentFilter();
        intentfilter.addAction("com.example.musicplayer.addlist");
        intentfilter.addAction("com.example.musicplayer.play");
        intentfilter.addAction("com.example.musicplayer.pause");
        intentfilter.addAction("com.example.musicplayer.pause");
        intentfilter.addAction("com.example.musicplayer.next");
        intentfilter.addAction("com.example.musicplayer.previous");
        intentfilter.addAction("com.example.musicplayer.frmList");
        intentfilter.addAction("com.example.musicplayer.frwd");
        intentfilter.addAction("com.example.musicplayer.rvrse");
        intentfilter.addAction("com.example.musicplayer.jststarted");
        intentfilter.addAction("com.example.musicplayer.seekBar");
        intentfilter.addAction("com.example.musicplayer.seekBarComp");
        registerReceiver(reciever, intentfilter);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(reciever);
        mp.pause();
        mp.stop();
        mp.release();
        wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
        wifiManager.removeNetwork(wifiManager.getConnectionInfo().getNetworkId());
        wifiManager.disconnect();
        try {

            serverSocket.close();
        }catch (Exception ex)
        {

        }
        super.onDestroy();

    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub
        mp.seekTo(seek.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub
        mp.seekTo(seek.getProgress());
    }

    class SeekThread extends Thread
    {
        public void run()
        {

            try
            {while(mp.getCurrentPosition()<=mp.getDuration())
            {

                sleep(1000);
                if(seek!=null) {
                    seek.setProgress(mp.getCurrentPosition());
                }
            }
            }
            catch (Exception e) {
                // TODO: handle exception

            }
        }
    }

    public void control_functions(String control_msg)
    {
        Toast.makeText(getApplicationContext(),control_msg,1).show();
        try {
            for (int i = 0; i < control_clients.size(); i++) {
                Socket soc = control_clients.get(i);
                OutputStream os = soc.getOutputStream();
                PrintStream ps=new PrintStream(os);
                ps.println(control_msg);
                ps.flush();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(),ex.getMessage(),1).show();
        }
    }
    class Broadcast_Song extends Thread
    {

        public Broadcast_Song()
        {
            try {
                serverSocket =new ServerSocket(6035,5);
                //serverSocket.bind(new InetSocketAddress(InetAddress.getByName("192.168.43.1"),6034));
                Toast.makeText(getApplicationContext(),"Socketbinded and listening",1).show();
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),ex.getMessage()+"",Toast.LENGTH_LONG).show();
                //Log.d("msg",ex.getMessage()+"");
            }
        }

        @Override
        public void run() {
            super.run();
            while(true)
            {
                try {
                    Socket socket=serverSocket.accept();
                    Log.d("msg_server",socket.toString());
                    System.out.println("msg"+socket.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Socket created",1).show();
                        }
                    });
                    DataInputStream pw=new DataInputStream(socket.getInputStream());
                    DataOutputStream dw=new DataOutputStream(socket.getOutputStream());
                    dw.writeUTF("Hello User");
                    connectedClients.add(socket);
                    //dw.close();
                }
                catch (Exception ex)
                {
                    //final  Exception ex1=ex;
                    /*handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),ex1.getMessage()+"",Toast.LENGTH_LONG).show();
                        }
                    });*/
                    ex.printStackTrace();
                     Log.d("msg",ex.getMessage()+"");
                }
            }
        }
    }

    class sendSong extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mp.pause();
            Toast.makeText(getApplicationContext(), "paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mp.start();
            Toast.makeText(getApplicationContext(), "play", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                File file=new File(filePath);

                byte [] mybytearray  = new byte [(int)file.length()];
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(mybytearray,0,mybytearray.length);
                //os = sock.getOutputStream();
                Log.i("message","Sending " + filePath + "(" + mybytearray.length + " bytes)");
                for (int i = 0; i < connectedClients.size(); i++) {
                    Socket soc = connectedClients.get(i);
                    OutputStream os = soc.getOutputStream();
                    os.write(mybytearray,0,mybytearray.length);
                    os.flush();
                    //os.close();
                    System.out.println("Done.");
                }
                /*bis.close();
                fis.close();*/

            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
            return null;
        }


    }

    class Control_Server extends Thread
    {
        public Control_Server()
        {
            try {
                control_server = new ServerSocket(6099);
                Toast.makeText(getApplicationContext(),"Socketbinded and listening",1).show();
            }catch (Exception ex)
            {

                Toast.makeText(getApplicationContext(),ex.getMessage()+"",1).show();
            }
        }

        @Override
        public void run() {
            super.run();
            while(true)
            {
                try {
                    //sleep(2500);
                    Socket soc=control_server.accept();
                    control_clients.add(soc);
                }catch (Exception ex)
                {

                }
            }
        }
    }
}
