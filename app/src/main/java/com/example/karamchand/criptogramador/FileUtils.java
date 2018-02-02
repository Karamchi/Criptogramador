package com.example.karamchand.criptogramador;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FileUtils {

    public final static String ROOT = Environment.getExternalStorageDirectory() + "/crip";

    //Path must start with /
    public static String saveWithTimeStamp(String id, String path, ArrayList<String> contents) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
        String filename = id + "_" + format.format(c.getTime());
        save(path, filename, contents);
        return filename;
    }

    public static void save(String path, String filename, ArrayList<String> contents) {
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

    public static void load(Context context, final LoadListener listener, final String path) {
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
}
