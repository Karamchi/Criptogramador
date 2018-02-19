package com.example.karamchand.criptogramador;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class RootActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://gist.githubusercontent.com/Karamchi/208810ea17178a81dba5c039942cbd6d/raw/";
    public static final String PATH = "/pack";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_activity);
        new HttpAsyncTask(BASE_URL + "all",
                new HttpAsyncTask.HTTPListener() {
                    @Override
                    public void onResponseSuccessful(ArrayList<String> result) {
                        for (String s : result) {
                            ((LinearLayout) findViewById(R.id.root_activity_layout)).addView(getButton(s));
                            refreshFile(s);
                        }
                    }

                    @Override
                    public void onResponseFailure(int statusCode) {
                    }

                    @Override
                    public void onFailure() {
                    }
                }).execute(HttpAsyncTask.GET);

        findViewById(R.id.root_activity_builder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RootActivity.this, PhraseActivity.class));
            }
        });

        findViewById(R.id.root_activity_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FileUtils.exists(PATH, "temp.txt")) return;
                Intent intent = new Intent(RootActivity.this, PrintActivity.class);
                intent.putExtra("filename", "temp.txt");
                startActivity(intent);
            }
        });

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
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FileUtils.exists(PATH, s + ".txt")) return;
                Intent intent = new Intent(RootActivity.this, PrintActivity.class);
                intent.putExtra("filename", s + ".txt");
                startActivity(intent);
            }
        });
        return b;
    }

}
