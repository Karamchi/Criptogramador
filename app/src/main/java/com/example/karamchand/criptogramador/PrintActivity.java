package com.example.karamchand.criptogramador;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import static com.example.karamchand.criptogramador.LettersView.ALPHABET;

public class PrintActivity extends AppCompatActivity {

    private static final String PATH = "/finished";
    private String mPhrase;
    private ArrayList<String> mWords;
    private HashMap<Character, ArrayList<Integer>> mIndexes;
    private HashMap<Integer, Character> mLettersForPhrasePos;

    private String lettersBuffer = "";
    private String phrasebuffer = "";
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
    }

    public void generate() {

        mIndexes = new HashMap<>();
        mLettersForPhrasePos = new HashMap<>();

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
        for (String word : mWords) {
            for (char c : word.toCharArray()) {
                int index = mIndexes.get(c).remove(0);
                addCell(' ', index + 1);
                mLettersForPhrasePos.put(index, ALPHABET.toUpperCase().charAt(mWords.indexOf(word)));
            }
            addRow();
        }

        addRow();

        int letterPosition = 0;
        for (int i = 0; i < mPhrase.length(); i++) {
            if (mPhrase.charAt(i) == ' ') {
                addBlackCell();
            } else {
                addCell(mLettersForPhrasePos.get(letterPosition), letterPosition + 1);
                letterPosition++;
            }
            if ((i + 1) % 20 == 0) addRow();
        }

        addRow();

    }

    private void addRow() {
        lettersBuffer += "\n\n";
        mLayout.addView(mLastRow);
        mLastRow = new LinearLayout(this);
    }

    private void addCell(char c, int i) {
        lettersBuffer += c + "\t" + Integer.toString(i) + "\t";
        mLastRow.addView(new CellView(this).with(c, i));
    }

    private void addBlackCell() {
        lettersBuffer += " \t \t";
        mLastRow.addView(new CellView(this).black());
    }

}
