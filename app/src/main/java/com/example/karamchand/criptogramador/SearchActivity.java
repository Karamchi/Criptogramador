package com.example.karamchand.criptogramador;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import android.os.Handler;

public class SearchActivity extends AppCompatActivity {

    public static final int STARTS = 0;
    public static final int HAS_ANY = 1;
    public static final int HAS_ALL = 2;
    public static final int HAS_NONE = 3;
    public static final int LETTERS = 4;
    public static final int CONSONANTS = 5;

    public ArrayList<String> mCorpus = new ArrayList<>();
    private RecyclerView mRecycler;
    private SearchAdapter adapter;
    private ArrayList<String> mAlphaCorpus = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        read();
        adapter = new SearchAdapter(mCorpus, mAlphaCorpus);
        mRecycler.setAdapter(adapter);
        ((EditText) findViewById(R.id.search_starts)).addTextChangedListener(getTextWatcher(STARTS));
        ((EditText) findViewById(R.id.search_has_all_of)).addTextChangedListener(getTextWatcher(HAS_ALL));
        ((EditText) findViewById(R.id.search_has_any_of)).addTextChangedListener(getTextWatcher(HAS_ANY));
        ((EditText) findViewById(R.id.search_has_none_of)).addTextChangedListener(getTextWatcher(HAS_NONE));
        ((EditText) findViewById(R.id.search_letters)).addTextChangedListener(getTextWatcher(LETTERS));
        ((EditText) findViewById(R.id.search_consonants)).addTextChangedListener(getTextWatcher(CONSONANTS));
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

    public void read() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("Dict.txt"), "UTF-8"));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                mCorpus.add(mLine);
            }
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("DictAlpha.txt"), "UTF-8"));
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

}
