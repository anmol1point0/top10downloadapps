package com.example.toptendownload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";
    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedUrl = "INVALIDATED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadUrl(String.format(feedUrl, feedLimit));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        } else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.mnufree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                }
                break;
            case R.id.mnurefresh:
                feedCachedUrl = "INVALIDATED";
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    private void downloadUrl(String feedUrl) {
        if (!feedUrl.equalsIgnoreCase(feedCachedUrl)) {
            Log.d(TAG, "downloadUrl: starting Asynctask");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedUrl);
            feedCachedUrl = feedUrl;
            Log.d(TAG, "downloadUrl: done");
        } else {
            Log.d(TAG, "downloadUrl: URL not changed");
        }
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplication parseApplications = new ParseApplication();
            parseApplications.parse(s);

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    MainActivity.this, R.layout.list_item, parseApplications.getApplications());
//            listApps.setAdapter(arrayAdapter);
            feedadaptor feedAdapter = new feedadaptor(MainActivity.this, R.layout.list_record,
                    parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader reader = new BufferedReader(inputStreamReader);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0) {
                        break;
                    }
                    if (charsRead > 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();

                return xmlResult.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception.  Needs permisson? " + e.getMessage());
//                e.printStackTrace();
            }

            return null;
        }
    }
}


//public class MainActivity extends AppCompatActivity {
//    public static final String feedcontents = "feedlimiit";
//    public static final String urlcontents = "feedurl";
//    private static final String TAG = "MainActivity";
//    private ListView listapps;
//    private String urlcached = "invalid";
//    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
//    private int feedLimit = 10;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        listapps = (ListView) findViewById(R.id.xmlListView);
//        if (savedInstanceState != null) {
//            feedLimit = (savedInstanceState.getInt(feedcontents));
//            feedUrl = (savedInstanceState.getString(urlcontents));
//        }
//        downloadUrl(String.format(feedUrl, feedLimit));
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.feeds_menu, menu);
//        if (feedLimit == 10) {
//            menu.findItem(R.id.mnu10).setChecked(true);
//        } else {
//            menu.findItem(R.id.mnu25).setChecked(true);
//        }
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.mnufree:
//                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
//                break;
//            case R.id.mnuPaid:
//                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
//                break;
//            case R.id.mnuSongs:
//                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
//                break;
//            case R.id.mnu10:
//            case R.id.mnu25:
//                if (!item.isChecked()) {
//                    item.setChecked(true);
//                    feedLimit = 35 - feedLimit;
//                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedlimit is " + feedLimit);
//                } else {
//                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + "feedlimit unchnaged ");
//                }
//                break;
//            case R.id.mnurefresh:
//                urlcached = "invalid";
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//        downloadUrl(String.format(feedUrl, feedLimit));
//        return true;
//        // return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt(feedcontents, feedLimit);
//        outState.putString(urlcontents, feedUrl);
//        super.onSaveInstanceState(outState);
//    }
//
//    private void downloadUrl(String feedUrl) {
//        if (!feedUrl.equalsIgnoreCase(urlcached)) {
//            DownloadData downloaddata = new DownloadData();
//            urlcached = feedUrl;
//            downloaddata.execute(feedUrl);
//            Log.d(TAG, "downloadUrl: done");
//        }
//    }
//
//    private class DownloadData extends AsyncTask<String, Void, String> {
//        private static final String TAG = "DownloadData";
//        //fist String is used to store URL.
//        //second String contains the XMl file after download.
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            //        Log.d(TAG, "onPostExecute:parameter is assessed ");
//            ParseApplication parseapplicaion = new ParseApplication();
//            parseapplicaion.parse(s);
////            ArrayAdapter<feedentry> arrayAdapter=new ArrayAdapter<feedentry>(
////                MainActivity.this, R.layout.list_item, parseapplicaion.getApplications());
////            listapps.setAdapter(arrayAdapter);
//            feedadaptor Feedadaptor = new feedadaptor(MainActivity.this, R.layout.list_record, parseapplicaion.getApplications());
//            listapps.setAdapter(Feedadaptor);
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            Log.d(TAG, "doInBackground: starts with " + strings[0]);
//            String rssfeed = DownloadXML(strings[0]);
//            if (rssfeed == null) {
//                Log.e(TAG, "doInBackground: Error downloading");
//            }
//            return rssfeed;
//        }
//
//        private String DownloadXML(String urlpath) {
//            StringBuilder xmlresult = new StringBuilder();
//            try {
//                URL url = new URL(urlpath);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                int response = connection.getResponseCode();
//                Log.d(TAG, "DownloadXML: the response code was " + response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader reader = new BufferedReader(inputStreamReader);
//                int charsread;
//                char[] inputBuffer = new char[500];
//                while (true) {
//                    charsread = reader.read(inputBuffer);
//                    if (charsread < 0)
//                        break;
//                    if (charsread > 0)
//                        xmlresult.append(String.copyValueOf(inputBuffer, 0, charsread));
//                }
//                reader.close();
//                return xmlresult.toString();
//            } catch (MalformedURLException e) {
//                Log.e(TAG, "DownloadXML: Invalidurl" + e.getMessage());
//            } catch (IOException e) {
//                Log.e(TAG, "DownloadXML: IO excetion reading data" + e.getMessage());
//            }
//            return null;
//        }
//    }
//}
