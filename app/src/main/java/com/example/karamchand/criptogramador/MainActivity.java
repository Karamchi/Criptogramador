package com.example.karamchand.criptogramador;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Deque;
import java.util.HashMap;

import static com.example.karamchand.criptogramador.LettersView.VOCALES;

public class MainActivity extends AppCompatActivity implements WordsView.OnWordChangedListener {

    private LettersView mLettersView;
    private WordsView mWordsView;
    private ArrayList<String> mState = new ArrayList<>();
    private SharedPreferences sp;
    private EditText mPhrase;
    private boolean mRestoring;
    private Deque<ArrayList<String>> mHistory = new ArrayDeque<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("private-shared-prefs", Activity.MODE_PRIVATE);
        mWordsView = ((WordsView) findViewById(R.id.words_view));
        mWordsView.setOnWordChangedListener(this);
        mLettersView = (LettersView) findViewById(R.id.letters_view);

        mPhrase = (EditText) findViewById(R.id.phrase);
        mPhrase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mLettersView.updatePhrase(new Data(editable.toString()));
                sp.edit().putString("phrase", editable.toString()).apply();
            }
        });

        mPhrase.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mPhrase.setBackgroundColor(Color.DKGRAY);
                } else {
                    mPhrase.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        //Si el intent viene con author, lo seteamos y tiramos todo
        //Si no, restoreamos sharedpreferences
        if (getIntent().hasExtra("title"))
            setTitleAuthor(getIntent().getStringExtra("title"));
        if (getIntent().hasExtra("phrase"))
            setPhrase(getIntent().getStringExtra("phrase"));
        setupToolbar();
    }

    private void setupToolbar() {
        findViewById(R.id.go_to_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });
    }

    private String mChosenFile;
    private String[] mFileList;

    private void load() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/crip");
        dir.mkdirs();
        if (dir.exists())
            mFileList = dir.list();
        if (mFileList == null) return;
        new AlertDialog.Builder(this)
        .setTitle("Choose your file")
        .setItems(mFileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mChosenFile = mFileList[which];
                //you can do stuff with the file here too
            }
        })
        .show();
    }

    private void save() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/crip");
        dir.mkdirs();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        String filename = new Date(System.currentTimeMillis()).toString()
                + "_" + c.get(Calendar.HOUR)
                + ":" + c.get(Calendar.MINUTE)
                + ":" + c.get(Calendar.SECOND);
        File file = new File(dir, filename + ".txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(mPhrase.getText().toString());
            for (String s : mState)
                pw.println(s);
            pw.flush();
            pw.close();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "File written to " + file, Toast.LENGTH_SHORT).show();

    }

    private void setTitleAuthor(String s) {
        sp.edit().clear().apply();
        s = PhraseActivity.toAlpha(s);

        for (int i = 0; i< s.length(); i++)
            mState.add("");
        mWordsView.setTitleAuthor(s);
        mLettersView.setTotalWords(s.length());
    }

    private void setPhrase(String s) {
        mPhrase.setText(s);
        mPhrase.setSelection(s.length());
    }

    @Override
    public void onBackPressed() {
        if (mHistory.size() == 1 && mHistory.getFirst().equals(mState)) return;
        ArrayList<String> backup = mHistory.getFirst();
        if (mState.equals(backup)) {
            mHistory.removeFirst();
            backup = mHistory.getFirst();
        }
        mState = backup;
        mRestoring = true;
        for (int i = 0; i < mState.size(); i++) {
            mWordsView.setWord(i, mState.get(i));
        }
        mRestoring = false;
        mLettersView.update(new Data(mState));
    }

    @Override
    public void onWordChanged(int index, String newWord) {
        mState.set(index, newWord);
        if (mRestoring) return;
        mLettersView.update(new Data(mState));
    }

    @Override
    public void onWordUnfocused(int index) {
        mHistory.push((ArrayList<String>) mState.clone());
        if (mHistory.size() > 10) {
            mHistory.removeLast();
        }
        sp.edit().putString(Integer.toString(index), mState.get(index)).apply();
    }

    public void onWordAdded(){
        mState.add("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mState.isEmpty()) return;
        mRestoring = true;
        int i;
        for (i = 0; true; i++) {
            if (!sp.contains(Integer.toString(i))) break;
            else mWordsView.setWord(i, sp.getString(Integer.toString(i), ""));
        }
        mRestoring = false;
        mLettersView.update(new Data(mState));
        setPhrase(sp.getString("phrase", ""));
        mLettersView.setTotalWords(i);
        onWordUnfocused(0);
    }
}
