package kz.itgroup.itgroupsupport;

import android.content.Context;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/*
 * Object[0] represents token data as TokenFile
 * Object[1] represents token id as Integer
 */
public class SendTokenAsyncTask extends HttpAsyncTask<Object, Void, Void> {

    private final String SEND_REQUEST = "send";

    public SendTokenAsyncTask(Context context, AsyncResponse<NetResponse<Void>> delegate) {
        super(context, delegate);
    }

    @Override
    protected NetResponse<Void> doInBackground(Object... objects)
            throws InvalidParameterException {

        if(!(objects[0] instanceof TokenFile) || !(objects[1] instanceof Integer))
            throw new InvalidParameterException("One of parameters does not represent valid type");

        TokenFile file = (TokenFile)objects[0];
        int id = (int)objects[1];
        TokenModel token = file.getToken();
        String json = JsonHelper.<TokenModel>convertToJson(token);

        try {
            HttpClient client = new DefaultHttpClient();
            List nameValuePairs = new ArrayList(2);
            nameValuePairs.add(new BasicNameValuePair("context", json));
            nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

            HttpPost post = new HttpPost(URI + SEND_REQUEST);
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = (String)client.execute(post, new BasicResponseHandler());

            return new NetResponse<>(null, OK);
        } catch(Exception ex) {
            return returnException(ex, null);
        }
    }
}
