package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;

    public class DownloadWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... s) {
            URL url;
            String result = "";
            try {
                url = new URL(s[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("Debug", "the json" + s);
            String toDisplay = "";
            try {
                JSONObject jsonObject = new JSONObject(s);
                String str = jsonObject.getString("name");
                toDisplay += "Name: " + str + "\n";
                str = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(str);
                if (jsonArray.length() != 0) {
                    Log.i("Debug", "the json" + str);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject part = jsonArray.getJSONObject(i);
                        toDisplay += "Main: " + part.getString("main") + "\nDescription: " +
                                part.getString("description") + "\n";
                    }
                    String strMain = jsonObject.getString("main");
                    JSONObject temps = new JSONObject(strMain);

                    double currentTemp = (temps.getDouble("temp") - 273.15) * 9 / 5 + 32;
                    double min = (temps.getDouble("temp_min") - 273.15) * 9 / 5 + 32;
                    double max = (temps.getDouble("temp_max") - 273.15) * 9 / 5 + 32;
                    toDisplay += String.format("Current Temp: %.1f\nHigh: %.1f\nLow: %.1f\nHumidity: %d %%\n",
                            currentTemp, max, min, temps.getInt("humidity"));
                    textView.setText(toDisplay);
                } else {
                    textView.setText("Could not find " + editText.getText().toString() + "\n\nEither you can't spell or you made this place up!");
                }

            } catch (Exception e) {
                e.printStackTrace();
                textView.setText("Could not find " + editText.getText().toString() + "\n\nEither you can't spell or you made this place up!");
            }

        }
    }

    public void click(View view) {
        DownloadWeather getWeather = new DownloadWeather();
        String appId = "990e3ff11e805e0bd89fd68874bd12de";
        String endPoint = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s",
                editText.getText().toString(), appId);
        Log.i("Debug", "the json will come from " + endPoint);

        getWeather.execute(endPoint);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);

    }
}
