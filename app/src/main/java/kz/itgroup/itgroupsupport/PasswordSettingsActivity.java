package kz.itgroup.itgroupsupport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordSettingsActivity extends AppCompatActivity {

    private EditText oldPass, newPass, renewPass;
    private Button acceptBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_settings);
        oldPass = (EditText)findViewById(R.id.old_pass_text);
        newPass = (EditText)findViewById(R.id.new_pass_text);
        renewPass = (EditText)findViewById(R.id.renew_pass_text);
        acceptBt = findViewById(R.id.accept_button);

    }

    public void onCancelButtonClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onAcceptButtonClick(View view) {

        //Если новые пароли не совпадают
        if(!newPass.getText().toString().equals(renewPass.getText().toString())) {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.error_pass_not_match),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Если новый пароль совпадает со старым
        if(newPass.getText().toString().equals(oldPass.getText().toString())) {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.error_pass_match),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
