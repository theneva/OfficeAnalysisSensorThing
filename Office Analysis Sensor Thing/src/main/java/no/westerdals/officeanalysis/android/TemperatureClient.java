package no.westerdals.officeanalysis.android;

import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@Rest(interceptors = {TemperatureJsonInterceptor.class, ApiKeyInterceptor.class}, converters = {GsonHttpMessageConverter.class, StringHttpMessageConverter.class})
public interface TemperatureClient
{
    void setRootUrl(String rootUrl);

    @Post("/api/integrations/{integrationId}")
    String send(Temperature temperature, String integrationId);
}
