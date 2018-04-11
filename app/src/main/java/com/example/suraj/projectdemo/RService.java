package com.example.suraj.projectdemo;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class RService extends Service {

    Socket sc;
    MediaPlayer mp=new MediaPlayer();
    File file;
    Handler handler=new Handler();
    public RService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Rsocket().start();
    }

    class Rsocket extends Thread
    {
        public Rsocket()
        {
            try {

            }catch (Exception ex)
            {
                //Toast.makeText(getApplicationContext(),ex.getMessage()+"",1).show();
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            try
            {
                //sc.connect("192.168.43.1", 6034);
                sleep(2000);
                sc = new Socket("192.168.43.1", 6035);

                //BufferedReader in=new BufferedReader(new InputStreamReader(sc.getInputStream()));
                DataInputStream dis=new DataInputStream(sc.getInputStream());
                Log.d("msg1",dis.readLine());
                //Toast.makeText(getApplicationContext(),"dhdj",1).show();
               handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new RecieveFile().execute();
                    }
                });
            }catch (Exception ex)
            {
                //Toast.makeText(getApplicationContext(),ex.getMessage()+"",1).show();
                final Exception ex1=ex;
                ex.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),ex1.getMessage(),1).show();
                    }
                });
            }
        }
    }

    class RecieveFile extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            file=new File(Environment.getExternalStorageDirectory(),"temp.mp3");
            if(file.exists()){
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(),"file created ",1).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Toast.makeText(getApplicationContext(),"Playing Music",1).show();
                mp.reset();
                //mp.setDataSource(arg0, uri);
                mp.setDataSource(file.getPath());
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setLooping(true);
                mp.prepare();
                mp.start();
            }
            catch (Exception ex)
            {
                Log.d("msg",ex.getMessage());
                //Toast.makeText(getApplicationContext(),ex.getMessage()+"",1).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
////              /*ByteBuffer bytes=ByteBuffer.allocate(1024*1024*10);
//                byte[] bytes = new byte[1024*1024*10];
//                InputStream is = sc.getInputStream();
//                BufferedReader br=new BufferedReader(new InputStreamReader(is));
//                FileOutputStream fos=new FileOutputStream(file);
//                PrintStream ps=new PrintStream(fos);
//                String temp="";
//                while(null!=(temp=br.readLine()))
//                {
//                    ps.print(temp.getBytes());
//                }*/


                 int FILE_SIZE = 1024 * 1024 *20;
                // receive file
                byte [] mybytearray  = new byte [FILE_SIZE];
                InputStream is = sc.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int bytesRead = is.read(mybytearray, 0, mybytearray.length);
                int current = bytesRead;

                Log.e("nnnnnnnn", "File " + file.getPath()
                        + "started receiving");

                int n = 0;
                do {
                    bytesRead =
                            is.read(mybytearray, current, (mybytearray.length-current));
                    if(bytesRead >= 0) current += bytesRead;

                    //if(n % 10 == 0){
                        Log.e("nnnnnnnn", " " + current);
                    //}

//                    bos.write(mybytearray, 0 , current);
                    n++;
                } while(bytesRead > -1 && current <= 1000000);

                bos.write(mybytearray, 0 , current);
                bos.flush();
                System.out.println("File " + file.getPath()
                        + " downloaded (" + current + " bytes read)");

            }
            catch (Exception ex)
            {
                Log.d("error",ex.getMessage()+":");
            }
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.pause();
        mp.stop();
    }
}


