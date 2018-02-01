package com.example.karamchand.criptogramador.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.karamchand.criptogramador.FileUtils;
import com.example.karamchand.criptogramador.LettersView;
import com.example.karamchand.criptogramador.PhraseActivity;
import com.example.karamchand.criptogramador.PrintActivity;
import com.example.karamchand.criptogramador.R;
import com.example.karamchand.criptogramador.SearchActivity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class MainActivity extends AppCompatActivity implements WordsView.OnWordChangedListener, FileUtils.LoadListener {

    private LettersView mLettersView;
    private WordsView mWordsView;
    private ArrayList<String> mState = new ArrayList<>();
    private EditText mPhrase;
    private boolean mRestoring;
    private Deque<ArrayList<String>> mHistory = new ArrayDeque<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                checkIfFinished();
            }
        });

        mPhrase.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) mPhrase.setBackgroundColor(Color.DKGRAY);
                else mPhrase.setBackgroundColor(Color.TRANSPARENT);
            }
        });

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
        findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrintActivity.class);
                intent.putExtra("phrase", mPhrase.getText().toString());
                intent.putExtra("words", mState);
                startActivity(intent);
            }
        });
    }

    private void load() {
        FileUtils.load(this, this, "");
    }

    @Override
    public void onLoad(ArrayList<String> s) {
        mState = s;
        setPhrase(mState.remove(0));
        restoreFromState();
        onWordUnfocused(0);
        checkIfFinished();
    }

    private void save() {
        ArrayList<String> content = (ArrayList<String>) mState.clone();
        content.add(0, mPhrase.getText().toString());
        String filename = FileUtils.saveWithTimeStamp(
                mPhrase.getText().subSequence(0, Math.min(8, mPhrase.length())).toString().replace(" ", "_"),
                "",
                content);
        Toast.makeText(this, "File written to " + filename, Toast.LENGTH_SHORT).show();
    }

    private void save(String filename) {
        ArrayList<String> content = (ArrayList<String>) mState.clone();
        content.add(0, mPhrase.getText().toString());
        FileUtils.save("", filename, content);
    }

    private void setTitleAuthor(String s) {
        s = PhraseActivity.toAlpha(s);

        mState = new ArrayList<>();
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
        restoreFromState();
        Toast.makeText(this, "Undo", Toast.LENGTH_SHORT).show();
    }

    public void restoreFromState() {
        mRestoring = true;
        for (int i = 0; i < mState.size(); i++) {
            mWordsView.setWord(i, mState.get(i));
        }
        mRestoring = false;
        mLettersView.update(new Data(mState));
        mLettersView.setTotalWords(mState.size());
    }

    @Override
    public void onWordChanged(int index, String newWord) {
        mState.set(index, newWord);
        if (mRestoring) return;
        Data data = new Data(mState);
        mLettersView.update(data);
        checkIfFinished();
    }

    private void checkIfFinished() {
        boolean finished = new Data(mState).isFinished(PhraseActivity.toAlpha(mPhrase.getText().toString()));
        findViewById(R.id.finish).setVisibility(finished ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onWordUnfocused(int index) {
        mHistory.push((ArrayList<String>) mState.clone());
        if (mHistory.size() > 10) {
            mHistory.removeLast();
        }
    }

    @Override
    protected void onStop() {
        save("temp");
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mState = FileUtils.readFromFile("", "temp.txt");

        setPhrase(mState.remove(0));
        restoreFromState();

        //Si el intent viene con author, lo seteamos y tiramos todo
        //Si no, restoreamos sharedpreferences
        if (getIntent().hasExtra("title"))
            setTitleAuthor(getIntent().getStringExtra("title"));
        if (getIntent().hasExtra("phrase"))
            setPhrase(getIntent().getStringExtra("phrase"));
    }

}