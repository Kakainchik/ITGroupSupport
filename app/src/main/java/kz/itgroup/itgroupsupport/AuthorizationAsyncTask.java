package kz.itgroup.itgroupsupport;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationAsyncTask extends HttpAsyncTask<GoogleSignInAccount, Void, Integer> {

    private final String AUTH_REQUEST = "getUser";
    private final String TAG = "AuthorizationAsyncTask";

    public AuthorizationAsyncTask(Context context, AsyncResponse<NetResponse<Integer>> delegate) {
        super(context, delegate);
    }

    @Override
    protected NetResponse<Integer> doInBackground(GoogleSignInAccount... googleSignInAccounts) {

        GoogleSignInAccount account = googleSignInAccounts[0];

        try {
            Task<InstanceIdResult> task = FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(resultTask -> {
                        if(!resultTask.isSuccessful()) {
                            Log.w("Firebase get id", "getInstanceId failed", resultTask.getException());
                            return;
                        }

                        //Get new Instance ID token
                        final String id = resultTask.getResult().getToken();

                        //Log
                        Log.d(TAG, id);
                    });
            if(!task.isSuccessful()) returnException(new Exception(), -1);

            final String appToken = task.getResult().getToken();
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpClient client = new DefaultHttpClient(params);
            List nameValuePairs = new ArrayList(3);
            nameValuePairs.add(new BasicNameValuePair("email", account.getEmail()));
            nameValuePairs.add(new BasicNameValuePair("gmailId", account.getId()));
            nameValuePairs.add(new BasicNameValuePair("tokenId", appToken));

            HttpPost post = new HttpPost(URI + AUTH_REQUEST);
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = (String)client.execute(post, new BasicResponseHandler());
            int id = JsonHelper.convertToObject(response, AuthorizationAsyncTask.Id.class).id;

            return new NetResponse<Integer>(id, OK);
        } catch(Exception ex) {
            return returnException(ex, -1);
        }
    }

    class Id {
        public int id;
    }
}