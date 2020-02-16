package com.example.karamchand.criptogramador.main;

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

import com.example.karamchand.criptogramador.R;

public class WordView extends LinearLayout {

    private EditText mEditText;
    private String oldText;

    public WordView(Context context) {
        super(context);
        init();
    }

    public WordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.word_view, this);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mEditText.setTextSize(12);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                ((TextView) findViewById(R.id.letter_count)).setText(Integer.toString(editable.toString().length()));
            }
        });
        findViewById(R.id.close).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
            if (mEditText.getText().length() > 1) {
                mEditText.requestFocus();
                setText(Character.toString(mEditText.getText().charAt(0)));
                mEditText.setSelection(1);
            }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (oldText != null) {
            mEditText.setText(oldText);
        }
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener l) {
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    oldText = mEditText.getText().toString();
                    mEditText.setBackgroundColor(Color.DKGRAY);
                } else {
                    mEditText.setBackgroundColor(Color.TRANSPARENT);
                    if (!oldText.equals(mEditText.getText().toString()))
                        l.onFocusChange(WordView.this, b);
                }
            }
        });
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        mEditText.addTextChangedListener(textWatcher);
    }

    public void setText(String s) {
        if (!isAttachedToWindow()) oldText = s;
        else mEditText.setText(s);
    }
}
