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
import com.example.karamchand.criptogramador.PhraseActivity;
import com.example.karamchand.criptogramador.PrintActivity;
import com.example.karamchand.criptogramador.R;
import com.example.karamchand.criptogramador.SearchActivity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class MainActivity extends AppCompatActivity implements WordsView.OnWordChangedListener, FileUtils.LoadListener {

    private static final String PATH = "/builder";

    private LettersView mLettersView;
    private WordsView mWordsView;
    private ArrayList<String> mState = new ArrayList<>();
    private EditText mPhrase;
    private boolean mRestoring;
    private Deque<ArrayList<String>> mHistory = new ArrayDeque<>();
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) mIntent = getIntent();
        setContentView(R.layout.activity_main);
        mWordsView = findViewById(R.id.words_view);
        mWordsView.setOnWordChangedListener(this);
        mLettersView = findViewById(R.id.letters_view);

        mPhrase = findViewById(R.id.phrase);
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
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
                Intent intent = new Intent(MainActivity.this, DefinitionsActivity.class);
                intent.putExtra("phrase", mPhrase.getText().toString());
                intent.putExtra("words", mState);
                startActivity(intent);
            }
        });
    }

    private void load() {
        FileUtils.load(this, this, PATH);
    }

    @Override
    public void onLoad(ArrayList<String> s, String filename) {
        mState = s;
        setPhrase(mState.remove(0));
        restoreFromState();
        onWordUnfocused(0);
        checkIfFinished();
    }

    private void save() {
        ArrayList<String> content = (ArrayList<String>) mState.clone();
        content.add(0, mPhrase.getText().toString());
        String filename = FileUtils.saveWithTimeStamp(this,
                FileUtils.phrase2Filename(mPhrase.getText().toString()),
                PATH,
                content);
        Toast.makeText(this, "File written to " + filename, Toast.LENGTH_SHORT).show();
    }

    private void save(String filename) {
        ArrayList<String> content = (ArrayList<String>) mState.clone();
        content.add(0, mPhrase.getText().toString());
        FileUtils.save(this, PATH, filename, content);
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
        mWordsView.setWords(mState);
        mRestoring = false;
        mLettersView.update(new Data(mState));
        mLettersView.setTotalWords(mState.size());
    }

    @Override
    public void onWordChanged(int index, String newWord) {
        if (mRestoring) return;
        mState.set(index, newWord);
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
        mIntent = null;
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mState = FileUtils.readFromFile(PATH, "temp.txt");
        if (mState.size() > 0)
            setPhrase(mState.remove(0));
        restoreFromState();
        onWordUnfocused(0);
        if (mIntent == null) return;

        //Si el intent viene con author, lo seteamos y tiramos todo
        //Si no, restoreamos sharedpreferences
        if (mIntent.hasExtra("title"))
            setTitleAuthor(mIntent.getStringExtra("title"));
        if (mIntent.hasExtra("phrase"))
            setPhrase(mIntent.getStringExtra("phrase"));
    }

}
