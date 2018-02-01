package com.example.karamchand.criptogramador;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.karamchand.criptogramador.LettersView.ALPHABET;

public class PrintActivity extends AppCompatActivity implements FileUtils.LoadListener {

    private static final String PATH = "/finished";
    private String mPhrase;
    private ArrayList<String> mWords;
    private HashMap<Character, ArrayList<Integer>> mIndexes;
    private HashMap<Integer, Character> mLettersForPhrasePos;

    private ArrayList<ArrayList<Integer>> mLettersState = new ArrayList<>();
    private ArrayList<Character> mPhraseState = new ArrayList<>();
    private ArrayList<Integer> mCellNumbers = new ArrayList<>();

    private String mPhraseAlpha;
    private LinearLayout mLayout;
    private LinearLayout mLastRow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        mPhrase = PhraseActivity.toAlpha(getIntent().getStringExtra("phrase").toLowerCase(), true);
        mPhraseAlpha = mPhrase.replace(" ", "");
        mWords = (ArrayList<String>) getIntent().getExtras().get("words");
        mLayout = (LinearLayout) findViewById(R.id.activity_print_layout);

        generate();
        restoreFromState();
        setupToolbar();
    }

    private void setupToolbar() {
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        findViewById(R.id.load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });
    }

    public void generate() {
        mIndexes = new HashMap<>();
        mLettersForPhrasePos = new HashMap<>();
        for (int i = 0; i < mWords.size(); i++)
            mLettersState.add(new ArrayList<Integer>());

        for (int i = 0; i < mPhraseAlpha.length(); i++) {
            char c = mPhraseAlpha.charAt(i);
            if (mIndexes.containsKey(c)) {
                ArrayList<Integer> oldArray = mIndexes.get(c);
                oldArray.add(i);
                Collections.shuffle(oldArray);
                mIndexes.put(c, oldArray);
            } else {
                ArrayList<Integer> array = new ArrayList<>();
                array.add(i);
                mIndexes.put(c, array);
            }
        }

        mLastRow = new LinearLayout(this);
        for (int i = 0; i<mWords.size(); i++) {
            String word = mWords.get(i);
            for (char c : word.toCharArray()) {
                int index = mIndexes.get(c).remove(0);
                mLettersState.get(i).add(index);
                mLettersForPhrasePos.put(index, ALPHABET.toUpperCase().charAt(mWords.indexOf(word)));
            }
        }

        int letterPosition = 0;
        for (int i = 0; i < mPhrase.length(); i++) {
            if (mPhrase.charAt(i) == ' ') {
                mPhraseState.add(' ');
            } else {
                mPhraseState.add(mLettersForPhrasePos.get(letterPosition));
                letterPosition++;
            }
            mCellNumbers.add(letterPosition);
        }

    }

    private void restoreFromState() {
        mLastRow = new LinearLayout(this);
        for (ArrayList<Integer> word : mLettersState) {
            for (Integer i : word) {
                addCell(' ', i);
            }
            addRow();
        }
        for (int i = 0; i<mPhraseState.size(); i++) {
            if (mPhraseState.get(i) == ' ')
                addBlackCell();
            else
                addCell(mPhraseState.get(i), mCellNumbers.get(i));
            if (i % 20 == 19)
                addRow();
        }
    }

    private void addRow() {
        mLayout.addView(mLastRow);
        mLastRow = new LinearLayout(this);
    }

    private void addCell(char c, int i) {
        mLastRow.addView(new CellView(this).with(c, i));
    }

    private void addBlackCell() {
        mLastRow.addView(new CellView(this).black());
    }

    private void save() {
        ArrayList<String> content = new ArrayList<>();
        for (ArrayList<Integer> wordState : mLettersState) {
            String word = "";
            for (Integer cell : wordState)
                word += " \t" + Integer.toString(cell) + "\t";
            content.add(word);
            content.add("");
        }
        String word = "";
        for (int i = 0; i < mPhraseState.size(); i++) {
            word += Character.toString(mPhraseState.get(i)) + '\t';
            if (mPhraseState.get(i) != ' ') {
                word += Integer.toString(mCellNumbers.get(i)) + '\t';
            } else {
                word += " \t";
            }
            if ((i + 1) % 20 == 0) {
                content.add(word);
                content.add("");
                word = "";
            }
        }
        String filename = FileUtils.saveWithTimeStamp(
                mPhrase.subSequence(0, Math.min(8, mPhrase.length())).toString().replace(" ", "_"),
                PATH,
                content);
        Toast.makeText(this, "File written to " + filename, Toast.LENGTH_SHORT).show();
    }

    private void load() {
        FileUtils.load(this, this, PATH);
    }

    @Override
    public void onLoad(ArrayList<String> contents) {
        mPhraseState = new ArrayList<>();
        mLettersState = new ArrayList<>();
        mLayout.removeAllViews();
        boolean readingWords = true;
        for (String s : contents) {
            if (!s.isEmpty() && ALPHABET.toUpperCase().contains(Character.toString(s.charAt(0)))) {
                readingWords = false;
            }
            if (readingWords) {
                ArrayList<Integer> word = new ArrayList<>();
                for (String number : s.split("\t")) {
                    number = number.replaceAll("[^0-9]", "");
                    if (!number.equals(""))
                        word.add(Integer.parseInt(number));
                }
                mLettersState.add(word);
            } else {
                boolean odd = true;
                for (char c : s.replace("\t", "").toCharArray()) {
                    if (odd)
                        mPhraseState.add(c);
                    odd = !odd;
                }
            }
        }
        restoreFromState();
    }
}
