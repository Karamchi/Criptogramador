package com.example.karamchand.criptogramador;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.karamchand.criptogramador.main.LettersView.ALPHABET;

public class PrintActivity extends AppCompatActivity implements CellView.CellListener,
        SolveWordView.DefinitionShownListener {

    private static final String PATH = "/finished";
    private static final int ROW_WIDTH = 10;
    private String mTitle;

    //The numbers that correspond to word i char j
    private ArrayList<ArrayList<Integer>> mLettersState = new ArrayList<>();
    private ArrayList<String> mDefinitions = new ArrayList<>();
    //The letter and number corresponding to cell i in the phrase (with spaces)
    private ArrayList<Character> mCellLetters = new ArrayList<>();
    private ArrayList<Integer> mCellNumbers = new ArrayList<>();
    //For every cell on the phrase, the input
    private HashMap<Integer, Character> mInput = new HashMap<>();
    //For every cell on the phrase with punctuation, the char for its number
    private HashMap<Integer, Character> mPunctuation = new HashMap<>();

    //The cell view *on the letters* with this number
    private HashMap<Integer, CellView> mCells;

    private LinearLayout mLayout;
    private SolveWordView mLastRow;
    private CellView mLastAdded;
    private EditText mEditText;
    private CellView mCurrentInput;
    private boolean mFromUser;
    private int mSolution;
    private Timer mTimer = new Timer();
    private int mTime;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) mIntent = getIntent();
        setContentView(R.layout.activity_print);
        mLayout = (LinearLayout) findViewById(R.id.activity_print_layout);
        mEditText = ((EditText) findViewById(R.id.print_activity_edit_text));
        if (mIntent == null) {
            String[] current = ProfileUtils.getProfile().get("current").split(":");
            load(FileUtils.readFromFile(RootActivity.PATH, "temp.txt"), current[0]);
            mTime = Integer.parseInt(current[1]);
        } else if (mIntent.hasExtra("words")) {
            Generator g = new Generator(mIntent.getStringExtra("phrase"),
                    (ArrayList<String>) mIntent.getExtras().get("words"));
            g.generate();
            mLettersState = g.mLettersState;
            mCellLetters = g.mCellLetters;
            mCellNumbers = g.mCellNumbers;
            mPunctuation = g.mPunctuation;
            mTitle = g.mFileId;
            mSolution = g.mSolution;
            findViewById(R.id.save).setVisibility(View.VISIBLE);
            restoreFromState();
        } else {
            load(FileUtils.readFromFile(RootActivity.PATH, mIntent.getStringExtra("filename")),
                    mIntent.getStringExtra("title"));
        }
        setupToolbar();
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mFromUser) {
                    mFromUser = true;
                    return;
                }
                boolean b = mEditText.getSelectionStart() > 1;
                mCurrentInput.setInput(s.toString(), b);
                if (b || s.length() == 0) mEditText.setSelection(Math.min(mEditText.length(), 1));
                checkForSolution();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupToolbar() {
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(true);
            }
        });
        findViewById(R.id.timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mLayout.getChildCount(); i++) {
                    if (mLayout.getChildAt(i) instanceof SolveWordView)
                        ((SolveWordView) mLayout.getChildAt(i)).hideDefinition();
                }
            }
        });
        findViewById(R.id.show_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mLayout.getChildCount(); i++) {
                    if (mLayout.getChildAt(i) instanceof SolveWordView)
                        ((SolveWordView) mLayout.getChildAt(i)).showDefinition(false);
                }
            }
        });
    }

    private void restoreFromState() {
        findViewById(R.id.show_all).setVisibility(View.VISIBLE);
        findViewById(R.id.timer).setVisibility(View.VISIBLE);
        mLastAdded = null;
        mEditText.setVisibility(View.GONE);
        mCells = new HashMap<>();
        String fullAlphabet = ALPHABET.toUpperCase() + ALPHABET;
        if (!mCellLetters.contains('Ñ'))
            fullAlphabet = fullAlphabet.replace("Ñ", "");
        for (int word = 0; word < mLettersState.size(); word++) {
            mLastRow = new SolveWordView(this)
                    .withLetter(fullAlphabet.charAt(word))
                    .withListener(this);
            if (mDefinitions.size() > word)
                mLastRow.setDefinition(mDefinitions.get(word));
            for (Integer i : mLettersState.get(word))
                addCell(' ', i);
            mLayout.addView(mLastRow);
        }
        mLastRow = new SolveWordView(this);
//        mLayout.invalidate();
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
        mLastAdded.setNext(mLastAdded);
        mLayout.addView(mLastRow);
        restartTimer();
    }

    private void restartTimer() {
        mTime = 0;
        resumeTimer();
    }

    private void resumeTimer() {
        mTimer.cancel();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.timer)).setText(Integer.toString(mTime));
                    }
                });
                mTime++;
            }
        }, 1000, 1000);
    }

    private void addCell(char c, int i) {
        CellView v = new CellView(this).with(c, i).withListener(this);
        if (mCells.containsKey(i)) {
            v.setTwin(mCells.get(i));
            if (mInput.containsKey(i) && c != ' ')
                v.setInput(Character.toString(mInput.get(i)), false);
        } else {
            mCells.put(i, v);
        }
        if (mPunctuation.containsKey(i) && c != ' ')
            v.setPunctuation(mPunctuation.get(i));
        mLastRow.addView(v);
        v.setPrevious(mLastAdded);
        if (mLastAdded != null) mLastAdded.setNext(v);
        mLastAdded = v;
    }

    private void addBlackCell() {
        mLastRow.addView(new CellView(this).black());
    }

    private void save(boolean showToast) {
        ArrayList<String> content = new ArrayList<>();
        for (ArrayList<Integer> wordState : mLettersState) {
            String word = "";
            for (Integer cell : wordState)
                word += " \t" + Integer.toString(cell) + "\t";
            content.add(word);
            content.add("");
        }
        ArrayList<String> phraseDump = dumpPhrase();
        ArrayList<String> inputDump = dumpInput();
        for (int i = 0; i < phraseDump.size(); i++) {
            content.add(phraseDump.get(i));
            content.add(inputDump.get(i));
        }
        content.add(Integer.toString(mSolution));
        content.addAll(mDefinitions);

        if (showToast) {
            FileUtils.save(this, PATH, mTitle, content);
            Toast.makeText(this, "File written to " + mTitle, Toast.LENGTH_SHORT).show();
        } else {
            FileUtils.save(this, RootActivity.PATH, "temp", content);
        }
    }

    public ArrayList<String> dumpPhrase() {
        ArrayList<String> result = new ArrayList<>();
        String word = "";
        for (int i = 0; i < mCellLetters.size(); i++) {
            word += Character.toString(mCellLetters.get(i)) + '\t';
            if (mCellLetters.get(i) != ' ')
                word += Integer.toString(mCellNumbers.get(i)) + '\t';
            else
                word += " \t";
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
            String input = null;
            if (mCells.containsKey(i))
                input = mCells.get(i).getInput();
            if (input == null || input.equals(""))
                input = " ";
            word += input + '\t';
            if (mPunctuation.containsKey(i))
                word += Character.toString(mPunctuation.get(i)) + '\t';
            else
                word += " \t";
            if ((j + 1) % ROW_WIDTH == 0) {
                result.add(word);
                word = "";
            }
        }
        result.add(word);
        return result;
    }

    private void load(ArrayList<String> contents, String title) {
        mTitle = title;
        ((TextView) findViewById(R.id.title)).setText(mTitle);
        mCellLetters = new ArrayList<>();
        mCellNumbers = new ArrayList<>();
        mLettersState = new ArrayList<>();
        mPunctuation = new HashMap<>();
        mLayout.removeAllViews();
        boolean readingWords = true;
        int i;
        for (i = 0; i < contents.size() - 1; i += 2) {
            String topRow = contents.get(i);
            String bottomRow = contents.get(i + 1);
            if (!topRow.isEmpty() && (ALPHABET.toUpperCase() + ALPHABET)
                    .contains(Character.toString(topRow.charAt(0)))) {
                readingWords = false;
            }
            if (readingWords) {
                ArrayList<Integer> word = new ArrayList<>();
                for (String number : topRow.split("\t")) {
                    number = number.replaceAll("[^0-9]", "");
                    if (!number.equals(""))
                        word.add(Integer.parseInt(number));
                }
                if (word.size() > 0)
                    mLettersState.add(word);
            } else {
                if (!topRow.contains("\t")) break;
                loadPhraseLine(topRow.split("\t"), bottomRow.split("\t"));
            }
        }
        for (; i < contents.size(); i++) {
            if (contents.get(i).replaceAll("[0-9-]", "").length() > 0)
                mDefinitions.add(contents.get(i));
            else
                mSolution = Integer.parseInt(contents.get(i));
        }
        long t = System.currentTimeMillis();
        restoreFromState();
        Log.e("Time to restore", Long.toString(System.currentTimeMillis() - t));
        if (mIntent != null) mTime = mIntent.getIntExtra("time", 0);
    }

    private void loadPhraseLine(String[] topRow, String[] bottomRow) {
        for (int i = 0; i < topRow.length; i += 2) {
            mCellLetters.add(topRow[i].charAt(0));
            int number = 0;
            if (!topRow[i+1].equals(" "))
                number = Integer.parseInt(topRow[i + 1]);
            mCellNumbers.add(number);

            mInput.put(number, string2Char(bottomRow[i]));
            if (!bottomRow[i + 1].equals(" "))
                mPunctuation.put(number, string2Char(bottomRow[i + 1]));
        }
    }

    private char string2Char(String s) {
        if (s.length() > 0) return s.charAt(0);
        return ' ';
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && mEditText.getSelectionStart() == 0 && mEditText.isEnabled()) {
            mCurrentInput.setInput("", false);
            mEditText.setSelection(Math.min(mEditText.length(), 1));
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //Podría evitar esto al restorear pero no cambia el tiempo.
    @Override
    public void onFocusRequested(CellView cellView) {

        if (mCurrentInput != null)
            mCurrentInput.setFocused(false, mEditText);
        else
            ((FrameLayout) mEditText.getParent()).removeView(mEditText);
        cellView.setFocused(true, mEditText);
        mEditText.requestFocus();

        if (mCurrentInput != null) {
            mCurrentInput.setBackground(getDrawable(R.drawable.stroke));
            mCurrentInput.showInput(true);
        }
        cellView.setBackgroundColor(Color.LTGRAY);
        mFromUser = false;
        mEditText.setText(cellView.getInput());
        cellView.showInput(false);
        mCurrentInput = cellView;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CellView && mEditText.isEnabled()) {
            mEditText.setVisibility(View.VISIBLE);
            mEditText.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mEditText, 0);
            ((CellView) v).requestCursor();
        }
    }

    public void checkForSolution() {
        String solution = "";
        for (String line : dumpInput())
            solution += line;
        solution = solution.replaceAll("[^A-ZÑ]", "").toLowerCase();
        if (solution.hashCode() == mSolution) {
            ((TextView) findViewById(R.id.title)).setText(mTitle + " - SOLVED");
            mTimer.cancel();
            if (!ProfileUtils.getProfile().containsKey(mTitle))
                ProfileUtils.putInProfile(this, mTitle, Integer.toString(mTime));
            mEditText.setEnabled(false);
            mEditText.setVisibility(View.GONE);
            if (mCurrentInput != null) {
                mCurrentInput.setBackground(getDrawable(R.drawable.stroke));
                mCurrentInput.showInput(true);
            }
        }
    }

    @Override
    protected void onStop() {
        save(false);
        ProfileUtils.putInProfile(this, "current", mTitle + ":" + Integer.toString(mTime));
        mIntent = null;
        super.onStop();
    }

    @Override
    public void onDefinitionShown(SolveWordView view) {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            if (mLayout.getChildAt(i) instanceof SolveWordView)
                ((SolveWordView) mLayout.getChildAt(i)).hideDefinition();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTimer();
        checkForSolution();
    }
}
