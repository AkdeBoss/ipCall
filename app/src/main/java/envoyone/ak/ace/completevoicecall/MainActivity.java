package envoyone.ak.ace.completevoicecall;



/*
* We need a dispatcher to recieve the call and a dispatcher to notify  and   to make a call
*
*
*
*
*
*
* */




import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    Button call,dissconnect;
    TextView caller,yourip;
    EditText ip;









public String Tag="1";
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

public String Ipadd="192.168.0.0";

public boolean incoming=true;
    public boolean outgoing=false;
    private AudioTrack speaker;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        call = (Button) findViewById(R.id.button_Call);
        dissconnect = (Button) findViewById(R.id.button_disscnct);
        ip= (EditText) findViewById(R.id.editText_Ip);
        caller = (TextView) findViewById(R.id.textView_CAller);
        new Thread(new BackgroundTask()).start();
        yourip=(TextView) findViewById(R.id.ipTextView);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        yourip.setText("Your IP address: "+ip);
call.setOnClickListener(callor);
dissconnect.setOnClickListener(dissconnector);
//        incomingListner();



    }







    View.OnClickListener callor = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            status=true;
            outgoing=true;

            Ipadd= ip.getText().toString();
            outgoingdispatch();
        }
    };







    View.OnClickListener dissconnector = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //  status=false;

            Intent intent = new Intent(getApplicationContext() , Callscreen.class);
            intent.putExtra("callerIp","192.168.43.94"); // getText() SHOULD NOT be static!!!
            // intent.putExtra("callerName",callerName);
            startActivity(intent);



        }
    };





OutputStreamWriter outputstrm;
public void outgoingdispatch()
{


    Thread CallThread = new Thread(new Runnable() {

        @Override
        public void run() {
            Log.e("VS", "Calling..");
            Socket socket = null;
            try {
                socket = new Socket(Ipadd, 2331);
                Log.e("VS", "socket okay");
                outputstrm = new OutputStreamWriter(socket.getOutputStream());

                outputstrm.write(Tag+"\n");
                Log.e("VS", "okay tag=" + Tag);
                outputstrm.flush();

           outputstrm.close();
                Log.e("VS", "request sent");





                String reslt;
                reslt="ok";

                if(reslt.contains("ok"))
                {
                    Log.e("VS", "accepted ");

                    Intent intent = new Intent(getApplicationContext() , Callscreen.class);
                    intent.putExtra("callerIp",Ipadd); // getText() SHOULD NOT be static!!!
                    // intent.putExtra("callerName",callerName);
                    startActivity(intent);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    });
    CallThread.start();
}





ServerSocket incomingAgent;

    Socket callerClient;
    String result= "2";
    Context context;



    public class BackgroundTask implements Runnable {

        @Override
        public void run() {
            try {
                incomingAgent= new  ServerSocket(2331);

                Log.e("VC", "receiver running");

                while(status= true) {
                    callerClient = incomingAgent.accept();

                    Ipadd = callerClient.getInetAddress().toString();
                    Log.e("VC", "got a request from"+Ipadd);
                    BufferedReader InReader = null;
                    InReader = new BufferedReader(new InputStreamReader(callerClient.getInputStream()));

                    result = InReader.readLine();

                    callerClient.close();
                    Log.e("VC", result + "   this is the tag sent");


                    if (Tag.matches(result)) {
                        Log.e("VC", "tag okay");

                        String s= Ipadd;
                        Pattern p = Pattern.compile(".*\\/ *(.*).*");
                        Matcher m = p.matcher(s);
                        m.find();
                        String text = m.group(1);

                        Intent intent = new Intent(getApplicationContext(), CallAlert.class);
                        intent.putExtra("callerIp", text);
                        startActivity(intent);


                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }




        }


    }



















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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



 /*   runOnUiThread(new Runnable() {
                            public void run() {
////////////////////////////////
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        getApplicationContext());

                                // set title
                                alertDialogBuilder.setTitle("You are getting a voice call");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("What do you wanna do?")
                                        .setCancelable(false)
                                        .setPositiveButton("recieve", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, close
                                                // current activity
                                                Log.e("VS", "prompt and okay");
                                                try {
                                                    //  callerClient.getOutputStream();

                                                    PrintWriter outs = new PrintWriter(new BufferedWriter(new OutputStreamWriter(callerClient.getOutputStream())), true);
                                                    outs.write("ok");
                                                    outs.flush();

                                               // callerClient.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }


                                                Intent intent = new Intent(getApplicationContext(), Callscreen.class);
                                                intent.putExtra("callerIp", Ipadd); // getText() SHOULD NOT be static!!!
                                                // intent.putExtra("callerName",callerName);
                                                startActivity(intent);


                                            }
                                        })
                                        .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                Log.e("VS", "cancel clicked");
                                                dialog.cancel();
                                                //      callerClient.close();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                            }
                        });*/