package com.josephb.lostandfoundapp;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public final class ImageStorageUtil {

    private static final String IMAGE_DIR = "item_images";

    private ImageStorageUtil() {}

    @Nullable
    public static String copyUriToInternal(@NonNull Context context, @NonNull Uri uri) {
        File dir = new File(context.getFilesDir(), IMAGE_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }

        File outFile = new File(dir, "img_" + UUID.randomUUID() + ".jpg");

        try (InputStream in = context.getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(outFile)) {

            if (in == null) return null;

            byte[] buffer = new byte[8 * 1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            return outFile.getAbsolutePath();

        } catch (IOException e) {
            if (outFile.exists()) {
                outFile.delete();
            }
            return null;
        }
    }
}