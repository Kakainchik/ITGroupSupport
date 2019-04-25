package kz.itgroup.itgroupsupport;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;

public class CreateTokenActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText themeText, descriptionText;
    private Button saveToken, sendToken;
    private TokenModel currentToken;
    private Bundle bundle;
    private int accountId;
    private boolean isBusy = false;

    private static final String TAG = "Create token activity";
    public static final int RESULT_SENT = 3;
    public static final int RESULT_ERROR = 2;
    public static final int RESULT_SAVED = 1;
    public static final int RESULT_CANCELED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_token);

        themeText = (EditText)findViewById(R.id.token_theme_text);
        descriptionText = (EditText)findViewById(R.id.token_description_text);
        saveToken = (Button)findViewById(R.id.save_token_button);
        sendToken = (Button)findViewById(R.id.send_token_button);

        bundle = getIntent().getExtras();
        if(bundle != null) {
            currentToken = (TokenModel)bundle.get(TokenActivity.TOKEN_KEY);

            themeText.setText(currentToken.getTitle());
            descriptionText.setText(currentToken.getDescription());

            if(currentToken.getState() == TokenState.SENT ||
                    currentToken.getState() == TokenState.HANDLED)
                setEnabledUI(false);
        }

        themeText.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if(currentToken != null && (
                currentToken.getState() == TokenState.SENT |
                currentToken.getState() == TokenState.HANDLED)) {
            super.onBackPressed();
            return;
        }

        if(!isBusy) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogPrimaryStyle)
                    .setMessage(R.string.alert_cancel_token)
                    .setPositiveButton(R.string.alert_continue, new DialogInterface.OnClickListener() {
                        //Не сохранять
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    })
                    .setNeutralButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                        //Отмена
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            if (isValidToSave())
                builder.setNegativeButton(R.string.title_save_token, new DialogInterface.OnClickListener() {
                    //Сохранить
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveToken();
                    }
                });

            builder.create().show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_token_button: {
                SendToken();
                break;
            }
            case R.id.save_token_button: {
                SaveToken();
                break;
            }
        }
    }

    //Отправить токен
    private void SendToken() {
        if (!isValidToSave()) {
            Toast.makeText(this, R.string.error_field_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account == null) {
            Toast.makeText(this, R.string.error_auth_request, Toast.LENGTH_LONG).show();
            return;
        }

        this.setEnabledUI(false);
        isBusy = true;

        String theme = themeText.getText().toString();
        String description = descriptionText.getText().toString();
        TokenFile file;

        if(currentToken == null)
            currentToken = new TokenModel(theme, description);
        else {
            currentToken.setDescription(description);
            currentToken.setTitle(theme);
        }

        Intent data = new Intent();

        String name = IOFileHelper.getFileTokenName(currentToken);
        file = new TokenFile(currentToken, name);

        //Создаём задачу авторизации и выполняем её
        AuthorizationAsyncTask authTask = new AuthorizationAsyncTask(this, out -> {
            //Получаем id
            accountId = out.getResponse();

            //Отменяем если имеются ошибки
            if(out.getCodeResponse() != HttpAsyncTask.OK) {
                this.setEnabledUI(true);
                isBusy = false;
                return;
            }
            //Создаём задачу отправки токена на сервер и выполняем её
            SendTokenAsyncTask sendTask = new SendTokenAsyncTask(this, response -> {
                //Эту задачу также проверяем на ошибки
                if(response.getCodeResponse() != HttpAsyncTask.OK) {
                    this.setEnabledUI(true);
                    isBusy = false;
                    return;
                }

                currentToken.setState(TokenState.SENT);
                if(bundle != null && bundle.containsKey("position"))
                    data.putExtra("position", bundle.getInt("position"));

                try {
                    IOFileHelper.saveTokenFile(this, currentToken);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.e(TAG, ex.getMessage());
                    setResult(RESULT_ERROR);
                    super.finish();
                }

                data.putExtra(TokenActivity.TOKEN_FILE_KEY, file);
                setResult(RESULT_SENT, data);

                super.finish();
            });
            sendTask.execute(file, accountId);
        });
        //Запрашиваем id для авторизации
        authTask.execute(account);

        Log.d(TAG, "Send request");
    }

    //Сохранить токен
    private void SaveToken() {
        if (!isValidToSave()) {
            Toast.makeText(this, R.string.error_field_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        String theme = themeText.getText().toString();
        String description = descriptionText.getText().toString();
        TokenFile file;

        if(currentToken == null)
            currentToken = new TokenModel(theme, description);
        else {
            currentToken.setDescription(description);
            currentToken.setTitle(theme);
        }

        Intent data = new Intent();

        try {
            IOFileHelper.saveTokenFile(this, currentToken);
            String name = IOFileHelper.getFileTokenName(currentToken);
            file = new TokenFile(currentToken, name);
            data.putExtra(TokenActivity.TOKEN_FILE_KEY, file);
        } catch(IOException ex) {
            Log.e(TAG, ex.getMessage());
            setResult(RESULT_ERROR);
            super.finish();
        }

        if(bundle != null && bundle.containsKey("position"))
            data.putExtra("position", bundle.getInt("position"));

        setResult(RESULT_SAVED, data);

        super.finish();
    }

    private boolean isValidToSave() {
        if (themeText.length() == 0 | descriptionText.length() == 0) return false;
        else return true;
    }

    private void setEnabledUI(boolean enabled) {
        if(enabled) {
            descriptionText.setEnabled(true);
            themeText.setEnabled(true);
            saveToken.setEnabled(true);
            sendToken.setEnabled(true);
        } else {
            descriptionText.setEnabled(false);
            themeText.setEnabled(false);
            saveToken.setEnabled(false);
            sendToken.setEnabled(false);
        }
    }
}