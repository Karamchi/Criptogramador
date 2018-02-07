package com.example.karamchand.criptogramador;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SolveWordView extends LinearLayout {

    private ArrayList<CellView> mChildren = new ArrayList<>();
    private TextView mDefinition;
    private DefinitionShownListener mListener;

    public SolveWordView(Context context) {
        super(context);
        init();
    }

    public SolveWordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SolveWordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.solve_word_view, this);
    }

    public SolveWordView withLetter(char c) {
        TextView letter = ((TextView) findViewById(R.id.solve_word_view_letter));
        letter.setVisibility(VISIBLE);
        letter.setText(Character.toString(c));
        letter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleShowDefinition();
            }
        });
        return this;
    }

    public void setNumbers(ArrayList<Integer> word) {
        for (int i : word) {
            CellView v = new CellView(getContext()).with(' ', i);
            mChildren.add(v);
            addView(v);
        }
    }

    public CellView getViewAt(int i) {
        return mChildren.get(i);
    }

    @Override
    public void addView(View child) {
        ((LinearLayout) findViewById(R.id.solve_word_view_layout)).addView(child);
        if (child instanceof CellView)
            mChildren.add((CellView) child);
    }

    public void toggleShowDefinition() {
        if (mDefinition == null) return;
        if (mDefinition.getVisibility() == GONE) {
            mDefinition.setVisibility(VISIBLE);
            if (mListener != null) mListener.onDefinitionShown(this);
        } else {
            mDefinition.setVisibility(GONE);
        }
    }

    public void setDefinition(String s) {
        mDefinition = findViewById(R.id.solve_word_view_definition);
        mDefinition.setText(s);
    }

    public SolveWordView withListener(DefinitionShownListener l) {
        mListener = l;
        return this;
    }

    public interface DefinitionShownListener {
        void onDefinitionShown(SolveWordView view);
    }
}
