package things.useful.asynctask_IoT;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    /*String strUrl = "http://188.246.60.182:1001/svo=stanje";*/
    TextView text;
    CheckBox checkBox;
    private BroadcastReceiver broadcastReceiver;

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    //text.append("\n" +intent.getExtras().get("vrednosti"));
                    text.setText(""+intent.getExtras().get("vrednosti"));

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("update_senzora"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.tv_result);
        checkBox = (CheckBox)findViewById(R.id.checkBox);

        if(isMyServiceRunning(IoT.class)){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }




        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(checkBox.isChecked()){
                        Intent i = new Intent(getApplicationContext(),IoT.class);
                        startService(i);

                    }else{
                        Intent i = new Intent(getApplicationContext(),IoT.class);
                        stopService(i);
                        text.setText("Rezultat");
                    }
                }
            }
        );
    }

}


