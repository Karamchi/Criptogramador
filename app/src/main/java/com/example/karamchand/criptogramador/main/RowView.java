package com.example.karamchand.criptogramador.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.karamchand.criptogramador.R;

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
        int percentage = (int) ((mAmount / ((float) mTotal)) * 100);
        if (mAmount + mTotal == 0) percentage = 100;
        int color = rgb(255 - 255 * (percentage - 90)/10, 255, 0);
        if (percentage > 100) color = rgb(255, 0, 0);
        if (percentage < 90) color = rgb(255, 255, 0);

        TextView percentageView = (TextView) findViewById(R.id.percentage);

        percentageView.setText(String.format("%d%%", percentage));
        percentageView.setBackgroundColor(color);

        int diff = mTotal - mAmount;
        if (diff != 0)
            ((TextView) findViewById(R.id.remaining)).setText(Integer.toString(diff));
        else
            ((TextView) findViewById(R.id.remaining)).setText("");

    }

    private int rgb(int r, int g, int b) {
        return (200 & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
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
