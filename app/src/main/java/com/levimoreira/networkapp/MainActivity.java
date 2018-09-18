package com.levimoreira.networkapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {


    private final static String URL = "https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22";
    @BindView(R.id.output)
    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DownloadJsonFileAsyncTask task = new DownloadJsonFileAsyncTask();
        task.execute(URL);


        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                output.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainActivity", error.getMessage());
            }
        });


        queue.add(request);
    }


    private class DownloadJsonFileAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);


                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();
                int response = connection.getResponseCode();

                if (response == 200) {
                    InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
                    return convertInputToString(inputStream, 1024);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            output.setText(s);
        }

        private String convertInputToString(InputStreamReader stream, int len)
                throws IOException, UnsupportedEncodingException {

            BufferedReader reader = new BufferedReader(stream, len);
            StringBuilder builder = new StringBuilder();
            String jsonLine = null;

            while ((jsonLine = reader.readLine()) != null) {
                builder.append(jsonLine);
            }
            return builder.toString();
        }
    }
}
