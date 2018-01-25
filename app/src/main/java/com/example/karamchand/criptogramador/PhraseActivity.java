package com.example.karamchand.criptogramador;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class PhraseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhraseActivity.this, MainActivity.class);
                intent.putExtra("phrase", ((EditText) findViewById(R.id.phrase)).getText().toString());
                intent.putExtra("title", ((EditText) findViewById(R.id.title)).getText().toString());
                startActivity(intent);
            }
        });
    }

}