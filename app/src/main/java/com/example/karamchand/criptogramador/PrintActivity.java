package com.example.karamchand.criptogramador;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.karamchand.criptogramador.LettersView.ALPHABET;

public class PrintActivity extends AppCompatActivity implements FileUtils.LoadListener, CellView.CellListener {

    private static final String PATH = "/finished";
    private static final int ROW_WIDTH = 10;
    private String mPhrase;
    private String mFileId;

    private ArrayList<ArrayList<Integer>> mLettersState = new ArrayList<>();
    private ArrayList<Character> mCellLetters = new ArrayList<>();
    private ArrayList<Integer> mCellNumbers = new ArrayList<>();
    private HashMap<Integer, CellView> mCells;
    private HashMap<Integer, Character> mPunctuation = new HashMap<>();

    private LinearLayout mLayout;
    private SolveWordView mLastRow;
    private CellView mLastAdded;
    private EditText mEditText;
    private CellView mCurrentInput;

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
        mEditText = ((EditText) findViewById(R.id.print_activity_edit_text));
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mCurrentInput.setInput(s.toString());
                    mCurrentInput.mTwin.setInput(s.toString());
                    if (mCurrentInput.mNext != null)
                        mCurrentInput.mNext.requestCursor();
                    mEditText.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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
        String phrase = getIntent().getStringExtra("phrase").toLowerCase();
        buildPunctuation(phrase);
        mPhrase = PhraseActivity.toAlpha(phrase, true);
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

    private void buildPunctuation(String phrase) {
        int letterCount = 0;
        for (int i = 0; i < phrase.length(); i++) {
            if ((ALPHABET + "áéíóúû").contains(Character.toString(phrase.charAt(i)))) {
                letterCount++;
            } else if (phrase.charAt(i) != ' '){
                mPunctuation.put(letterCount, phrase.charAt(i));
            }
        }
    }

    private void restoreFromState() {
        mCells = new HashMap<>();
        for (int word = 0; word < mLettersState.size(); word++) {
            mLastRow = new SolveWordView(this)
                    .withLetter((ALPHABET.toUpperCase() + ALPHABET).charAt(word));
            for (Integer i : mLettersState.get(word)) {
                addCell(' ', i + 1);
            }
            mLayout.addView(mLastRow);
        }
        mLastRow = new SolveWordView(this);
        for (int i = 0; i < mCellLetters.size(); i++) {
            if (mCellLetters.get(i) == ' ')
                addBlackCell();
            else
                addCell(mCellLetters.get(i), mCellNumbers.get(i));
            if ((i + 1) % ROW_WIDTH == 0) {
                mLayout.addView(mLastRow);
                mLastRow = new SolveWordView(this);
            }
        }
        mCells.get(1).requestCursor();
        mLayout.addView(mLastRow);
    }

    private void addCell(char c, int i) {
        CellView v = new CellView(this).with(c, i).withListener(this);
        if (mPunctuation.containsKey(i) && c != ' ') {
            v.setPunctuation(mPunctuation.get(i));
        }
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
                word += " \t" + Integer.toString(cell + 1) + "\t";
            content.add(word);
            content.add("");
        }
        ArrayList<String> phraseDump = dumpPhrase();
        ArrayList<String> inputDump = dumpInput();
        for (int i = 0; i<phraseDump.size(); i++) {
            content.add(phraseDump.get(i));
            content.add(inputDump.get(i));
        }

        String filename = FileUtils.saveWithTimeStamp(mFileId, PATH, content);
        Toast.makeText(this, "File written to " + filename, Toast.LENGTH_SHORT).show();
    }

    public ArrayList<String> dumpPhrase() {
        ArrayList<String> result = new ArrayList<>();
        String word = "";
        for (int i = 0; i < mCellLetters.size(); i++) {
            word += Character.toString(mCellLetters.get(i)) + '\t';
            if (mCellLetters.get(i) != ' ') {
                word += Integer.toString(mCellNumbers.get(i)) + '\t';
            } else {
                word += " \t";
            }
            if ((i + 1) % ROW_WIDTH == 0) {
                result.add(word);
                word = "";
            }
        }
        result.add(word);
        return result;
    }

    public ArrayList<String> dumpInput() {
        ArrayList<String> result = new ArrayList<>();
        String word = "";
        for (int j = 0; j < mCellLetters.size(); j++) {
            int i = mCellNumbers.get(j);
            String input = mCells.get(i).getInput();
            if (input == null || input.equals(""))
                input = " ";
            word += input + '\t';
            if (mPunctuation.containsKey(i)) {
                word += Character.toString(mPunctuation.get(i)) + '\t';
            } else {
                word += " \t";
            }
            if ((j + 1) % ROW_WIDTH == 0) {
                result.add(word);
                word = "";
            }
        }
        result.add(word);
        return result;
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
                if (word.size() > 0)
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
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            mCurrentInput.setInput("");
            mCurrentInput.mTwin.setInput("");
            if (mCurrentInput.mPrevious != null)
                mCurrentInput.mPrevious.requestCursor();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFocusRequested(CellView cellView, float x, float y) {
        mEditText.setX(x);
        mEditText.setY(y);

        if (mCurrentInput != null)
            mCurrentInput.setBackground(getDrawable(R.drawable.stroke));
        cellView.setBackgroundColor(Color.LTGRAY);
        mCurrentInput = cellView;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CellView) ((CellView) v).requestCursor();
    }
}
