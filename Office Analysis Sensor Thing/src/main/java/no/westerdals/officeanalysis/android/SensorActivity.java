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
import org.springframework.util.LinkedMultiValueMap;

@EActivity(R.layout.activity_sensor)
public class SensorActivity extends Activity implements SensorEventListener
{
    private final String TAG = SensorActivity.class.getSimpleName();

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

    @Bean
    DefaultRestErrorHandler defaultRestErrorHandler;

    @RestService
    LightClient lightClient;

    private final static int lightSendoffIntervalMilliseconds = 5000;

    private long nextLightSendoffMilliseconds = 0; // immediately

    private boolean spamLight = false;

    SensorManager sensorManager;

    Sensor lightSensor;

    Sensor proximitySensor;

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

        spamLight = true;
        spamLight();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        sensorManager.unregisterListener(this);

        spamLight = false;
    }

    @AfterInject
    void configureErrorHandlers()
    {
        lightClient.setRestErrorHandler(defaultRestErrorHandler);
    }

    @Background
    void spamLight()
    {
        for (; ; )
        {
            if (!spamLight)
            {
                break;
            }

            final long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis < nextLightSendoffMilliseconds)
            {
                return;
            }

            nextLightSendoffMilliseconds = currentTimeMillis + lightSendoffIntervalMilliseconds;
            prepareLightSendoff();

            try
            {
                Thread.sleep(lightSendoffIntervalMilliseconds);
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, "spamLight: could not sleep", e);
            }
        }

    }

    @Click(R.id.sendLightButton)
    void prepareLightSendoff()
    {
        if (lightSensorValueTextField == null
                || integrationIdEditText == null)
        {
            Log.d(TAG, "A text field was null");
            return;
        }

        final String rootUrl = serverIpEditText.getText().toString();
        lightClient.setRootUrl(rootUrl);

        final String lightString = lightSensorValueTextField.getText().toString();
        final String integrationId = integrationIdEditText.getText().toString();

        final Light light = new Light(lightString, integrationId);

        Log.d(TAG, "Prepared light sendoff. rootUrl = " + rootUrl + ", integrationId = " + integrationId + ", light = " + light);

        sendLight(light);
    }

    @Background
    @Trace
    void sendLight(Light light)
    {
        final LinkedMultiValueMap<String, String> lightMultiValueMap = new LinkedMultiValueMap<>();

        lightMultiValueMap.add("value", light.getValue());
        lightMultiValueMap.add("integrationId", light.getIntegrationId());

        String response = lightClient.send(lightMultiValueMap);

        Log.d(TAG, "Got response: " + response);
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent)
    {
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
