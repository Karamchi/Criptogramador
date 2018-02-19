package com.example.karamchand.criptogramador;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FileUtils {

    public final static String ROOT = Environment.getExternalStorageDirectory() + "/crip";

    //Path must start with /
    public static String saveWithTimeStamp(Activity context, String id, String path, ArrayList<String> contents) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
        String filename = id + "_" + format.format(c.getTime());
        save(context, path, filename, contents);
        return filename;
    }

    public static void save(Activity context, String path, String filename, ArrayList<String> contents) {
        requestPermissions(context);
        File dir = new File(ROOT + path);
        dir.mkdirs();
        File file = new File(dir, filename + ".txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            for (String s : contents) {
                pw.println(s);
            }
            pw.flush();
            pw.close();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Activity context, final LoadListener listener, final String path) {
        requestPermissions(context);
        final File dir = new File(ROOT + path);
        dir.mkdirs();
        final String[] mFileList = dir.list();
        if (mFileList == null) return;
        new AlertDialog.Builder(context)
                .setTitle("Load file")
                .setItems(mFileList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
                        listener.onLoad(readFromFile(path, mFileList[which]), mFileList[which]);
                    }
                })
                .show();
    }

    public static ArrayList<String> readFromFile(String path, String filename) {
        ArrayList<String> result = new ArrayList<>();
        try {
            FileInputStream fIn = new FileInputStream(new File(ROOT + path + "/" + filename));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));

            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public interface LoadListener {
        void onLoad(ArrayList<String> s, String filename);
    }

    public static void copyFile(File src, File dst) {
        try {
            FileInputStream fInStream = new FileInputStream(src);
            FileOutputStream fOutStream = new FileOutputStream(dst);
            byte[] buffer = new byte[1024];

            int inputSize;
            while ((inputSize = fInStream.read(buffer)) > 0)
                fOutStream.write(buffer, 0, inputSize);

            fInStream.close();
            fOutStream.close();
        } catch (Exception e) {

        }
    }

    private static void requestPermissions(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (context.getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName())
                    == PackageManager.PERMISSION_DENIED)
                context.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);
    }

    public static ArrayList<String> streamToString(InputStream inputStream) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        InputStreamReader streamReader = new InputStreamReader(bufferedInputStream);
        BufferedReader buffer = new BufferedReader(streamReader);
        ArrayList<String> data = new ArrayList();
        try {
            String chunk = buffer.readLine();
            while (chunk != null) {
                data.add(chunk);
                chunk = buffer.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
