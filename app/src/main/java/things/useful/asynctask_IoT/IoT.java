package things.useful.asynctask_IoT;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nikola on 11/6/2017.
 */

public class IoT extends Service {

    String strUrl = "http://188.246.60.182:1001/svo=stanje";
    boolean flag = true;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread thread = new Thread(){
            public void run(){
                while(flag){
                    new SimpleSyncTask().execute(strUrl);

                    try {
                        sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }

    public class SimpleSyncTask extends AsyncTask<String, String, String> {



        //private String finalni = "";

        public SimpleSyncTask() {

        }

        @Override
        protected void onPostExecute(String s) {

            Intent i = new Intent("update_senzora");
            i.putExtra("vrednosti",s);
            sendBroadcast(i);



        }
        @Override
        protected String doInBackground(String... params) {
            String value = "";


                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection(); // otvaranje konekcije
                    con.setRequestMethod("GET");
                    con.connect();

                    BufferedReader bf = new BufferedReader((new InputStreamReader(con.getInputStream())));
                    value = bf.readLine();

                    /*for (int i = 0; i < value.length(); i++) {
                        finalni+=value.charAt(i);
                        if(value.charAt(i) == ' '){
                            finalni+="\n\n";
                        }
                    }*/

                } catch (Exception e) {
                    System.out.println(e);
                }

            return value;
        }

    }
}