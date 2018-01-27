package com.example.karamchand.criptogramador;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class WordsView extends LinearLayout {

    private OnWordChangedListener w;
    private ArrayList<WordView> mChildren = new ArrayList<>();

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
        for (int i = 0; i < titleAuthor.length(); i++) {
            WordView newView = new WordView(getContext());
            newView.addTextChangedListener(getTextWatcher(i));
            newView.setText(Character.toString(titleAuthor.charAt(i)));
            mChildren.add(newView);
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

    public void setWord(int i, String string) {
        try {
            mChildren.get(i).setText(string);
        } catch (IndexOutOfBoundsException e) {
            if ((i < 0) || (i > 30)) return;
            WordView newView = new WordView(getContext());
            newView.addTextChangedListener(getTextWatcher(i));
            mChildren.add(newView);
            addView(newView);
            w.onWordAdded();
            setWord(i, string);
        }
    }

    public interface OnWordChangedListener {
        void onWordChanged(int index, String newWord);

        void onWordAdded();
    }

}
