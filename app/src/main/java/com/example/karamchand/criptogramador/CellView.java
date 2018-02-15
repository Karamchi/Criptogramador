package com.example.karamchand.criptogramador;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CellView extends LinearLayout {
    private TextView mInput;
    private CellView mTwin;
    public CellView mPrevious;
    public CellView mNext;
    private CellListener mListener;
    public int mNumber;

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

    public CellView(CellView currentInput) {
        super(currentInput.getContext());
        mNumber = currentInput.mNumber;
        setNext(currentInput.mNext);
        setPrevious(currentInput.mPrevious);
    }

    private void init() {
        inflate(getContext(), R.layout.cell_view, this);
        mInput = ((TextView) findViewById(R.id.cell_view_input));
    }
/*
    public void setInput(String input, boolean overwriteNext) {
        if (input == null) return;
        input = input.toUpperCase().replace(" ", "");
        if (input.length() == 0) {
            mInput.setText("");
//            mTwin.setInputFromTwin("");
            if (mPrevious != null)
                mPrevious.requestCursor();
        } else {
            mInput.setText(input.substring(0, 1));
//            mTwin.setInputFromTwin(input.substring(0, 1));
            if (mNext != null) {
                if (input.length() == 2 && overwriteNext)
                    mNext.setInput(input.substring(1, 2), false);
                mNext.requestCursor();
            }
        }

    }*/

    public void showInput(boolean show) {
        mInput.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    public void setPrevious(CellView previous) {
        mPrevious = previous;
    }

    public void setNext(CellView next) {
        mNext = next;
    }

    public void requestCursor() {
        mListener.onFocusRequested(this);
    }

    public String getInput() {
        return mInput.getText().toString();
    }

    public void setBackground(@DrawableRes int drawable) {
        setBackground(getResources().getDrawable(drawable));
    }

    public void setBlack(boolean b) {
        mInput.setVisibility(b ? GONE : VISIBLE);
        setBackgroundColor(b ? Color.BLACK : Color.WHITE); //Fix
    }

    public void setLetterNumber(char c, int i) {
        mNumber = i;
        ((TextView) findViewById(R.id.cell_view_letter)).setText(Character.toString(c));
        ((TextView) findViewById(R.id.cell_view_number)).setText(Integer.toString(i));
        setBackground(getResources().getDrawable(R.drawable.stroke));
    }

    public void setListener(CellListener listener) {
        mListener = listener;
        setOnClickListener(listener);
    }

    public void setInput(String s) {
        mInput.setText(s);
    }

    public void setFocused(EditText editText) {
        FrameLayout fl = (FrameLayout) mInput.getParent();
        ViewGroup fl2 = (ViewGroup) editText.getParent();
        fl2.removeView(editText);
        fl.addView(editText);
    }

    public interface CellListener extends OnClickListener {

        void onFocusRequested(CellView cellView);
    }

    public void setPunctuation(char c) {
        ((TextView) findViewById(R.id.cell_view_punctuation)).setText(Character.toString(c));
    }
}
