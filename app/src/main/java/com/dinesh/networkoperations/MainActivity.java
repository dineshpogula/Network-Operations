package com.dinesh.networkoperations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager mconManager;
//    public NetworkReceiver mnetReceiver;
    private ImageView imageView;
    public  String sPreferredNetwork;
    public Button wsjl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingshttp);
        mconManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        mnetReceiver=new NetworkReceiver();
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(mnetReceiver,filter);
        imageView =(ImageView)findViewById(R.id.resultImageView);
         wsjl = (Button)findViewById(R.id.journal) ;





    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        sPreferredNetwork =preferences.getString("chosenNetworkType", "Any");
        wsjl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent simplexmlparser = new Intent(MainActivity.this,WSJL_Activity.class);
                startActivity(simplexmlparser);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId()== R.id.action_settings){
            Intent settingsIntent = new Intent(getBaseContext(),SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onRefreshImage(View v) {
        final String TAG = "Refresh";
        String imagePath = "http://lorempixel.com/640/480/";
        boolean isWiFiAvailable = mconManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        if (sPreferredNetwork.equals("Any")) {

            if (mconManager != null) {
                NetworkInfo networkInfo = mconManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadImageTask().execute(imagePath);
                } else {
                    Toast.makeText(this, "Network Not Available", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (sPreferredNetwork.equals("WiFi")){
            if (isWiFiAvailable){
                new DownloadImageTask().execute(imagePath);
            }
            else{
                Toast.makeText(this,"Data allowed only on wifi network",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Data displayed by user", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mnetReceiver!= null){
//            unregisterReceiver(mnetReceiver);
//        }
//    }



    public void onShowNetworkStatus(View v){
        if (mconManager!= null){
            NetworkInfo networkInfo =mconManager.getActiveNetworkInfo();

            if(networkInfo!= null&&networkInfo.isConnected()){
                Toast.makeText(this,"Network available",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Network Not Available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String...urls){
            return downloadImage(urls[0]);
            }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageView != null){
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
    }

    private Bitmap downloadImage(String path) {
        final String TAG ="Download Task";
        Bitmap bitmap = null;
        InputStream inStream;
        try{
            URL url = new URL(path);
            HttpURLConnection urlConnection= (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(2500);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            inStream = urlConnection.getInputStream();
            bitmap= BitmapFactory.decodeStream(inStream);

        }catch (MalformedURLException e){
            Log.e(TAG,"URL error:" + e.getMessage());


        }catch (IOException e){
            Log.e(TAG," Download failed :" + e.getMessage());

        }
        return bitmap;
    }

//    private class NetworkReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            NetworkInfo networkInfo = mconManager.getActiveNetworkInfo();
//            if (networkInfo!=null){
//                boolean isWifiAvailable =mconManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
//                boolean isGSMAvailable = mconManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
//                if (isWifiAvailable){
//                    Toast.makeText(context,"Wifi Reconnected",Toast.LENGTH_SHORT).show();
//
//                }else if (isGSMAvailable)
//                {
//                    Toast.makeText(context,"GSM Datat Avalible",Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(context,"Network not Available",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }
//    }
}
