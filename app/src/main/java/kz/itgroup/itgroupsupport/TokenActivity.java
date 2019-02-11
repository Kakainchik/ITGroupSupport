package kz.itgroup.itgroupsupport;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class TokenActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TokenPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}