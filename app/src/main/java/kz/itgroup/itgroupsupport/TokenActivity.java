package kz.itgroup.itgroupsupport;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.internet.MimeMessage;

public class TokenActivity extends AppCompatActivity {

    private List<TokenModel> tokens = new ArrayList<>();
    private ListView tokenList;
    private FloatingActionButton tokenFAB;

    private void setInitialData() {
        tokens.add(new TokenModel("Mars One", "is bankrot"));
        tokens.add(new TokenModel("Me", "Too"));
        tokens.add(new TokenModel("Mars One", "is bankrot"));
        tokens.add(new TokenModel("Me", "Too"));
        tokens.add(new TokenModel("Mars One", "is bankrot"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        Toolbar toolbar = (Toolbar) findViewById(R.id.token_toolbar);
        setSupportActionBar(toolbar);

        setInitialData();

        tokenList = (ListView) findViewById(R.id.token_list);
        tokenFAB = (FloatingActionButton) findViewById(R.id.fab);

        TokenAdapter adapter = new TokenAdapter(this, R.layout.token_list_item, tokens);

        tokenList.setAdapter(adapter);
        tokenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TokenModel selectedToken = (TokenModel) parent.getItemAtPosition(position);
            }
        });

        tokenFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), CreateTokenActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
    }
}