package things.useful.asynctask_IoT;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


    TextView text;
    CheckBox checkBox;
    private BroadcastReceiver broadcastReceiver;

    float vrednostTemperatura = 0;
    float vrednostVlaznost = 0;

    String vrednostSvega = "";

    String vrednostOsvetljenje = "";
    String vrednostTemp = "";
    String vrednostVlaz = "";

    String temp1 = "Temperatura: ";
    String temp2 = " *C";
    String vlaz1 = "Relativna vlažnost vazduha: ";
    String vlaz2 = " %";
    String osvet1 = "Relativno osvetljenje iznosi: ";
    String osvet2 = " [/]";
    String finalno = "";


    boolean flagT = true;
    boolean flagH = false;
    boolean flagL = false;

    int brojac = 0;

    float vrednostTemperaturaSuma = 0;
    float vrednostVlaznostSuma = 0;
    int brojUzorkovanja = 10;

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
                    //text.setText(""+intent.getExtras().get("vrednosti"));
                    vrednostSvega = ""+intent.getExtras().get("vrednosti");

                    vrednostOsvetljenje = "";
                    vrednostTemp = "";
                    vrednostVlaz = "";

                    vrednostTemperatura = 0;
                    vrednostVlaznost = 0;

                    flagT = true;
                    flagH = false;
                    flagL = false;

                    if(brojac < brojUzorkovanja){
                        for (int i = 0; i < vrednostSvega.length(); i++) {

                            if (flagT) {
                                vrednostTemp += vrednostSvega.charAt(i);
                                if (vrednostSvega.charAt(i) == ' ') {
                                    flagT = false;
                                    flagH = true;
                                    continue;
                                }
                            }
                            if (flagH) {
                                vrednostVlaz += vrednostSvega.charAt(i);
                                if (vrednostSvega.charAt(i) == ' ') {
                                    flagH = false;
                                    flagL = true;
                                    continue;
                                }
                            }
                            if (flagL) {
                                vrednostOsvetljenje += vrednostSvega.charAt(i);
                            }
                        }
                        vrednostTemperatura = Float.parseFloat(vrednostTemp);
                        vrednostVlaznost = Float.parseFloat(vrednostVlaz);

                        vrednostTemperaturaSuma += vrednostTemperatura;
                        vrednostVlaznostSuma += vrednostVlaznost;


                        Log.e("HAOS TEMPERATURA", vrednostTemp);
                        Log.e("HAOS VLAZNOST", vrednostVlaz);
                        Log.e("HAOS OSVETLJENJE", vrednostOsvetljenje);
                        Log.e("HAOS PRAZANRED", "*******************"+brojac);




                        //finalni += vrednostTemperatura+ "\n\n" + vrednostVlaznost + "\n\n" + vrednostOsvetljenje;
                        brojac ++;
                    }


                    if(brojac == brojUzorkovanja){
                        vrednostTemperatura = vrednostTemperaturaSuma / (brojac);
                        vrednostVlaznost = vrednostVlaznostSuma / (brojac);

                        finalno = temp1 +vrednostTemperatura+ temp2 +"\n\n"+vlaz1+vrednostVlaznost+vlaz2+"\n\n"+osvet1+vrednostOsvetljenje+osvet2;

                        text.setText(String.valueOf(finalno));
                        brojac = 0;
                        vrednostTemperaturaSuma = 0;
                        vrednostVlaznostSuma = 0;
                    }

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
                        brojac = 0;
                        vrednostTemperaturaSuma = 0;
                        vrednostVlaznostSuma = 0;
                    }
                }
            }
        );
    }

}


