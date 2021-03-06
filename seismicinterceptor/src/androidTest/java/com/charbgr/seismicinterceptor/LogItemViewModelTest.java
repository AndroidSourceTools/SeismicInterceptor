package com.charbgr.seismicinterceptor;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import java.net.SocketTimeoutException;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.charbgr.seismicinterceptor.OkHttpHelperUtils.convertToRequestBody;
import static com.charbgr.seismicinterceptor.OkHttpHelperUtils.convertToResponseBody;
import static com.charbgr.seismicinterceptor.OkHttpHelperUtils.generateMockRequestWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class LogItemViewModelTest {

    private Context context;
    private Request request;
    private Response response;
    private LogItemViewModel viewModel;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getContext();
        request = generateMockRequestWith("GET", null);
    }

    @Test
    public void testIfSuccessResponseIsCorrectlyCalculated() {
        response = new Response.Builder()
                .code(200)
                .message("OK")
                .addHeader("Content-Type", "text/plain")
                .body(convertToResponseBody("MOCK"))
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .build();

        viewModel = new LogItemViewModel(context, request, new ResponseExceptionWrapper(response));
        assertEquals(ContextCompat.getColor(context, R.color.success_response), viewModel.getHttpStatusBgColor());
    }

    @Test
    public void testIfFailResponseIsCorrectlyCalculated() {
        response = new Response.Builder()
                .code(404)
                .message("OK")
                .addHeader("Content-Type", "text/plain")
                .body(convertToResponseBody("MOCK"))
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .build();

        viewModel = new LogItemViewModel(context, request, new ResponseExceptionWrapper(response));
        assertEquals(ContextCompat.getColor(context, R.color.failed_response), viewModel.getHttpStatusBgColor());
    }

    @Test
    public void testIfElementsCorrectlyCalculated() {
        response = new Response.Builder()
                .code(404)
                .message("OK")
                .addHeader("Content-Type", "text/plain")
                .body(convertToResponseBody("MOCK"))
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .build();

        viewModel = new LogItemViewModel(context, request, new ResponseExceptionWrapper(response));
        assertEquals("text/plain", viewModel.getContentType());
        assertEquals("GET", viewModel.getHttpVerb());
        assertEquals("http://localhost/", viewModel.getUrl());

        request = generateMockRequestWith("POST", convertToRequestBody("foo"));
        response = new Response.Builder()
                .code(404)
                .message("OK")
                .body(convertToResponseBody("MOCK"))
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .build();

        viewModel = new LogItemViewModel(context, request, new ResponseExceptionWrapper(response));
        assertEquals("Unknown", viewModel.getContentType());
        assertEquals("POST", viewModel.getHttpVerb());
    }

    @Test
    public void testIfExceptionIsCorrectlyCalculated() {
        SocketTimeoutException exception = new SocketTimeoutException("timeout!");

        viewModel = new LogItemViewModel(context, request, new ResponseExceptionWrapper(exception));
        assertNull(viewModel.getContentType());
        assertEquals("GET", viewModel.getHttpVerb());
        assertEquals("http://localhost/", viewModel.getUrl());
        assertEquals(ContextCompat.getColor(context, R.color.failed_response), viewModel.getHttpStatusBgColor());
    }
}
