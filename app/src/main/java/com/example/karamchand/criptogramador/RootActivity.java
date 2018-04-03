package com.example.karamchand.criptogramador;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class RootActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://gist.githubusercontent.com/Karamchi/208810ea17178a81dba5c039942cbd6d/raw/";
    public static final String PATH = "/pack";
    private Button mResumeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_activity);

        findViewById(R.id.root_activity_builder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RootActivity.this, PhraseActivity.class));
            }
        });

        mResumeButton = new Button(this);
        mResumeButton.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        mResumeButton.setText("Resume");
        mResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canResume()) return;
                Intent intent = new Intent(RootActivity.this, PrintActivity.class);
                intent.putExtra("filename", "temp.txt");
                String[] current = ProfileUtils.getProfile().get("current").split(":");
                intent.putExtra("title", current[0]);
                intent.putExtra("time", Integer.parseInt(current[1]));
                startActivity(intent);
            }
        });

        if (!isMyServiceRunning(ConnectivityService.class)) {
            Intent serviceIntent = new Intent(this, ConnectivityService.class);
            startService(serviceIntent);
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new HttpAsyncTask(BASE_URL + "all",
                new HttpAsyncTask.HTTPListener() {
                    @Override
                    public void onResponseSuccessful(ArrayList<String> result) {
                        for (String s : result)
                            refreshFile(s);
                        onResponse(result);
                    }

                    @Override
                    public void onResponseFailure(int statusCode) {
                    }

                    @Override
                    public void onFailure() {
                        onResponse(null);
                    }
                }).execute(HttpAsyncTask.GET);
    }

    private void onResponse(ArrayList<String> result) {
        ((LinearLayout) findViewById(R.id.root_activity_layout)).removeAllViews();
        if (canResume())
            ((LinearLayout) findViewById(R.id.root_activity_layout)).addView(mResumeButton);
        TreeSet<String> set = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    int int1 = Integer.parseInt(o1);
                    int int2 = Integer.parseInt(o2);
                    return (int) Math.signum(int1 - int2);
                } catch (Exception e) {
                    return o1.compareTo(o2);
                }
            }
        });
        String[] list = FileUtils.getDirList(this, PATH);
        for (int i = 0; i < list.length; i++) list[i] = list[i].replace(".txt", "");
        set.addAll(Arrays.asList(list));
        if (result != null) set.addAll(result);
        for (String s : set) {
            if (s.equals("temp")) continue;
            ((LinearLayout) findViewById(R.id.root_activity_layout)).addView(getButton(s));
            refreshFile(s);
        }
    }

    private boolean canResume() {
        return FileUtils.exists(PATH, "temp.txt");
    }

    private void refreshFile(final String s) {
        new HttpAsyncTask(BASE_URL + s,
                new HttpAsyncTask.HTTPListener() {
                    @Override
                    public void onResponseSuccessful(ArrayList<String> result) {
                        FileUtils.save(RootActivity.this, PATH, s, result);
                    }

                    @Override
                    public void onResponseFailure(int statusCode) {
                    }

                    @Override
                    public void onFailure() {
                    }
                }).execute(HttpAsyncTask.GET);
    }

    private View getButton(final String s) {
        Button b = new Button(this);
        b.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        b.setText(s);
        if (ProfileUtils.getProfile().containsKey(s)) {
            b.setTextColor(Color.GREEN);
            b.setText(s + "\n" + "Solved in " +
                    ProfileUtils.getProfile().get(s) + "s");
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FileUtils.exists(PATH, s + ".txt")) return;
                if (!canResume()) {
                    startPuzzle(s);
                    return;
                }
                new AlertDialog.Builder(RootActivity.this)
                        .setTitle("Progress on current puzzle will be lost")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startPuzzle(s);
                            }
                        })
                        .setNegativeButton("back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
        return b;
    }

    private void startPuzzle(String s) {
        Intent intent = new Intent(RootActivity.this, PrintActivity.class);
        intent.putExtra("filename", s + ".txt");
        intent.putExtra("title", s);
        startActivity(intent);
    }

}
