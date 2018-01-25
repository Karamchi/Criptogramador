package com.example.karamchand.criptogramador;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import static com.example.karamchand.criptogramador.LettersView.ALPHABET;

public class WordsView extends LinearLayout {

    private OnWordChangedListener w;

    public WordsView(Context context) {
        super(context);
        init();
    }

    public WordsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WordsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    public void setOnWordChangedListener(OnWordChangedListener w) {
        this.w = w;
    }

    public void setTitleAuthor(String titleAuthor) {
        titleAuthor = titleAuthor.toLowerCase().replace(" ", "");
        for (int i = 0; i < titleAuthor.length(); i++) {
            WordView newView = new WordView(getContext());
            newView.addTextChangedListener(getTextWatcher(i));
            newView.setText(Character.toString(titleAuthor.charAt(i)));
            addView(newView);
        }
    }

    private TextWatcher getTextWatcher(final int index) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                w.onWordChanged(index, editable.toString());
            }
        };
    }

    public interface OnWordChangedListener {
        void onWordChanged(int index, String newWord);
    }

}
