package com.example.vanya.stormy.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vanya.stormy.R;
import com.example.vanya.stormy.Weather.Current;
import com.example.vanya.stormy.Weather.Forecast;
import com.example.vanya.stormy.Weather.Hour;
import com.example.vanya.stormy.databinding.ActivityMainBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String TAG = MainActivity.class.getSimpleName();

    private Forecast forecast = new Forecast();

    private ImageView iconImageView;

    final double latitude = 32.7767;
    final double longitude = -96.7970;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getForecast(latitude,longitude);
    }

    private void getForecast(double latitude, double longitude) {
        final ActivityMainBinding binding =
                DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        iconImageView = findViewById(R.id.iconImageView);
        TextView darksky = findViewById(R.id.darkSkyAttribution);
        darksky.setMovementMethod(LinkMovementMethod.getInstance());

        String apiKey = "8eece017b0717db65117f6a0e197a3bb";


        String forecastURL = "https://api.darksky.net/forecast/"+
                apiKey+"/"+latitude+","+longitude;

        if(isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            forecast = parseForecastData(jsonData);

                            Current current = forecast.getCurrent();

                            final Current displayWeather = new Current(
                                    current.getLocationLabel(),
                                    current.getIcon(),
                                    current.getTime(),
                                    current.getTemperature(),
                                    current.getHumidity(),
                                    current.getPrecipChance(),
                                    current.getSummary(),
                                    current.getTimeZone()
                            );

                            binding.setWeather(displayWeather);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(displayWeather.getIconId());
                                    iconImageView.setImageDrawable(drawable);
                                }
                            });


                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception Caught: ", e);
                    }catch (JSONException e){
                        Log.e(TAG,"JSON Exception Caught: ", e);
                    }
                }
            });
        }else {
            Toast.makeText(this, R.string.network_unavailable,Toast.LENGTH_LONG).show();
        }
    }

    private Forecast parseForecastData(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));

        return forecast;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");
        Hour[] Hours = new Hour[data.length()];

        for (int index = 0; index<data.length(); index++){
            JSONObject jsonObject = data.getJSONObject(index);
            Hour hour = new Hour(jsonObject.getLong("time"),
                    jsonObject.getString("summary"),
                    jsonObject.getDouble("temperature"),
                    jsonObject.getString("icon"),
                    timezone);

            Hours[index]=hour;
        }

        return Hours;
    }


    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);

        String timeZone = forecast.getString("timezone");
        Log.i(TAG,"from Json: "+timeZone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();

        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setLocationLabel("Dallas, TX");
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timeZone);

        Log.d(TAG, current.getFormattedTime());

        return current;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if(networkInfo!=null && networkInfo.isConnected()){
            isAvailable=true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }

    public void refreshOnClick(View view){
        getForecast(latitude,longitude);
        Toast.makeText(this,"Refreshing Data",Toast.LENGTH_LONG).show();
    }

    public void hourlyOnClick(View view){
        List<Hour> hours = Arrays.asList(forecast.getHourlyForecast());

        Intent intent = new Intent(this,HourlyForecastActivity.class);
        intent.putExtra("HourlyList", (Serializable) hours);
        startActivity(intent);
    }

}
