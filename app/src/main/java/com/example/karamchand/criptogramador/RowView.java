package com.example.karamchand.criptogramador;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RowView extends LinearLayout {

    protected int mAmount = 0;
    protected int mTotal = 0;
    private char mLetter;

    public RowView(Context context) {
        super(context);
        init();
    }

    public RowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.row_view, this);
    }

    public void update() {
        ((TextView) findViewById(R.id.amount)).setText(Integer.toString(mAmount));
        ((TextView) findViewById(R.id.total)).setText(Integer.toString(mTotal));
        ((TextView) findViewById(R.id.percentage)).setText(
                String.format("%d%%",
                (int) ((mAmount / ((float) mTotal)) * 100)));
        ((TextView) findViewById(R.id.remaining)).setText(Integer.toString(mTotal - mAmount));

    }

    public RowView withLetter(char c) {
        mLetter = c;
        ((TextView) findViewById(R.id.letter)).setText(Character.toString(mLetter));
        return this;
    }

    public void resetTotal(int mTotalWords) {
        mTotal = mTotalWords;
        update();
    }

    public void setAmount(int amount) {
        mAmount = amount;
        update();
    }

    public void setTotal(int total) {
        mTotal = total;
        update();
    }
}
