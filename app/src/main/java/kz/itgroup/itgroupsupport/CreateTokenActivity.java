package kz.itgroup.itgroupsupport;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateTokenActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText themeText, descriptionText;
    private Button saveToken, sendToken;
    private TokenModel currentToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_token);

        themeText = (EditText) findViewById(R.id.token_theme_text);
        descriptionText = (EditText) findViewById(R.id.token_description_text);
        saveToken = (Button) findViewById(R.id.save_token_button);
        sendToken = (Button) findViewById(R.id.send_token_button);

        themeText.requestFocus();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogPrimaryStyle)
                .setMessage(R.string.alert_cancel_token)
                .setPositiveButton(R.string.alert_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .setNeutralButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        if (isValidToSave())
            builder.setNegativeButton(R.string.title_save_token, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SaveToken();
                }
            });

        builder.create().show();
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

    private void SendToken() {
        if (!isValidToSave()) {
            Toast.makeText(this, R.string.error_field_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();
    }

    private void SaveToken() {
        if (!isValidToSave()) {
            Toast.makeText(this, R.string.error_field_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        String theme = themeText.getText().toString();
        String description = descriptionText.getText().toString();
        currentToken = new TokenModel(theme, description);

        Intent data = new Intent();
        data.putExtra(TokenActivity.TOKEN_KEY, currentToken);
        setResult(RESULT_OK, data);

        super.finish();
    }

    private boolean isValidToSave() {
        if (themeText.length() == 0 | descriptionText.length() == 0) return false;
        else return true;
    }
}