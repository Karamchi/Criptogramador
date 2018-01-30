package com.example.karamchand.criptogramador;

import android.content.Context;
import android.widget.TextView;

public class FloatRowView extends RowView {

    private double mTotalDivisor;
    private double mDivisor;

    public FloatRowView(Context context) {
        super(context);
    }

    @Override
    public void update() {
        ((TextView) findViewById(R.id.amount)).setText(String.format("%.2f", mAmount / mDivisor));
        ((TextView) findViewById(R.id.total)).setText(String.format("%.2f", mTotal / mTotalDivisor));
        double remaining = (mTotal - mAmount) / (mTotalDivisor - mDivisor);
        ((TextView) findViewById(R.id.remaining)).setText(String.format("%.2f", remaining));
    }

    public void setTotalDivisor(int mTotalWords) {
        mTotalDivisor = mTotalWords;
        update();
    }

    public void setDivisor(int count) {
        mDivisor = count;
        update();
    }
}
