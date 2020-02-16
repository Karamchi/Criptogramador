package com.example.karamchand.criptogramador;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    public static final int STARTS = 0;
    public static final int HAS_ANY = 1;
    public static final int HAS_ALL = 2;
    public static final int HAS_NONE = 3;
    public static final int LETTERS = 4;
    public static final int CONSONANTS = 5;

    public ArrayList<String> mCorpus;
    private RecyclerView mRecycler;
    private SearchAdapter adapter;
    private ArrayList<String> mAlphaCorpus;
    private Language language = Language.SPANISH;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mRecycler = findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        read("Dict-es.txt", "DictAlpha-es.txt");
        adapter = new SearchAdapter(mCorpus, mAlphaCorpus);
        mRecycler.setAdapter(adapter);
        ((EditText) findViewById(R.id.search_starts)).addTextChangedListener(getTextWatcher(STARTS));
        ((EditText) findViewById(R.id.search_has_all_of)).addTextChangedListener(getTextWatcher(HAS_ALL));
        ((EditText) findViewById(R.id.search_has_any_of)).addTextChangedListener(getTextWatcher(HAS_ANY));
        ((EditText) findViewById(R.id.search_has_none_of)).addTextChangedListener(getTextWatcher(HAS_NONE));
        ((EditText) findViewById(R.id.search_letters)).addTextChangedListener(getTextWatcher(LETTERS));
        ((EditText) findViewById(R.id.search_consonants)).addTextChangedListener(getTextWatcher(CONSONANTS));
        setFlag();
    }

    private void setFlag() {
        final ImageView flag = findViewById(R.id.search_lang);
        flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (language.equals(Language.SPANISH)) {
                    language = Language.ENGLISH;
                    flag.setImageDrawable(getDrawable(R.drawable.uk));
                    read("Dict-en.txt", "DictAlpha-en.txt");
                } else {
                    language = Language.SPANISH;
                    flag.setImageDrawable(getDrawable(R.drawable.spain));
                    read("Dict-es.txt", "DictAlpha-es.txt");
                }
                adapter.changeCorpus(mCorpus, mAlphaCorpus);
            }
        });
    }

    private TextWatcher getTextWatcher(final int filterType) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.filter(editable.toString(), filterType);
            }
        };
    }

    public void read(String corpusFile, String alphaCorpusFile) {
        mCorpus = new ArrayList<>();
        mAlphaCorpus = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(corpusFile), "UTF-8"));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                mCorpus.add(mLine);
            }
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(alphaCorpusFile), "UTF-8"));
            while ((mLine = reader.readLine()) != null) {
                mAlphaCorpus.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    public enum Language {SPANISH, ENGLISH}
}
