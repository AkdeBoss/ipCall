package envoyone.ak.ace.completevoicecall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Callscreen extends ActionBarActivity {


    public String callerIP;
Button dissconnect;
    TextView callee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callscreen);
dissconnect=(Button) findViewById(R.id.button_dissconnect);


   callerIP= getIntent().getExtras().getString("callerIp");
        Log.d("Oncreate","caller= "+callerIP);

callee =(TextView) findViewById(R.id.textViewCallerIpNew);

dissconnect.setOnClickListener(dissconnector);

       startStreaming();

        startReceiving();



    }


    volatile boolean stop = false;


    View.OnClickListener dissconnector = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            status=false;
            stop= true;

            finish();



        }
    };













    private int port = 1127;         //which port??
     private int sampleRate = 44100;//11025;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;
    public byte[] buffer;
    public static DatagramSocket socket;
    AudioRecord recorder;
    String contactIps;




    private AudioTrack speaker;



    public void startReceiving() {

        final Thread receiveThread = new Thread (new Runnable() {

            @Override
            public void run() {


                    try {


                        final DatagramSocket socket = new DatagramSocket(1127);
                        Log.d("VR", "Socket Created");

/*
                        runOnUiThread(new Runnable() {
                            public void run() {
callee.setText(callerIP);

                                            }
                                        });
*/





                                //minimum buffer size. need to be careful. might cause problems. try setting manually if any problems faced
                        int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                        byte[] buffer = new byte[6000];
                        speaker = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate, channelConfig, audioFormat, minBufSize, AudioTrack.MODE_STREAM);


                        while (status == true) {
                            try {

                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                                socket.receive(packet);

                                buffer = packet.getData();
                                speaker.write(buffer, 0, minBufSize);
                                speaker.play();

                                Log.e("data received  ::", buffer.toString());
                            } catch (IOException e) {
                                Log.e("VR", "IOException");
                            }
                        }


                    } catch (SocketException e) {
                        Log.e("VR", "SocketException");
                    }






            }
            });
        receiveThread.start();
if(status== false) {
    receiveThread.stop();
}

    }











    public void startStreaming()
    {
        Thread streamThread = new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{



                    DatagramSocket socket = new DatagramSocket();
              //      Log.d("VS", "Socket Created");

                    byte[] buffer = new byte[minBufSize];

//                    Log.d("VS", "Buffer created of size " + minBufSize);


  //                  Log.d("VS", "Address retrieved");
                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize);
    //                Log.d("VS", "Recorder initialized");


                    recorder.startRecording();


                    InetAddress IPAddress = InetAddress.getByName(callerIP);


      //              Log.d("VS", "Calling"+contactIps);
                    byte[] sendData = new byte[minBufSize];

                    byte[] receiveData = new byte[1024];

        //            Log.d("VS", "status"+status);

                    while (status==true)
                    {
                        recorder.read(sendData,0,minBufSize);
                     Log.d("VS", "data"+sendData.toString());

                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 1127);
                        socket.send(sendPacket);
                        //     Thread.sleep(10);
                    }

                } catch(UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    Log.e("VS", "IOException");
                    e.printStackTrace();
                }


            }

        });
        streamThread.start();
    }










































    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_callscreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
