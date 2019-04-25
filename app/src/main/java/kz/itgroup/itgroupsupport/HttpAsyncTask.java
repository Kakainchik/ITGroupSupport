package kz.itgroup.itgroupsupport;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public abstract class HttpAsyncTask<Param, During, Response> extends AsyncTask<Param, During, NetResponse<Response>> {

    protected final String URI = "http://192.168.100.2:8080/support/";
    protected Context context;
    protected String response;
    public AsyncResponse<NetResponse<Response>> delegate = null;
    public static final int OK = 0;
    public static final int UNKNOWN_ERROR = -1;
    public static final int NO_INTERNET_CONNECTION_ERROR = -2;
    public static final int INTERNAL_SERVER_ERROR = -3;
    public static final int CONNECTION_TIMEOUT_ERROR = -4;

    protected HttpAsyncTask(Context context, AsyncResponse<NetResponse<Response>> delegate) {
        super();
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(NetResponse<Response> r) {
        super.onPostExecute(r);
        switch(r.getCodeResponse()) {
            case NO_INTERNET_CONNECTION_ERROR: {
                Toast.makeText(context, R.string.error_internet_connection, Toast.LENGTH_LONG).show();
                break;
            }
            case INTERNAL_SERVER_ERROR: {
                Toast.makeText(context, R.string.error_internal_server, Toast.LENGTH_LONG).show();
                break;
            }
            case UNKNOWN_ERROR: {
                Toast.makeText(context, R.string.error_unknown, Toast.LENGTH_LONG).show();
                break;
            }
            case CONNECTION_TIMEOUT_ERROR: {
                Toast.makeText(context, R.string.error_connection_timeout, Toast.LENGTH_LONG).show();
                break;
            }
            default: break;
        }
        delegate.processFinish(r);
    }

    protected NetResponse<Response> returnException(Exception ex, Response response) {
        if(ex instanceof UnsupportedEncodingException) {

            ex.printStackTrace();
            return new NetResponse<>(response, UNKNOWN_ERROR);
        }else if(ex instanceof ClientProtocolException) {

            ex.printStackTrace();
            if(ex.fillInStackTrace() instanceof HttpResponseException)
                switch(((HttpResponseException)ex).getStatusCode()) {

                    case 501: return new NetResponse<>(response, INTERNAL_SERVER_ERROR);

                    default: return new NetResponse<>(response, UNKNOWN_ERROR);
                } else return new NetResponse<>(response, UNKNOWN_ERROR);
        } else if(ex instanceof IOException) {

            ex.printStackTrace();
            if(ex.fillInStackTrace() instanceof ConnectTimeoutException)
                return new NetResponse<>(response, CONNECTION_TIMEOUT_ERROR);
            else return new NetResponse<>(response, NO_INTERNET_CONNECTION_ERROR);
        } else {

            ex.printStackTrace();
            return new NetResponse<>(response, UNKNOWN_ERROR);
        }
    }

    public interface AsyncResponse<Out> {
        void processFinish(Out output);
    }
}