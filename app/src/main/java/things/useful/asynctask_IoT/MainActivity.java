package things.useful.asynctask_IoT;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.nitri.gauge.Gauge;

public class MainActivity extends AppCompatActivity {


    //TextView text;
    CheckBox checkBox;
    private BroadcastReceiver broadcastReceiver;
    Button cancel;
    Button buttonABOUT;
    SeekBar seekBar;
    ProgressBar progressBar;
    TextView textProgresVrednost;
    TextView textViewTEMP;
    TextView textViewVLAZ;
    TextView textViewOSV;


    Gauge gauge1;
    Gauge gauge2;
    Gauge gauge3;


    float vrednostTemperatura = 0;
    float vrednostVlaznost = 0;

    String vrednostSvega = "";

    String vrednostOsvetljenje = "";
    String vrednostTemp = "";
    String vrednostVlaz = "";

    String temp1 = "Temperatura: ";
    String temp2 = " °C";
    String vlaz1 = "Relativna vlažnost vazduha: ";
    String vlaz2 = " %";
    String osvet1 = "Relativno osvetljenje iznosi: ";
    String osvet2 = " [/]";
    String finalno = "";

    String stateTemp = "";
    String stateVlaz = "";
    String stateOsv = "";


    boolean flagT = true;
    boolean flagH = false;
    boolean flagL = false;


    int brojac = 0;

    float vrednostTemperaturaSuma = 0;
    float vrednostVlaznostSuma = 0;
    int brojUzorkovanja = 1;

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showABOUT(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout_about);
        dialog.setCancelable(false);
        dialog.setTitle(getResources().getString(R.string.info));
        cancel = (Button) dialog.findViewById(R.id.but_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

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

                    if(!vrednostSvega.equals("")){
                        vrednostOsvetljenje = "";
                        vrednostTemp = "";
                        vrednostVlaz = "";

                        vrednostTemperatura = 0;
                        vrednostVlaznost = 0;

                        flagT = true;
                        flagH = false;
                        flagL = false;

                        progressBar.setProgress(0);

                        if(brojac < brojUzorkovanja){
                            //progressBar.setProgress(brojac);
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

                            //----------------------------------------**************************************************
                            //PROVERA DA NAM BROADCAST RECIVER NEUHVATI SLUCAJNO NEKI ODPAD DA NI IZBEGLI NULL POINTER EXCEPTION
                            int brojacTacaka = 0;
                            boolean flag = true;
                            vrednostTemp = vrednostTemp.trim();
                            vrednostVlaz = vrednostVlaz.trim();
                            for(int i = 0; i< vrednostTemp.length(); i++){
                                if(Character.isDigit(vrednostTemp.charAt(i)) || vrednostTemp.charAt(i) == '.'){
                                    if(vrednostTemp.charAt(i) == '.'){
                                        brojacTacaka++;
                                        if(brojacTacaka > 1){
                                            flag = false;
                                            break;
                                        }
                                    }
                                }else{
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag && brojacTacaka <2){
                                vrednostTemperatura = Float.parseFloat(vrednostTemp);
                            }else{
                                vrednostTemperatura = 20;
                            }
                            brojacTacaka = 0;
                            flag = true;

                            for(int i = 0; i< vrednostVlaz.length(); i++){
                                if(Character.isDigit(vrednostVlaz.charAt(i)) || vrednostVlaz.charAt(i) == '.'){
                                    if(vrednostVlaz.charAt(i) == '.'){
                                        brojacTacaka++;
                                        if(brojacTacaka > 1){
                                            flag = false;
                                            break;
                                        }
                                    }
                                }else{
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag  && brojacTacaka <2){
                                vrednostVlaznost = Float.parseFloat(vrednostVlaz);
                            }else{
                                vrednostVlaznost = 20;
                            }

                            //----------------------------------------**************************************************

                            vrednostTemperaturaSuma += vrednostTemperatura;
                            vrednostVlaznostSuma += vrednostVlaznost;


                            Log.e("HAOS TEMPERATURA", vrednostTemp);
                            Log.e("HAOS VLAZNOST", vrednostVlaz);
                            Log.e("HAOS OSVETLJENJE", vrednostOsvetljenje);
                            Log.e("HAOS PRAZANRED", "*******************"+brojac);




                            //finalni += vrednostTemperatura+ "\n\n" + vrednostVlaznost + "\n\n" + vrednostOsvetljenje;
                            brojac ++;
                            progressBar.setProgress(brojac);
                        }



                        if(brojac == brojUzorkovanja){
                            vrednostTemperatura = vrednostTemperaturaSuma / (brojac);
                            vrednostVlaznost = vrednostVlaznostSuma / (brojac);

                            finalno = temp1 +"\n\n"+vrednostTemperatura+ temp2 +"\n\n"+vlaz1+"\n\n"+vrednostVlaznost+vlaz2+"\n\n"+osvet1+"\n\n"+vrednostOsvetljenje+osvet2;

                            //text.setText(String.valueOf(finalno));

                            Thread thread1 = new Thread(){
                                public void run(){
                                    gauge1.moveToValue(vrednostTemperatura);
                                    gauge2.moveToValue(vrednostVlaznost);
                                    if(Character.isDigit(vrednostOsvetljenje.charAt(0))){
                                        gauge3.moveToValue(Float.valueOf(""+vrednostOsvetljenje.charAt(0)));
                                    }

                                    /*textViewTEMP.setText(String.valueOf(vrednostTemperatura));
                                    textViewVLAZ.setText(String.valueOf(vrednostVlaznost));
                                    textViewOSV.setText(""+vrednostOsvetljenje.charAt(0));*/

                                }
                            };
                            thread1.start();

                            /*textViewTEMP.setText(String.valueOf(vrednostTemperatura));
                            textViewVLAZ.setText(String.valueOf(vrednostVlaznost));
                            textViewOSV.setText(""+vrednostOsvetljenje.charAt(0));*/

                            stateTemp = String.valueOf(vrednostTemperatura);
                            stateVlaz = String.valueOf(vrednostVlaznost);
                            stateOsv = ""+vrednostOsvetljenje.charAt(0);

                            gauge1.setLowerText(stateTemp);
                            gauge2.setLowerText(stateVlaz);
                            gauge3.setLowerText(stateOsv);

                            brojac = 0;
                            vrednostTemperaturaSuma = 0;
                            vrednostVlaznostSuma = 0;
                            progressBar.setProgress(0);
                        }
                    }


                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("update_senzora"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String stateTemp1 = stateTemp;
        String stateVlaz1 = stateVlaz;
        String stateOsv1 = stateOsv;
        int stateProg = progressBar.getProgress();
        float stateTempSuma = vrednostTemperaturaSuma;
        float stateVlazSuma = vrednostVlaznostSuma;
        int stateBrojac = brojac;
        outState.putString("saved_state_1", stateTemp1);
        outState.putString("saved_state_2", stateVlaz1);
        outState.putString("saved_state_3", stateOsv1);
        outState.putInt("saved_state_4", stateProg);
        outState.putFloat("saved_state_5", stateTempSuma);
        outState.putFloat("saved_state_6", stateVlazSuma);
        outState.putInt("saved_state_7", stateBrojac);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String stateSaved_1 = savedInstanceState.getString("saved_state_1");
        String stateSaved_2 = savedInstanceState.getString("saved_state_2");
        String stateSaved_3 = savedInstanceState.getString("saved_state_3");
        int stateSaved_4 = savedInstanceState.getInt("saved_state_4");
        float stateSaved_5 = savedInstanceState.getFloat("saved_state_5");
        float stateSaved_6 = savedInstanceState.getFloat("saved_state_6");
        int stateSaved_7 = savedInstanceState.getInt("saved_state_7");
        if(stateSaved_1 != null){
            gauge1.setLowerText(stateSaved_1);
            gauge2.setLowerText(stateSaved_2);
            gauge3.setLowerText(stateSaved_3);
            progressBar.setProgress(stateSaved_4);
            vrednostTemperaturaSuma = stateSaved_5;
            vrednostVlaznostSuma = stateSaved_6;
            brojac = stateSaved_7;
        }
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //text = (TextView) findViewById(R.id.tv_result);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        buttonABOUT = (Button)findViewById(R.id.buttonABOUT);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        textProgresVrednost = (TextView)findViewById(R.id.textProgresVrednost);

        gauge1 = (Gauge) findViewById(R.id.gauge1);
        gauge2 = (Gauge) findViewById(R.id.gauge2);
        gauge3 = (Gauge) findViewById(R.id.gauge3);


        progressBar.setProgress(0);
        progressBar.setMax(1);

        buttonABOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showABOUT();
            }
        });

        if(isMyServiceRunning(IoT.class)){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }

        seekBar.setMax(9);
        seekBar.setProgress(0);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                brojUzorkovanja = progress + 1;

                textProgresVrednost.setText((String.valueOf(brojUzorkovanja*2.5))+ " s");
                progressBar.setMax(brojUzorkovanja);
                progressBar.setProgress(0);

            }
        });




        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(checkBox.isChecked()){
                        Intent i = new Intent(getApplicationContext(),IoT.class);
                        startService(i);

                    }else{
                        Intent i = new Intent(getApplicationContext(),IoT.class);
                        stopService(i);
                        //text.setText("Rezultat");
                        gauge1.setLowerText("");
                        gauge2.setLowerText("");
                        gauge3.setLowerText("");
                        gauge1.moveToValue(-30);
                        gauge2.moveToValue(0);
                        gauge3.moveToValue(0);
                        brojac = 0;
                        vrednostTemperaturaSuma = 0;
                        vrednostVlaznostSuma = 0;
                    }
                }
            }
        );
    }


}


