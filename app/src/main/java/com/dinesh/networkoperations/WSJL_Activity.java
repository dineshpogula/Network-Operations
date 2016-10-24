package com.dinesh.networkoperations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by dinesh on 10/20/2016.
 */
public class WSJL_Activity extends AppCompatActivity {
    public final String mNewsFeed = "http://www.wsj.com/xml/rss/3_7455.xml";
    private ConnectivityManager connectivityManager;
    public LinearLayout mContainerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xmlparser);
        connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mContainerLayout =(LinearLayout)findViewById(R.id.containerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo!= null && networkInfo.isConnected()){
            loadNewsPage();
        }
        else{
            loadDefaultMessage();
        }



    }
    public void loadNewsPage() {
        new DownloadNewsTask().execute(mNewsFeed);
    }
    void loadDefaultMessage(){
        TextView message = new TextView(this);
        message.setText("Internet connection is not available");
        mContainerLayout.removeAllViews();
        mContainerLayout.addView(message);
    }

    private class DownloadNewsTask extends AsyncTask<String, Void, List<SimpleXmlParser.NewsItem>> {
        protected List<SimpleXmlParser.NewsItem> doInBackground(String... params){
            List<SimpleXmlParser.NewsItem> items;
            InputStream xmlStream =null;
            String url = params[0];
            xmlStream = downloadXML(url);
            items = createNewsItemFromXML(xmlStream);
        return items;
        }
        protected  void onPostExecute (List<SimpleXmlParser.NewsItem> items){
            LayoutInflater inflater =LayoutInflater.from(getBaseContext());
            mContainerLayout.removeAllViews();
            for(SimpleXmlParser.NewsItem item : items){
                LinearLayout ll =(LinearLayout)inflater.inflate(R.layout.news_item,null, false);
                TextView heading = (TextView)ll.findViewById(R.id.heading);
                TextView description= (TextView)ll.findViewById(R.id.description);
                heading.setText(item.title);
                description.setText(item.description);
                mContainerLayout.addView(ll);
            }
        }
    }
    public InputStream downloadXML(String path){
        final String TAG = "Downlaod Task";
        Bitmap bitmap = null;
        InputStream inStream = null;

        try{
            URL url = new URL(path);
            HttpURLConnection urlConnection= (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(2500);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            inStream = urlConnection.getInputStream();


        }catch (MalformedURLException e){
            Log.e(TAG,"URL error:" + e.getMessage());


        }catch (IOException e){
            Log.e(TAG," Download failed :" + e.getMessage());

        }
        return inStream;

    }
    public List<SimpleXmlParser.NewsItem> createNewsItemFromXML(InputStream xml){
        SimpleXmlParser parser = new SimpleXmlParser();
        return parser.parse(xml);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId()== R.id.action_settings){
            Intent SimpleXmlParser = new Intent(getBaseContext(),SimpleXmlParser.class);
            startActivity(SimpleXmlParser);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}




