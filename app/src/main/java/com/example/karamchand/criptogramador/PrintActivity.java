package com.example.karamchand.criptogramador;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.karamchand.criptogramador.LettersView.ALPHABET;

public class PrintActivity extends AppCompatActivity implements FileUtils.LoadListener {

    private static final String PATH = "/finished";
    private static final int ROW_WIDTH = 10;
    private String mPhrase;
    private String mFileId;

    private ArrayList<ArrayList<Integer>> mLettersState = new ArrayList<>();
    private ArrayList<Character> mCellLetters = new ArrayList<>();
    private ArrayList<Integer> mCellNumbers = new ArrayList<>();
    private HashMap<Integer, CellView> mCells;
    private HashMap<Integer, Character> mPunctuation;

    private LinearLayout mLayout;
    private LinearLayout mLastRow;
    private CellView mLastAdded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        mLayout = (LinearLayout) findViewById(R.id.activity_print_layout);
        if (getIntent().hasExtra("words")) {
            generate();
            restoreFromState();
        } else {
            load();
        }
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
        mPhrase = PhraseActivity.toAlpha(getIntent().getStringExtra("phrase").toLowerCase(), true);
        mFileId = mPhrase.subSequence(0, Math.min(8, mPhrase.length())).toString().replace(" ", "_");
        String mPhraseAlpha = mPhrase.replace(" ", "");
        ArrayList<String> mWords = (ArrayList<String>) getIntent().getExtras().get("words");

        HashMap<Character, ArrayList<Integer>> indexes = new HashMap<>();
        HashMap<Integer, Character> lettersForPhrasePos = new HashMap<>();

        for (int i = 0; i < mWords.size(); i++)
            mLettersState.add(new ArrayList<Integer>());

        for (int i = 0; i < mPhraseAlpha.length(); i++) {
            char c = mPhraseAlpha.charAt(i);
            if (indexes.containsKey(c)) {
                ArrayList<Integer> oldArray = indexes.get(c);
                oldArray.add(i);
                Collections.shuffle(oldArray);
                indexes.put(c, oldArray);
            } else {
                ArrayList<Integer> array = new ArrayList<>();
                array.add(i);
                indexes.put(c, array);
            }
        }

        mLastRow = new LinearLayout(this);
        for (int i = 0; i< mWords.size(); i++) {
            String word = mWords.get(i);
            for (char c : word.toCharArray()) {
                int index = indexes.get(c).remove(0);
                mLettersState.get(i).add(index);
                lettersForPhrasePos.put(index, ALPHABET.toUpperCase().charAt(mWords.indexOf(word)));
            }
        }

        int letterPosition = 0;
        for (int i = 0; i < mPhrase.length(); i++) {
            if (mPhrase.charAt(i) == ' ') {
                mCellLetters.add(' ');
            } else {
                mCellLetters.add(lettersForPhrasePos.get(letterPosition));
                letterPosition++;
            }
            mCellNumbers.add(letterPosition);
        }

    }

    private void restoreFromState() {
        mCells = new HashMap<>();
        mLastRow = new LinearLayout(this);
        for (ArrayList<Integer> word : mLettersState) {
            for (Integer i : word) {
                addCell(' ', i + 1);
            }
            addRow();
        }
        for (int i = 0; i < mCellLetters.size(); i++) {
            if (mCellLetters.get(i) == ' ')
                addBlackCell();
            else
                addCell(mCellLetters.get(i), mCellNumbers.get(i));
            if ((i + 1) % ROW_WIDTH == 0)
                addRow();
        }
        addRow();
    }

    private void addRow() {
        mLayout.addView(mLastRow);
        mLastRow = new LinearLayout(this);
    }

    private void addCell(char c, int i) {
        CellView v = new CellView(this).with(c, i);
        if (mCells.containsKey(i))
            v.setTwin(mCells.get(i));
        else
            mCells.put(i, v);
        mLastRow.addView(v);
        v.setPrevious(mLastAdded);
        if (mLastAdded != null) mLastAdded.setNext(v);
        mLastAdded = v;
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
        for (int i = 0; i < mCellLetters.size(); i++) {
            word += Character.toString(mCellLetters.get(i)) + '\t';
            if (mCellLetters.get(i) != ' ') {
                word += Integer.toString(mCellNumbers.get(i)) + '\t';
            } else {
                word += " \t";
            }
            if ((i + 1) % ROW_WIDTH == 0) {
                content.add(word);
                content.add("");
                word = "";
            }
        }
        content.add(word);
        content.add("");
        String filename = FileUtils.saveWithTimeStamp(mFileId, PATH, content);
        Toast.makeText(this, "File written to " + filename, Toast.LENGTH_SHORT).show();
    }

    private void load() {
        FileUtils.load(this, this, PATH);
    }

    @Override
    public void onLoad(ArrayList<String> contents, String filename) {
        mFileId = filename.substring(0, 8);
        mCellLetters = new ArrayList<>();
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
                for (String numberOrLetter : s.split("\t")) {
                    if (numberOrLetter.equals("")) continue;
                    if (odd)
                        mCellLetters.add(numberOrLetter.charAt(0));
                    else if (!numberOrLetter.equals(" "))
                        mCellNumbers.add(Integer.parseInt(numberOrLetter));
                    else
                        mCellNumbers.add(0);
                    odd = !odd;
                }
            }
        }
        restoreFromState();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View v = getCurrentFocus();
        v.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

}
