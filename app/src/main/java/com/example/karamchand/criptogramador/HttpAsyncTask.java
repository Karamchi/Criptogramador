package com.example.karamchand.criptogramador;

import android.os.AsyncTask;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class HttpAsyncTask extends AsyncTask<String, Object, ArrayList<String>> {

    public final static String GET = "GET";
    private String mUrlString;
    private HTTPListener mListener;

    public HttpAsyncTask(String mUrlString, HTTPListener mListener) {
        this.mUrlString = mUrlString;
        this.mListener = mListener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... method) {
        try {
        // Creating connection with url and required Header.
            URL url = new URL(mUrlString);
            HttpURLConnection urlConnection = (HttpURLConnection) (url.openConnection());
            urlConnection.setRequestMethod(method[0]);
            urlConnection.connect();
            Log.i("Http request", method[0]);
            Log.i("Http request", mUrlString);

            // Check the connection status.
            int statusCode = urlConnection.getResponseCode();

            // Connection success. Proceed to fetch the response.
            if (statusCode == HttpURLConnection.HTTP_OK) {
                ArrayList<String> result = FileUtils.streamToString(urlConnection.getInputStream());
                return result;
            } else {
                mListener.onResponseFailure(statusCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onFailure();
            return null;
        }
    }

    @Override
    public void onPostExecute(ArrayList<String> result) {
        if (result != null)
            mListener.onResponseSuccessful(result);
    }

    interface HTTPListener {
        void onResponseSuccessful(ArrayList<String> result);
        void onResponseFailure(int statusCode);
        void onFailure();
    }
}