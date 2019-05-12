package kz.itgroup.itgroupsupport;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTokenActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText themeText, descriptionText;
    private Button saveToken, sendToken, addFileButton;
    private TokenModel currentToken;
    private List<FileChip> currentChips = new ArrayList<FileChip>();
    private FileChipAdapter adapter;
    private RecyclerView recyclerView;
    private Bundle bundle;
    private int accountId;
    private boolean isBusy = false;

    private static final String TAG = "Create token activity";

    //Response codes
    public static final int RESULT_SENT = 3;
    public static final int RESULT_ERROR = 2;
    public static final int RESULT_SAVED = 1;
    public static final int RESULT_CANCELED = 0;

    //Request codes
    public static final int REQUEST_PICKFILE_CODE = 8778;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_create_token);

        //Initializing views
        themeText = (EditText)findViewById(R.id.token_theme_text);
        descriptionText = (EditText)findViewById(R.id.token_description_text);
        saveToken = (Button)findViewById(R.id.save_token_button);
        sendToken = (Button)findViewById(R.id.send_token_button);
        addFileButton = (Button)findViewById(R.id.fad);
        recyclerView = (RecyclerView)findViewById(R.id.chip_list);

        //Get previous token if we want to change it
        bundle = getIntent().getExtras();
        if(bundle != null) {
            currentToken = (TokenModel)bundle.get(TokenActivity.TOKEN_KEY);

            themeText.setText(currentToken.getTitle());
            descriptionText.setText(currentToken.getDescription());

            //Set only for reading if token had been sent
            if(currentToken.getState() == TokenState.SENT ||
                    currentToken.getState() == TokenState.HANDLED) {
                setEnabledUI(false);

                //Add info of loaded files
                for(String key: currentToken.getFiles().keySet())
                    currentChips.add(FileChip.createShadow(key));
            }
        }

        //Initialize the adapter of files
        adapter = new FileChipAdapter(this, currentChips, new FileChipAdapter.OnChipClickListener() {
            //Remove this file
            @Override
            public void onCancelClick(int position) {
                currentChips.remove(position);
                adapter.notifyItemRemoved(position);
            }

            //Open this file
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndNormalize(currentChips.get(position).getData());

                PackageManager manager = getPackageManager();
                List<ResolveInfo> activities = manager.queryIntentActivities(intent, 0);

                if(activities.size() > 0) {
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 20, false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        themeText.requestFocus();
    }

    @Override
    public void onBackPressed() {
        //Just return back if one cannot change anything
        if(currentToken != null && (
                currentToken.getState() == TokenState.SENT |
                currentToken.getState() == TokenState.HANDLED)) {
            super.onBackPressed();
            return;
        }

        //Do not allow one to leave while performing
        if(!isBusy) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogPrimaryStyle)
                    .setMessage(R.string.alert_cancel_token)
                    .setPositiveButton(R.string.alert_continue, new DialogInterface.OnClickListener() {
                        //Do not save
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    })
                    .setNeutralButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                        //Cancel
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            if (isValidToSave())
                builder.setNegativeButton(R.string.title_save_token, new DialogInterface.OnClickListener() {
                    //Save
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveToken();
                    }
                });

            builder.create().show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_token_button: {  //Sending button
                sendToken();
                break;
            }
            case R.id.save_token_button: {  //Saving button
                saveToken();
                break;
            }
            case R.id.fad: {                //Adding a file button
                uploadFile();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode) {
            case REQUEST_PICKFILE_CODE: //If we add file

                switch(resultCode) {
                    case RESULT_OK: //If we added file
                        Uri uri = data.getData();
                        String type = MimeTypeMap.getFileExtensionFromUrl(data.getData().toString());

                        if(type == null) {
                            Toast.makeText(this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FileChip chip = null;

                        //If the file is an image
                        if(type.equals("jpeg") || type.equals("jpg") || type.equals("png")) {
                            final Uri image = data.getData();

                            try {
                                chip = new FileChip(image, this.getContentResolver());
                            } catch(IOException ex) {
                                ex.printStackTrace();
                                Toast.makeText(this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        //If the file is a video
                        else if(type.equals("mp4")) {
                            Bitmap image = FileChip.getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_movie_black_24dp);
                            chip = new FileChip(uri, image);
                        }
                        //If the file is a document
                        else {
                            Bitmap image = FileChip.getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_insert_drive_file_black_24dp);
                            chip = new FileChip(uri, image);
                        }

                        currentChips.add(chip);
                        adapter.notifyDataSetChanged();

                        break;
                }
                break;
        }
    }

    //Send the current token
    private void sendToken() {
        if (!isValidToSave()) {
            Toast.makeText(this, R.string.error_field_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        //Get the current google account
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

        //Encode all files to Base64
        for(FileChip chip: currentChips) {
            String name = FilenameUtils.getName(chip.getData().getPath());
            String base64Data = IOFileHelper.encodeWithBase64(this, chip.getData());
            base64Data = base64Data.replaceAll(System.getProperty("line.separator"), "");

            currentToken.addFile(name, base64Data);
        }

        Intent data = new Intent();

        String name = IOFileHelper.getFileTokenName(currentToken);
        file = new TokenFile(currentToken, name);

        //Create authorization task and perform it
        AuthorizationAsyncTask authTask = new AuthorizationAsyncTask(this, out -> {
            //Getting id
            accountId = out.getResponse();

            //Cancel if having errors
            if(out.getCodeResponse() != HttpAsyncTask.OK) {
                this.setEnabledUI(true);
                isBusy = false;
                return;
            }
            //Create task of sending token to the server and perform it
            SendTokenAsyncTask sendTask = new SendTokenAsyncTask(this, response -> {
                //Check this task for errors as well
                if(response.getCodeResponse() != HttpAsyncTask.OK) {
                    this.setEnabledUI(true);
                    isBusy = false;
                    return;
                }

                currentToken.setState(TokenState.SENT);
                if(bundle != null && bundle.containsKey("position"))
                    data.putExtra("position", bundle.getInt("position"));

                //Create a file of the token and save it
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
            //Send the token to the server
            sendTask.execute(file, accountId);
        });
        //Require the id for authorization
        authTask.execute(account);

        Log.d(TAG, "Send request");
    }

    //Save the current token
    private void saveToken() {
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

        //Encode all files to Base64
        for(FileChip chip: currentChips) {
            String name = FilenameUtils.getName(chip.getData().getPath());
            String base64Data = IOFileHelper.encodeWithBase64(this, chip.getData());

            currentToken.addFile(name, base64Data);
        }

        Intent data = new Intent();

        //Create a file of the token and return it back
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

    //Add doc(x), txt, ppt(x), rtf, pdf, ps, xls(x) file or png, jpg, jpeg, bmp image
    private void uploadFile() {

        final String types[] = {
                "video/mp4",
                "application/msword",
                "text/plain",
                "application/rtf",
                "application/pdf",
                "application/postscript",
                "application/excel",
                "image/png",
                "image/jpeg",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, types);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, REQUEST_PICKFILE_CODE);
    }

    //Check if one can send or save this token
    private boolean isValidToSave() {
        return !(themeText.length() == 0 | descriptionText.length() == 0);
    }

    private void setEnabledUI(boolean enabled) {
        if(enabled) {
            descriptionText.setEnabled(true);
            themeText.setEnabled(true);
            saveToken.setEnabled(true);
            sendToken.setEnabled(true);
            addFileButton.setEnabled(true);
            recyclerView.setEnabled(true);
        } else {
            descriptionText.setEnabled(false);
            themeText.setEnabled(false);
            saveToken.setEnabled(false);
            sendToken.setEnabled(false);
            addFileButton.setEnabled(false);
            recyclerView.setEnabled(false);
        }
    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); //item position
            int column = position % spanCount; //item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) { //top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; //item bottom
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing; //item top
                }
            }
        }
    }
}