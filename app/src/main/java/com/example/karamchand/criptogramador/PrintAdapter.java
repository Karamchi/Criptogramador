package com.example.karamchand.criptogramador;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PrintAdapter extends RecyclerView.Adapter {

    private static final int ROW_WIDTH = 10;

    private final Context mContext;
    private ArrayList<ArrayList<CellData>> mDataset = new ArrayList<>();
    private CellView.CellListener mCellListener;
    private int mLettersLength;

    public HashMap<Integer, String> mAdapterInput = new HashMap<>();
    private CellView mCurrentInput;
    private EditText mEditText;

    public PrintAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.solve_word_view, parent, false);
        return new PrintViewholder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PrintViewholder vh = (PrintViewholder) holder;
        if (position < mLettersLength)
            vh.setLetter((LettersView.ALPHABET.toUpperCase() + LettersView.ALPHABET).charAt(position));
        else
            vh.setLetter(null);
        long t = System.currentTimeMillis();
        ((PrintViewholder) holder).setItem(mDataset.get(position));
        Log.e("Time to set", Long.toString(System.currentTimeMillis() - t));
        if (position > 0) {
            vh.setPrevious(mDataset.get(position - 1));
        }
        if (position < mDataset.size() - 1) {
            vh.setNext(mDataset.get(position + 1));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public PrintAdapter withlistener(CellView.CellListener l) {
        mCellListener = l;
        return this;
    }

    public void setLettersLength(int length) {
        mLettersLength = length;
    }

    public void setInput(String input, boolean overwriteNext) {

        if (mCurrentInput == null) return;

        if (input == null) return;
        input = input.toUpperCase().replace(" ", "");
        if (input.length() == 0) {
            mAdapterInput.put(mCurrentInput.mNumber, input);
            updateItem(mCurrentInput.mNumber);
            if (mCurrentInput.mPrevious != null)
                mCurrentInput.mPrevious.requestCursor();
            updateItem(mCurrentInput.mNumber);
        } else {
            mAdapterInput.put(mCurrentInput.mNumber, input.substring(0, 1));
            updateItem(mCurrentInput.mNumber);
            if (mCurrentInput.mNext != null) {
                mCurrentInput.mNext.requestCursor();
                if (input.length() == 2 && overwriteNext)
                    setInput(input.substring(1, 2), false);
            }
        }
    }

    public void updateItem(int mCurrentInput) {
        for (int i = 0; i<mDataset.size(); i++) {
            for (CellData c : mDataset.get(i)) {
                if (c.number == mCurrentInput) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void setCurrentInput(CellView currentInput) {
        int placeholder = 0;
        if (mCurrentInput != null)
            placeholder = mCurrentInput.mNumber;
        mCurrentInput = new CellView(currentInput);
        updateItem(mCurrentInput.mNumber);
        updateItem(placeholder);
    }

    public void setLettersState(ArrayList<ArrayList<Integer>> mLettersState) {
        for (ArrayList<Integer> word : mLettersState) {
            ArrayList<CellData> mLastRow = new ArrayList<>();
            for (Integer i : word)
                mLastRow.add(new CellData(' ', i));
            mDataset.add(mLastRow);
        }
    }

    public void setPhrase(ArrayList<Character> mCellLetters, ArrayList<Integer> mCellNumbers,
                          HashMap<Integer, String> mInput, HashMap<Integer, Character> mPunctuation) {
        this.mAdapterInput = mInput;
        ArrayList<CellData> mLastRow = new ArrayList<>();
        for (int i = 0; i < mCellLetters.size(); i++) {
            if (mCellLetters.get(i) == ' ')
                mLastRow.add(new CellData());
            else {
                int j = mCellNumbers.get(i);
                mCellLetters.get(i);
                mLastRow.add(new CellData(mCellLetters.get(i), j, mPunctuation.get(j)));
            }
            if ((i + 1) % ROW_WIDTH == 0) {
                mDataset.add(mLastRow);
                mLastRow = new ArrayList<>();
            }
        }
        mDataset.add(mLastRow);
    }

    public void setEditText(EditText editText) {
        mEditText = editText;
    }

    private class PrintViewholder extends RecyclerView.ViewHolder {
        private final LinearLayout mLayout;
        private final ArrayList<CellView> mChildren = new ArrayList<>();
        private TextView textView;

        public PrintViewholder(View v) {
            super(v);
            mLayout = (LinearLayout) itemView.findViewById(R.id.solve_word_view_layout);
            textView = (TextView) itemView.findViewById(R.id.solve_word_view_letter);
            textView.setVisibility(View.GONE);
        }

        public void setItem(ArrayList<CellData> cellDatas) {
            CellView lastAdded = null;
            int i;
            for (i = 0; i < cellDatas.size(); i++) {
                CellData cellData = cellDatas.get(i);
                CellView view;
                if (mChildren.size() <= i) {
                    view = new CellView(mContext);
                    mLayout.addView(view);
                    mChildren.add(view);
                } else {
                    view = mChildren.get(i);
                    view.setVisibility(View.VISIBLE);
                }

                if (cellData.number == 0)
                    view.setBlack(true);
                else {
                    view.setBlack(false);
                    view.setLetterNumber(cellData.letter, cellData.number);
                    view.setListener(mCellListener);
                    view.setPunctuation(cellData.punctuation);
                    view.setInput(mAdapterInput.get(cellData.number));
                    if (lastAdded != null) {
                        view.setPrevious(lastAdded);
                        lastAdded.setNext(view);
                    }
                    lastAdded = view;

                    if (mCurrentInput != null && mCurrentInput.mNumber == view.mNumber) {
                        view.showInput(false);
                        view.setBackgroundColor(Color.DKGRAY);
                        view.setFocused(mEditText);
                    } else {
                        view.showInput(true);
                        view.setBackground(R.drawable.stroke);
                    }

                }
            }
            for (; i<mChildren.size(); i++) {
                mChildren.get(i).setVisibility(View.GONE);
            }
        }

        public void setNext(ArrayList<CellData> cellDatas) {

        }

        public void setPrevious(ArrayList<CellData> cellDatas) {

        }

        public void setLetter(Character c) {
            if (c == null) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText(c.toString());
            }

        }
    }
}
