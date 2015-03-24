package no.westerdals.officeanalysis.android;

import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;

@Rest(interceptors = {XWwwFormUrlEncodedInterceptor.class}, converters = {FormHttpMessageConverter.class, StringHttpMessageConverter.class})
public interface LightClient extends RestClientErrorHandling
{
    void setRootUrl(String rootUrl);

    @Post("/api/sensordata")
    String send(LinkedMultiValueMap<String, String> light);
}
