package com.example.karamchand.criptogramador;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CellView extends LinearLayout {
    private CellView mTwin;
    private EditText mInput;

    public CellView(Context context) {
        super(context);
        init();
    }

    public CellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CellView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.cell_view, this);
        mInput = ((EditText) findViewById(R.id.cell_view_input));
        mInput.setAllCaps(true);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (mTwin != null && !mTwin.getInput().equals(editable.toString())) {
                    mTwin.setInput(editable.toString());
                }
            }
        });
    }

    public CellView with(char c, int i) {
        ((TextView) findViewById(R.id.cell_view_letter)).setText(Character.toString(c));
        ((TextView) findViewById(R.id.cell_view_number)).setText(Integer.toString(i));
        setBackground(getResources().getDrawable(R.drawable.stroke));
        return this;
    }

    public View black() {
        mInput.setVisibility(GONE);
        setBackgroundColor(Color.BLACK);
        return this;
    }

    public void setTwin(CellView other) {
        if (mTwin == null) {
            mTwin = other;
            other.setTwin(this);
        }
    }

    public String getInput() {
        return mInput.getText().toString();
    }

    public void setInput(String input) {
        mInput.setText(input);
    }
}
