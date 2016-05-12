package envoyone.ak.ace.completevoicecall;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CallAlert extends ActionBarActivity {
TextView callerid;
    Button ok,cancel;
String callerIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_alert);
        callerid=(TextView)findViewById(R.id.textViewCallerID);
        ok=(Button) findViewById(R.id.button_OK);
        cancel=(Button) findViewById(R.id.button_cancel);
        callerIP= getIntent().getExtras().getString("callerIp");
        ok.setOnClickListener(okcall);
        cancel.setOnClickListener(cancelcall);
        callerid.setText(callerIP);


    }






    View.OnClickListener cancelcall = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


        }
    };





    View.OnClickListener okcall = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplicationContext() , Callscreen.class);
            intent.putExtra("callerIp",callerIP);
            startActivity(intent);
           // finish();
        }
    };







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_alert, menu);
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
