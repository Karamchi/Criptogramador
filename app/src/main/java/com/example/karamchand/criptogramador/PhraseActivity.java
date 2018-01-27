package com.example.karamchand.criptogramador;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PhraseActivity extends AppCompatActivity {

    private int mPhraseLength;
    private int mTitleLength;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_phrase);
        findViewById(R.id.button_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PhraseActivity.this)
                        .setTitle("Sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reset();
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        findViewById(R.id.button_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PhraseActivity.this, MainActivity.class));
            }
        });
        ((TextView) findViewById(R.id.phrase)).addTextChangedListener(getTextWatcher());
        ((TextView) findViewById(R.id.title)).addTextChangedListener(getTextWatcher());
    }

    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mPhraseLength = toAlpha(((TextView) findViewById(R.id.phrase)).getText().toString()).length();
                mTitleLength = toAlpha(((TextView) findViewById(R.id.title)).getText().toString()).length();
                ((TextView) findViewById(R.id.phrase_length)).setText(Integer.toString(mPhraseLength));
                ((TextView) findViewById(R.id.title_length)).setText(Integer.toString(mTitleLength));
                float proportion = mPhraseLength/((float) mTitleLength);
                if (mTitleLength > 0)
                    ((TextView) findViewById(R.id.proportion)).setText(String.format("%.2f", proportion));
                else
                    ((TextView) findViewById(R.id.proportion)).setText("");

            }
        };
    }

    public static String toAlpha(String s) {
        s = s.toLowerCase();
        for (int i = 0; i < 6; i++)
            s = s.replace("áéíóúü".charAt(i), "aeiouu".charAt(i));

        return s.replaceAll("[^a-z]", "");
    }

    public void reset() {
        Intent intent = new Intent(PhraseActivity.this, MainActivity.class);
        intent.putExtra("phrase", ((EditText) findViewById(R.id.phrase)).getText().toString());
        intent.putExtra("title", ((EditText) findViewById(R.id.title)).getText().toString());
        startActivity(intent);
    }

}