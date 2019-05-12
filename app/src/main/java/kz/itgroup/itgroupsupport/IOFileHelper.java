package kz.itgroup.itgroupsupport;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IOFileHelper {

    private static final String TOKEN_FILE_TYPE = ".json";
    private static final String TOKEN_DIR = "tokens";

    public static boolean saveTokenFile(Context context, @NonNull TokenModel token)
            throws IOException {

        String jsonString = JsonHelper.<TokenModel>convertToJson(token);
        FileOutputStream fos = null;

        try {

            String fileName = new StringBuilder()
                    .append(context.getDir(TOKEN_DIR, Context.MODE_PRIVATE))
                    .append("/")
                    .append(getFileTokenName(token)).toString();
            fos = new FileOutputStream(new File(fileName));

            fos.write(jsonString.getBytes());
            return true;
        } catch(IOException ex) {

            ex.printStackTrace();
        } finally {

            if(fos != null) fos.close();
        }

        return false;
    }

    public static TokenFile loadTokenFile(Context context, String name)
            throws IOException {

        String jsonString;
        TokenFile result = null;
        FileInputStream fis = null;
        String filePath = new StringBuilder()
                .append(context.getDir(TOKEN_DIR, Context.MODE_PRIVATE))
                .append("/")
                .append(name).toString();

        try {

            fis = new FileInputStream(new File(filePath));
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            jsonString = new String(bytes);
            result = new TokenFile(JsonHelper
                    .<TokenModel>convertToObject(jsonString, TokenModel.class),
                    FilenameUtils.getName(filePath));
        } catch(IOException ex) {

            ex.printStackTrace();
        } finally {

            if(fis != null) fis.close();
        }

        return result;
    }

    public static boolean deleteTokenFile(Context context, String name) {

        String filePath = new StringBuilder()
                .append(context.getDir(TOKEN_DIR, Context.MODE_PRIVATE))
                .append("/")
                .append(name).toString();

        File file = new File(filePath);
        return file.delete();
    }

    public static String[] getPathTokenFiles(Context context) {

        File fileList = context.getDir(TOKEN_DIR, Context.MODE_PRIVATE);
        File[] files = fileList.listFiles();
        String[] paths = new String[files.length];

        for(int i = 0; i < files.length; i++) {
            if(FilenameUtils.getExtension(files[i].getName()).equals("json")) {
                paths[i] = files[i].getName();
            }
        }

        return paths;
    }

    public static String getFileTokenName(TokenModel token) {

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHms");
        return sdf.format(new Date(token.getCreateDate())) + TOKEN_FILE_TYPE;
    }

    public static boolean contains(Context context, String name) {
        File fileList = context.getDir(TOKEN_DIR, Context.MODE_PRIVATE);
        for(File file: fileList.listFiles()) {
            if(file.isFile() && FilenameUtils.getName(file.getName()).equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static String encodeWithBase64(Context context, Uri data) {

        ContentResolver resolver = context.getContentResolver();
        byte bytes[] = null;

        try(FileInputStream fis = (FileInputStream)resolver.openInputStream(data)) {
            bytes = new byte[fis.available()];
            fis.read(bytes);
        } catch(IOException ioex) {
            ioex.printStackTrace();
        }

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}