package no.westerdals.officeanalysis.android;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

import java.util.Date;

@EActivity(R.layout.activity_temperature)
public class TemperatureActivity extends Activity
{
    @ViewById
    EditText serverIpEditText;

    @ViewById
    EditText temperatureEditText;

    @ViewById
    EditText integrationIdEditText;

    @RestService
    TemperatureClient temperatureClient;

    @Click(R.id.sendTemperatureButton)
    void prepareTemperatureSendoff()
    {
        final String rootUrl = serverIpEditText.getText().toString();
        temperatureClient.setRootUrl(rootUrl);

        final String integrationId = integrationIdEditText.getText().toString();

        final String temperatureString = temperatureEditText.getText().toString();
        final String timestamp = String.valueOf(new Date());

        final Temperature temperature = new Temperature(temperatureString, timestamp);

        Log.d(TemperatureActivity.class.getSimpleName(), "Prepared temperature sendoff. rootUrl = " + rootUrl + ", integrationId = " + integrationId + ", temperature = " + temperature);

        sendTemperature(temperature, integrationId);
    }

    @Background
    void sendTemperature(Temperature temperature, String integrationId)
    {
        Log.d(TemperatureActivity.class.getSimpleName(), "Sent temperature: " + temperature + " from integration id: " + integrationId);
        String response = temperatureClient.send(temperature, integrationId);
        Log.d(TemperatureActivity.class.getSimpleName(), "Got response: " + response);
    }
}
