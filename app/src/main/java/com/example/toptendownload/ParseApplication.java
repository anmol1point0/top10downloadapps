package com.example.toptendownload;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplication {
    private static final String TAG = "ParseApplication";
    private ArrayList<feedentry> applications;

    public ParseApplication() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<feedentry> getApplications() {
        return applications;
    }

    public boolean parse(String Xmldata) {
        boolean status = true;
        feedentry currentrecord = null;
        boolean inentry = false;
        String textvalue = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(Xmldata));
            int eventtype = xpp.getEventType();
            while (eventtype != XmlPullParser.END_DOCUMENT) {
                String tagname = xpp.getName();
                switch (eventtype) {
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Starting tag for " + tagname);
                        if ("entry".equalsIgnoreCase(tagname)) {
                            inentry = true;
                            currentrecord = new feedentry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textvalue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: Ending tag for " + tagname);
                        if (inentry) {
                            if ("entry".equalsIgnoreCase(tagname)) {
                                applications.add(currentrecord);
                                inentry = false;
                            } else if ("name".equalsIgnoreCase(tagname)) {
                                currentrecord.setName(textvalue);
                            } else if ("artist".equalsIgnoreCase(tagname)) {
                                currentrecord.setArtist(textvalue);
                            } else if ("releaseDate".equalsIgnoreCase(tagname)) {
                                currentrecord.setReleasedate(textvalue);
                            } else if ("summary".equalsIgnoreCase(tagname)) {
                                currentrecord.setSummary(textvalue);
                            } else if ("image".equalsIgnoreCase(tagname)) {
                                currentrecord.setImageurl(textvalue);
                            }
                        }
                        break;
                    default:
                        //nothing.
                }
                eventtype = xpp.next();
            }
            for (feedentry app : applications) {
                Log.d(TAG, "************************ ");
                Log.d(TAG, app.toString());
            }

        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
