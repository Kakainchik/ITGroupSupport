package kz.itgroup.itgroupsupport;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;

public class FileChip {

    private Uri data;
    private Bitmap image;
    private String fileName;
    private boolean isShadow;

    public FileChip(Uri data, ContentResolver resolver)
            throws IOException {
        this.data = data;
        this.image = MediaStore.Images.Media.getBitmap(resolver, data);
        this.isShadow = false;
        this.fileName = FilenameUtils.getName(data.getPath());
    }

    public FileChip(Uri data, Bitmap image) {
        this.data = data;
        this.image = image;
        this.isShadow = false;
        this.fileName = FilenameUtils.getName(data.getPath());
    }

    protected FileChip(String fileName) {
        this.data = null;
        this.image = null;
        this.isShadow = true;
        this.fileName = fileName;
    }

    public Uri getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isShadow() {
        return isShadow;
    }

    //Create chip without data
    public static FileChip createShadow(String fileName) {
        return new FileChip(fileName);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}