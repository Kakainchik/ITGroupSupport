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
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TokenActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    public static final String TOKEN_KEY = "TOKEN_KEY";
    public static final String TOKEN_FILE_KEY = "TOKEN_FILE_KEY";

    //Response codes
    private static final int REQUEST_CREATE_CODE = 1;
    private static final int REQUEST_CHANGE_CODE = 2;

    private List<TokenFile> tokens = new ArrayList<>();
    private FloatingActionButton tokenFAB;
    private RecyclerView tokenList;
    private TokenAdapter adapter;
    private RelativeLayout rootLayout;

    private void setInitialData() {
        String[] paths = IOFileHelper.getPathTokenFiles(this);

        try {
            for(String str: paths) {
                TokenFile file = IOFileHelper.loadTokenFile(this, str);
                tokens.add(file);
            }
        } catch(IOException ex) {
            Toast.makeText(this, R.string.error_cannot_load_tokens, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        Toolbar toolbar = (Toolbar)findViewById(R.id.token_toolbar);
        setSupportActionBar(toolbar);

        setInitialData();

        tokenList = (RecyclerView)findViewById(R.id.token_list);
        tokenFAB = (FloatingActionButton)findViewById(R.id.fab);
        rootLayout = (RelativeLayout)findViewById(R.id.view_token_layout);

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

        //Создаём новый токен
        tokenFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), CreateTokenActivity.class);
                startActivityForResult(intent, REQUEST_CREATE_CODE);
            }
        });

        //Переход на страницу изменения токена при нажатии
        tokenList.addOnItemTouchListener(new RecyclerItemClickListener(this,
                tokenList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), CreateTokenActivity.class);
                        intent.putExtra(TOKEN_KEY, tokens.get(position).getToken());
                        intent.putExtra("position", position);
                        startActivityForResult(intent, REQUEST_CHANGE_CODE);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (tokens.isEmpty()) {
            ((TextView) findViewById(R.id.token_text_info)).setText("У вас нет никаких токенов");
            updateInfoUI(false);
        } else {
            updateInfoUI(true);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int direction, int position) {
        if (holder instanceof TokenAdapter.ViewHolder) {

            final int index = holder.getAdapterPosition();
            final TokenFile deletedToken = tokens.get(index);
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
            IOFileHelper.deleteTokenFile(this, deletedToken.getFullFileName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final TokenFile token;

        switch(requestCode) {
            case REQUEST_CREATE_CODE: {

                switch (resultCode) {
                    case CreateTokenActivity.RESULT_SAVED: {
                        token = (TokenFile)data.getSerializableExtra(TOKEN_FILE_KEY);
                        tokens.add(0, token);
                        adapter.notifyItemInserted(0);
                        tokenList.scrollToPosition(0);
                        Toast.makeText(this, R.string.title_token_saved, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case CreateTokenActivity.RESULT_SENT: {
                        token = (TokenFile)data.getSerializableExtra(TOKEN_FILE_KEY);
                        tokens.add(0, token);
                        adapter.notifyItemInserted(0);
                        tokenList.scrollToPosition(0);
                        Toast.makeText(this, R.string.title_token_sent, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case CreateTokenActivity.RESULT_ERROR: {
                        Toast.makeText(this, R.string.error_cannot_create_token, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default: {
                        super.onActivityResult(requestCode, resultCode, data);
                        break;
                    }
                }
                break;
            }
            case REQUEST_CHANGE_CODE: {

                switch (resultCode) {
                    case CreateTokenActivity.RESULT_SAVED: {
                        int position = data.getIntExtra("position", 0);
                        token = (TokenFile)data.getSerializableExtra(TOKEN_FILE_KEY);
                        tokens.set(position, token);
                        adapter.notifyItemChanged(position);
                        tokenList.scrollToPosition(0);
                        Toast.makeText(this, R.string.title_token_saved, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case CreateTokenActivity.RESULT_SENT: {
                        int position = data.getIntExtra("position", 0);
                        token = (TokenFile)data.getSerializableExtra(TOKEN_FILE_KEY);
                        tokens.set(position, token);
                        adapter.notifyItemInserted(0);
                        tokenList.scrollToPosition(0);
                        Toast.makeText(this, R.string.title_token_sent, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case CreateTokenActivity.RESULT_ERROR: {
                        Toast.makeText(this, R.string.error_cannot_create_token, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
                break;
            }
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