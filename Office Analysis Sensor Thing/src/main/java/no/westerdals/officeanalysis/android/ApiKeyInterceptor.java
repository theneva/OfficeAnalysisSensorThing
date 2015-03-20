package no.westerdals.officeanalysis.android;

import android.util.Log;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class ApiKeyInterceptor implements ClientHttpRequestInterceptor
{
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException
    {
        final String xapikey = "1234";
        request.getHeaders().set("x-apikey", xapikey);
        Log.d(ApiKeyInterceptor.class.getSimpleName(), "Set x-apikey header to: " + xapikey);
        return execution.execute(request, body);
    }
}
