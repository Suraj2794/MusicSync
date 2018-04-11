package com.example.suraj.projectdemo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.suraj.MusicFolder.MusicAdapter;
import com.example.suraj.MusicFolder.MusicData;
import com.example.suraj.MusicFolder.SongService;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroup extends AppCompatActivity implements WifiP2pManager.GroupInfoListener,
        android.widget.AdapterView.OnItemClickListener,SeekBar.OnSeekBarChangeListener,MediaPlayer.OnSeekCompleteListener {
    private final IntentFilter intentFilter = new IntentFilter();

    public static ListView lv;
    File file;
    Cursor musicCursor;
    int currentPosition = 0;
    int total = 100;
    ArrayList<MusicData> song_list;
    LayoutInflater inflater;
    int position;
    Button back, previous,  next, ffd;
    public static Button play_pause;
    public static SeekBar seek;
    Intent intentService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        //lv=(ListView)findViewById(R.id.connectedPeers);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //intentService=new Intent(this,HotSpotService.class);
        //startService(intentService);
        inflater = getLayoutInflater();
        ffd=(Button)findViewById(R.id.dg);

        lv = (ListView) findViewById(R.id.lv);
        seek=(SeekBar)findViewById(R.id.seek);
        seek.setOnSeekBarChangeListener(this);
        song_list = new ArrayList<MusicData>();
        previous = (Button) findViewById(R.id.prev);
        play_pause = (Button) findViewById(R.id.play);
        next = (Button) findViewById(R.id.next);
		/*
		 * if(!mp.isPlaying()) { for (int i = 0; i < controls.size(); i++) {
		 * controls.get(i).setEnabled(false); } }
		 */
        storeMusicRecord();
		/*Intent inte = new Intent("com.example.musicplayer.addlist");
		inte.putExtra("list", song_list);
		sendBroadcast(inte);*/
        MusicAdapter adapter = new MusicAdapter(inflater, song_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        final Intent intent1 = new Intent(this, SongService.class);
        startService(intent1);
        Intent strt=new Intent("com.example.musicplayer.jststarted");
        sendBroadcast(strt);
        Intent seekBar=new Intent("com.example.musicplayer.seekBar");
        sendBroadcast(seekBar);
        ffd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stopService(intentService);
                stopService(intent1);
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        /*wifi=new WifiReceiverForGroup(mManager,mChannel,this,peerListUpdate);
        registerReceiver(wifi,intentFilter);*/
    }

    @Override
    protected void onPause() {
        super.onPause();/*
        unregisterReceiver(wifi);
        mManager.removeGroup(mChannel,null);
        mManager.removeLocalService(mChannel,serviceInfo,null);*/
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
        if(wifiP2pGroup!=null) {
            /*peersList.addAll(wifiP2pGroup.getClientList());
            adapter = new ArrayAdapter<WifiP2pDevice>(CreateGroup.this, android.R.layout.simple_list_item_1, peersList);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();*/
        }
    }




    public void storeMusicRecord() {
        ContentResolver musicResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        musicCursor = musicResolver.query(uri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            try {
                int idColumn = musicCursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA);
                int artistColumn = musicCursor
                        .getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int nameColumn = musicCursor
                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                // add songs to list
                do {
                    String path = musicCursor.getString(idColumn);
                    String thisName = musicCursor.getString(nameColumn);
                    String thisArtist = musicCursor.getString(artistColumn);

                    MusicData md = new MusicData(path, thisName, thisArtist);
                    song_list.add(md);
                    // Toast.makeText(this, ""+thisId, 0).show();
                } while (musicCursor.moveToNext());
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
		/*
		 * else { Toast.makeText(this, "No mp3 file found", 1).show(); }
		 */
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
		/*
		 * ps=new PlaySong(this,song_list,arg2,controls,seek,mp);
		 * ps.play_Song();
		 */
        play_pause.setText("PAUSE");
        position=arg2;
        Intent intent = new Intent("com.example.musicplayer.frmList");
        intent.putExtra("name", song_list.get(arg2).getPath());
        sendBroadcast(intent);
    }

    public void onPre(View view) {

        Intent intent=new Intent("com.example.musicplayer.rvrse");
        sendBroadcast(intent);
    }

    public void onPrevious(View view) {
        if(position>0)
        {
            position-=1;
            Intent intent = new Intent("com.example.musicplayer.previous");
            intent.putExtra("name", song_list.get(position).getPath());
            play_pause.setText("PAUSE");
            sendBroadcast(intent);
        }
    }

    public void onPlay(View view) {

        if (play_pause.getText().equals("PLAY")) {
            Intent intent = new Intent("com.example.musicplayer.play");
            sendBroadcast(intent);
            play_pause.setText("PAUSE");
        } else {
            Intent intent = new Intent("com.example.musicplayer.pause");
            sendBroadcast(intent);
            play_pause.setText("PLAY");
        }
    }

    public void onNext(View view) {
		/*
		 * if(ps!=null) { ps.onNext(); }
		 */
        if(position<song_list.size()-1)
        {
            position+=1;
            Intent intent = new Intent("com.example.musicplayer.next");
            intent.putExtra("name", song_list.get(position).getPath());
            play_pause.setText("PAUSE");
            sendBroadcast(intent);
        }
    }

    public void onForward(View view) {
		/*
		 * if(ps!=null) { ps.onForward(); }
		 */
        Intent intent = new Intent("com.example.musicplayer.frwd");
        sendBroadcast(intent);
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        // TODO Auto-generated method stub

        Toast.makeText(this, "Complete", 0).show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSeekComplete(MediaPlayer arg0) {
        // TODO Auto-generated method stubif(position<song_list.size()-1)
        Toast.makeText(this, "Complete", 0).show();
        if(position<song_list.size()-1)
        {
            position+=1;
            Intent intent = new Intent("com.example.musicplayer.seekBarComp");
            intent.putExtra("name", song_list.get(position).getTitle());
            sendBroadcast(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicCursor.close();
    }
}
