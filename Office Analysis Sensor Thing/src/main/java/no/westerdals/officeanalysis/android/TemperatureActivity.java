package no.westerdals.officeanalysis.android;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.rest.RestService;

import java.util.Arrays;
import java.util.Date;

@EActivity(R.layout.activity_temperature)
public class TemperatureActivity extends Activity implements SensorEventListener
{
    private final String TAG = TemperatureActivity.class.getSimpleName();
    @ViewById
    EditText serverIpEditText;

    @ViewById
    EditText temperatureEditText;

    @ViewById
    EditText integrationIdEditText;

    @ViewById
    TextView lightSensorValueTextField;

    @ViewById
    TextView proximitySensorValueTextField;

    @ViewById
    TextView decibelValueTextField;

    private boolean getDecibels = false;

    @RestService
    TemperatureClient temperatureClient;

    SensorManager sensorManager;

    Sensor lightSensor;

    Sensor proximitySensor;

    @Bean
    SoundMeter soundMeter;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        configureSoundMeter();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        sensorManager.unregisterListener(this);
        getDecibels = false;
        soundMeter.stop();
    }

    @AfterInject
    void configureSoundMeter()
    {
        if (soundMeter != null)
        {
            getDecibels = true;
            soundMeter.start();
            getDecibelsForever();
        }
    }

    @Background
    void getDecibelsForever()
    {
        for (; ; )
        {
            if (!getDecibels)
            {
                break;
            }

            displayDecibels(soundMeter.getDecibels());

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, "Could not put decibels thread to sleep", e);
            }
        }
    }

    @UiThread
    void displayDecibels(final double decibels)
    {
        decibelValueTextField.setText(String.valueOf(decibels));
    }

    @Click(R.id.sendTemperatureButton)
    void prepareTemperatureSendoff()
    {
        final String rootUrl = serverIpEditText.getText().toString();
        temperatureClient.setRootUrl(rootUrl);

        final String integrationId = integrationIdEditText.getText().toString();

        final String temperatureString = temperatureEditText.getText().toString();
        final String timestamp = String.valueOf(new Date());

        final Temperature temperature = new Temperature(temperatureString, timestamp);

        Log.d(TAG, "Prepared temperature sendoff. rootUrl = " + rootUrl + ", integrationId = " + integrationId + ", temperature = " + temperature);

        sendTemperature(temperature, integrationId);
    }

    @Background
    void sendTemperature(Temperature temperature, String integrationId)
    {
        Log.d(TAG, "Sent temperature: " + temperature + " from integration id: " + integrationId);
        String response = temperatureClient.send(temperature, integrationId);
        Log.d(TAG, "Got response: " + response);
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent)
    {
        Log.d(TAG, "onSensorChanged: " + Arrays.toString(sensorEvent.values));

        if (sensorEvent.sensor == lightSensor)
        {
            lightSensorValueTextField.setText(String.valueOf(sensorEvent.values[0]));
        }
        else if (sensorEvent.sensor == proximitySensor)
        {
            proximitySensorValueTextField.setText(String.valueOf(sensorEvent.values[0]));
        }
        else
        {
            Log.d(TAG, "Some other sensor");
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int i)
    {
    }
}
