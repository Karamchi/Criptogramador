package com.example.karamchand.criptogramador;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CellView extends LinearLayout {
    private TextView mInput;
    private CellView mTwin;
    private CellView mPrevious;
    private CellView mNext;
    private CellListener mListener;

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
        mInput = ((TextView) findViewById(R.id.cell_view_input));
    }

    public CellView with(char c, int i) {
        ((TextView) findViewById(R.id.cell_view_letter)).setText(Character.toString(c));
        ((TextView) findViewById(R.id.cell_view_number)).setText(Integer.toString(i));
        setBackground(getResources().getDrawable(R.drawable.stroke));
        return this;
    }

    public CellView withListener(CellListener listener) {
        mListener = listener;
        setOnClickListener(listener);
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

    public void setInput(String input, boolean overwriteNext) {
        input = input.toUpperCase().replace(" ", "");
        if (input.length() == 0) {
            mInput.setText("");
            mTwin.setInputFromTwin("");
            if (mPrevious != null)
                mPrevious.requestCursor();
        } else {
            mInput.setText(input.substring(0, 1));
            mTwin.setInputFromTwin(input.substring(0, 1));
            if (mNext != null) {
                if (input.length() == 2 && overwriteNext)
                    mNext.setInput(input.substring(1, 2), false);
                mNext.requestCursor();
            }
        }

    }

    public void setInputFromTwin(String input) {
        mInput.setText(input);
    }

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
        mListener.onFocusRequested(this, getAbsoluteX(), getAbsoluteY());
    }

    //Toda esta mierda es necesaria porque desde android 6 NO HAY FORMA de evitar que al focusear
    //una vista adentro de un scrollview del orto el forrito escrolee a 0.
    public float getAbsoluteY() {
        int[] liw = new int[2];
        getLocationOnScreen(liw);
        return liw[1] + ((View) mInput.getParent()).getY();
    }

    public float getAbsoluteX() {
        int[] liw = new int[2];
        getLocationOnScreen(liw);
        return liw[0];
    }

    public String getInput() {
        return mInput.getText().toString();
    }

    public interface CellListener extends OnClickListener {

        void onFocusRequested(CellView cellView, float x, float y);
    }

    public void setPunctuation(char c) {
        ((TextView) findViewById(R.id.cell_view_punctuation)).setText(Character.toString(c));
    }
}
