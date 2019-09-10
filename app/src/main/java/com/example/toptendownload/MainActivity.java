package com.example.toptendownload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listapps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listapps = (ListView) findViewById(R.id.xmlListView);
        Log.d(TAG, "onCreate: starting a async task");
        DownloadData downloaddata = new DownloadData();
        downloaddata.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
        Log.d(TAG, "onCreate: done");
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";
        //fist String is used to store URL.
        //second String contains the XMl file after download.

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute:parameter is assessed ");
            ParseApplication parseapplicaion = new ParseApplication();
            parseapplicaion.parse(s);
//            ArrayAdapter<feedentry> arrayAdapter=new ArrayAdapter<feedentry>(
//                MainActivity.this, R.layout.list_item, parseapplicaion.getApplications());
//            listapps.setAdapter(arrayAdapter);
            feedadaptor Feedadaptor = new feedadaptor(MainActivity.this, R.layout.list_record, parseapplicaion.getApplications());
            listapps.setAdapter(Feedadaptor);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssfeed = DownloadXML(strings[0]);
            if (rssfeed == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssfeed;
        }

        private String DownloadXML(String urlpath) {
            StringBuilder xmlresult = new StringBuilder();
            try {
                URL url = new URL(urlpath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "DownloadXML: the response code was " + response);
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                int charsread;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsread = reader.read(inputBuffer);
                    if (charsread < 0)
                        break;
                    if (charsread > 0)
                        xmlresult.append(String.copyValueOf(inputBuffer, 0, charsread));
                }
                reader.close();
                return xmlresult.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG, "DownloadXML: Invalidurl" + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "DownloadXML: IO excetion reading data" + e.getMessage());
            }
            return null;
        }
    }
}
