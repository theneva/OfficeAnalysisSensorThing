package no.westerdals.officeanalysis.android;

import android.util.Log;
import org.androidannotations.annotations.EBean;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

@EBean
public class DefaultRestErrorHandler implements RestErrorHandler
{
    private static final String TAG = DefaultRestErrorHandler.class.getSimpleName();

    @Override
    public void onRestClientExceptionThrown(final NestedRuntimeException e)
    {
        Log.e(TAG, "Rest client exception thrown", e);
    }
}
