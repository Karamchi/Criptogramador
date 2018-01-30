package com.example.karamchand.criptogramador;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class LettersView extends LinearLayout {

    public static final String ALPHABET = "abcdefghijklmnñopqrstuvwxyz";
    public static final String VOCALES = "aeiou";
    private HashMap<Character, RowView> mChildren = new HashMap<>();
    private RowView mTotals;
    private RowView mWords;
    private FloatRowView mLetrasXPal;
    private FloatRowView mVocalesxPal;

    public LettersView(Context context) {
        super(context);
        init();
    }

    public LettersView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LettersView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        for (char c : ALPHABET.toCharArray()) {
            RowView newView = new RowView(getContext()).withLetter(c);
            addView(newView);
            mChildren.put(c, newView);
        }
        mTotals = new RowView(getContext()).withLetter(' ');
        mWords = new RowView(getContext()).withLetter(' ');

        mVocalesxPal = new FloatRowView(getContext());
        mLetrasXPal = new FloatRowView(getContext());

        addView(mTotals);
        addView(mWords);
        addView(mLetrasXPal);
        addView(mVocalesxPal);
    }

    public void setTotalWords(int mTotalWords) {
        mLetrasXPal.setTotalDivisor(mTotalWords);
        mWords.resetTotal(mTotalWords);
    }

    public void updatePhrase(Data data) {
        for (char row : mChildren.keySet()) {
            mChildren.get(row).setTotal(data.getCount(row));
        }
        mTotals.setTotal(data.getCount());
        mVocalesxPal.setTotal(data.getVowels());
        mVocalesxPal.setTotalDivisor(data.getCount());
        mLetrasXPal.setTotal(data.getCount());
    }

    public void update(Data data) {
        for (char row : mChildren.keySet()) {
            mChildren.get(row).setAmount(data.getCount(row));
        }
        mTotals.setAmount(data.getCount());
        mWords.setAmount(data.getValidWords());
        mVocalesxPal.setAmount(data.getVowels());
        mVocalesxPal.setDivisor(data.getCount());
        mLetrasXPal.setAmount(data.getValidSum());
        mLetrasXPal.setDivisor(data.getValidWords());
    }
}
