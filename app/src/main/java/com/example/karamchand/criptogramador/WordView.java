package com.example.karamchand.criptogramador;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

class WordView extends LinearLayout {

    private EditText mEditText;

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
                setText(Character.toString(mEditText.getText().charAt(0)));
            }
        });
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mEditText.setBackgroundColor(Color.DKGRAY);
                } else {
                    mEditText.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        mEditText.addTextChangedListener(textWatcher);
    }

    public void setText(String s) {
        mEditText.setText(s);
    }
}
