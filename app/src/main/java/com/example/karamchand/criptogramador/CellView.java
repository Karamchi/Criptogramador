package com.example.karamchand.criptogramador;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CellView extends LinearLayout {
    private CellView mTwin;
    private EditText mInput;
    private CellView mPrevious;
    private CellView mNext;

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
        mInput.addTextChangedListener(new TextWatcher() {
            public int mCursorPosition;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCursorPosition = mInput.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 1) {
                    setInput(editable.subSequence(0, 1).toString());
                    setSelection(1);
                    if (mCursorPosition >= 2 && mNext != null) {
                        mNext.setInput(editable.subSequence(1, 2).toString());
                        mNext.setSelection(1);
                        mNext.requestFocus();
                    }
                    return;
                } if (editable.length() == 1 && !editable.toString().toUpperCase().equals(getInput())) {
                    //Todô este quilombo porque a los forros no se les ocurrió implementar AllCaps
                    // para edittext
                    setInput(editable.toString());
                    setSelection(1);
                    return;
                }
                if (mTwin != null && !mTwin.getInput().equalsIgnoreCase(editable.toString())) {
                    mTwin.setInput(editable.toString());
                }
            }
        });
    }

    private void setSelection(int i) {
        mInput.setSelection(i);
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
        mInput.setText(input.toUpperCase());
    }

    public void setPrevious(CellView previous) {
        mPrevious = previous;
    }

    public void setNext(CellView next) {
        mNext = next;
    }

    //Solo handlea deletes, y no hay forma de darse cuenta si borrasste vacio
    //Es medio imbecil: si es una letra la handlea el edittext, pero si es un delete
    //por mas que la handlee el edittext viene aca como un pelotudo
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL)
            if (mPrevious != null) {
                mPrevious.requestFocus();
                if (!mPrevious.getInput().equals(""))
                    mPrevious.setSelection(1);
            }
        return false;
    }

    @Override
    public boolean isFocused() {
        return mInput.isFocused();
    }
}
