package kz.itgroup.itgroupsupport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class CreateTokenActivity extends AppCompatActivity {

    private final static String APPLICATION_NAME = "ITGroup Support";
    private final static String TOKENS_DIRECTORY_PATH = "tokens";
    private final static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final static List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);
    private final static String CREDENTIALS_FILE_PATH = "/credentials.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_token);
    }
}