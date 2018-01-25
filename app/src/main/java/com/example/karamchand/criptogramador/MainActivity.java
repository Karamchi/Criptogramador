package com.example.karamchand.criptogramador;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements WordsView.OnWordChangedListener {

    private LettersView mLettersView;
    private WordsView mWordsView;
    private ArrayList<String> mState = new ArrayList<>();
    private SharedPreferences sp;
    private EditText mPhrase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("private-shared-prefs", Activity.MODE_PRIVATE);
        mWordsView = ((WordsView) findViewById(R.id.words_view));
        mWordsView.setOnWordChangedListener(this);
        mLettersView = (LettersView) findViewById(R.id.letters_view);

        mPhrase = (EditText) findViewById(R.id.phrase);
        mPhrase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

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
    }

    private void setTitleAuthor(String s) {
        sp.edit().clear().apply();
        s = s.toLowerCase().replaceAll("[^a-z]", "");
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
    public void onWordChanged(int index, String newWord) {
        mState.set(index, newWord);
        mLettersView.update(new Data(mState));
        sp.edit().putString(Integer.toString(index), newWord).apply();
    }

    public void onWordAdded(){
        mState.add("");
        mLettersView.update(new Data(mState));
    }

    public class Data {
        private HashMap<Character, Integer> counter;
        private int sum;
        private int validSum;
        private int vocales;
        private int validWords;

        public Data(ArrayList<String> state) {
            counter = new HashMap<>();
            for (String s : state) {
                processString(s);
                if (s.length() > 1) {
                    validWords++;
                    validSum += s.length();
                }
            }
        }

        public Data(String s) {
            counter = new HashMap<>();
            processString(s);
        }

        private void processString(String s) {
            for (char c : s.toCharArray()) {
                if (counter.keySet().contains(c))
                    counter.put(c, counter.get(c) + 1);
                else
                    counter.put(c, 1);
                if (LettersView.VOCALES.contains(Character.toString(c)))
                    vocales++;
                sum++;
            }
        }

        public int getCount(char c) {
            if (counter.containsKey(c))
                return counter.get(c);
            else
                return 0;
        }

        public int getCount() {
            return sum;
        }

        public int getVowels() {
            return vocales;
        }

        public int getValidWords() {
            return validWords;
        }

        public int getValidSum() {
            return validSum;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; true; i++) {
            if (!sp.contains(Integer.toString(i))) break;
            else mWordsView.setWord(i, sp.getString(Integer.toString(i), ""));
        }
        setPhrase(sp.getString("phrase", ""));
    }
}
