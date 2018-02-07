package com.example.karamchand.criptogramador;

import android.content.Context;
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

import static com.example.karamchand.criptogramador.LettersView.ALPHABET;

public class PrintActivity extends AppCompatActivity implements FileUtils.LoadListener,
        CellView.CellListener, SolveWordView.DefinitionShownListener {

    private static final String PATH = "/finished";
    private static final int ROW_WIDTH = 10;
    private String mFileId;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        mLayout = (LinearLayout) findViewById(R.id.activity_print_layout);
        mEditText = ((EditText) findViewById(R.id.print_activity_edit_text));
        if (getIntent().hasExtra("words")) {
            Generator g = new Generator(getIntent().getStringExtra("phrase"),
                    (ArrayList<String>) getIntent().getExtras().get("words"));
            g.generate();
            mLettersState = g.mLettersState;
            mCellLetters = g.mCellLetters;
            mCellNumbers = g.mCellNumbers;
            mPunctuation = g.mPunctuation;
            mFileId = g.mFileId;
            mSolution = g.mSolution;
            restoreFromState();
        } else {
            load();
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
                if (before == 1 && count == 0 && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
                    return; // Api < 23 va a llamar a onkeydown
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
        findViewById(R.id.load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });
    }

    private void restoreFromState() {
        findViewById(R.id.save).setVisibility(View.VISIBLE);
        mLastAdded = null;
        mEditText.setVisibility(View.GONE);
        mCells = new HashMap<>();
        for (int word = 0; word < mLettersState.size(); word++) {
            mLastRow = new SolveWordView(this)
                    .withLetter((ALPHABET.toUpperCase() + ALPHABET).charAt(word))
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

    private void save(boolean useTimestamp) {
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

        if (useTimestamp) {
            String filename = FileUtils.saveWithTimeStamp(this, mFileId, PATH, content);
            Toast.makeText(this, "File written to " + filename, Toast.LENGTH_SHORT).show();
        } else {
            FileUtils.save(this, PATH, "temp", content);
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

    private void load() {
        FileUtils.load(this, this, PATH);
    }

//    getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this.getPackageName())

    @Override
    public void onLoad(ArrayList<String> contents, String filename) {
        mFileId = filename.substring(0, 8);
        ((TextView) findViewById(R.id.title)).setText(mFileId);
        mCellLetters = new ArrayList<>();
        mLettersState = new ArrayList<>();
        mPunctuation = new HashMap<>();
        mLayout.removeAllViews();
        boolean readingWords = true;
        int i;
        for (i = 0; i < contents.size() - 1; i += 2) {
            String topRow = contents.get(i);
            String bottomRow = contents.get(i + 1);
            if (!topRow.isEmpty() && ALPHABET.toUpperCase().contains(Character.toString(topRow.charAt(0)))) {
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && mEditText.getSelectionStart() == 0) {
            mCurrentInput.setInput("", false);
            mEditText.setSelection(Math.min(mEditText.length(), 1));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //Podría evitar esto al restorear pero no cambia el tiempo.
    @Override
    public void onFocusRequested(CellView cellView, float x, float y) {
        ((FrameLayout) mEditText.getParent()).setX(x);
        ((FrameLayout) mEditText.getParent()).setY(y);

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
        if (v instanceof CellView) {
            mEditText.setEnabled(true);
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
        if (solution.hashCode() == mSolution)
            ((TextView) findViewById(R.id.title)).setText("SOLVED");
        else
            ((TextView) findViewById(R.id.title)).setText(mFileId);
    }

    @Override
    protected void onStop() {
        save(false);
        super.onStop();
    }

    @Override
    public void onDefinitionShown(SolveWordView view) {
        mEditText.setVisibility(View.GONE);
        mEditText.setEnabled(false);
        if (mCurrentInput != null) {
            mCurrentInput.setBackground(getDrawable(R.drawable.stroke));
            mCurrentInput.showInput(true);
        }
//        mEditText.setInputType(InputType.TYPE_NULL);
//        mFromUser = false;
    }
}
