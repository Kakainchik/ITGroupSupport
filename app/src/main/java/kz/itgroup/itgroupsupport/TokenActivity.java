package kz.itgroup.itgroupsupport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.List;

public class TokenActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    private List<TokenModel> tokens = new ArrayList<>();
    public static final String TOKEN_KEY = "TOKEN_KEY";
    private FloatingActionButton tokenFAB;
    private static final int REQUEST_ACCESS_CODE = 1;
    private RecyclerView tokenList;
    private TokenAdapter adapter;
    private RelativeLayout rootLayout;

    private void setInitialData() {
        tokens.add(new TokenModel("Mars One", "is bankrot"));
        tokens.add(new TokenModel("Me", "Too"));
        tokens.add(new TokenModel("Mars One", "is lucky"));
        tokens.add(new TokenModel("Me", "Too"));
        tokens.add(new TokenModel("Mars One", "is on Mars"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        Toolbar toolbar = (Toolbar) findViewById(R.id.token_toolbar);
        setSupportActionBar(toolbar);

        setInitialData();

        tokenList = (RecyclerView) findViewById(R.id.token_list);
        tokenFAB = (FloatingActionButton) findViewById(R.id.fab);
        rootLayout = (RelativeLayout) findViewById(R.id.view_token_layout);

        //Устанавливаем адаптер токенов
        adapter = new TokenAdapter(this, R.layout.token_list_item, tokens);

        tokenList.setHasFixedSize(true);
        final RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        final DividerItemDecoration decoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);

        tokenList.setItemAnimator(animator);
        tokenList.addItemDecoration(decoration);
        tokenList.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack
                = new RecycleItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(tokenList);

        tokenFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), CreateTokenActivity.class);
                ;
                startActivityForResult(intent, REQUEST_ACCESS_CODE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (tokens.isEmpty()) {
            ((TextView) findViewById(R.id.token_text_info)).setText("У вас нет никаких токенов");
        } else {
            updateInfoUI(true);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int direction, int position) {
        if (holder instanceof TokenAdapter.ViewHolder) {

            final int index = holder.getAdapterPosition();
            final TokenModel deletedToken = tokens.get(index);
            adapter.removeItem(index);

            Snackbar snackbar = Snackbar.make(rootLayout, R.string.alert_token_deleted, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.alert_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tokens.add(index, deletedToken);
                    adapter.notifyItemInserted(index);
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAddToken));
            snackbar.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final TokenModel token;

        if (requestCode == REQUEST_ACCESS_CODE) {
            if (resultCode == RESULT_OK) {
                token = (TokenModel) data.getSerializableExtra(TOKEN_KEY);
                tokens.add(0, token);
                adapter.notifyItemInserted(0);
                tokenList.scrollToPosition(0);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateInfoUI(boolean hide) {
        if (hide) {
            ((RelativeLayout) findViewById(R.id.image_token_layout)).setVisibility(View.GONE);
        } else {
            ((RelativeLayout) findViewById(R.id.image_token_layout)).setVisibility(View.VISIBLE);
        }
    }
}