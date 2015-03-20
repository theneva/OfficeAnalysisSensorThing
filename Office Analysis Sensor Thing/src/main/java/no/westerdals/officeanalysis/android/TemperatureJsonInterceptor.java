package no.westerdals.officeanalysis.android;

import android.util.Log;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class TemperatureJsonInterceptor implements ClientHttpRequestInterceptor
{
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException
    {
        request.getHeaders().set("Content-Type", "application/json");
        Log.d(TemperatureJsonInterceptor.class.getSimpleName(), "Set content type of request to application/json");
        return execution.execute(request, body);
    }
}
