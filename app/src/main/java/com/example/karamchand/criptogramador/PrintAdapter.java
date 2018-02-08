package com.example.karamchand.criptogramador;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PrintAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private ArrayList<ArrayList<CellData>> mDataset = new ArrayList<>();
    private CellView.CellListener mCellListener;
    private int mLettersLength;

    private HashMap<Integer, Character> mAdapterInput;
    private HashMap<Integer, CellView> mAdapterCells;

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
        ((PrintViewholder) holder).setItem(mDataset.get(position));
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

    public void add(ArrayList<CellData> mLastRow) {
        mDataset.add(mLastRow);
    }

    public PrintAdapter withlistener(CellView.CellListener l) {
        mCellListener = l;
        return this;
    }

    public void setLettersLength(int length) {
        mLettersLength = length;
    }

    private class PrintViewholder extends RecyclerView.ViewHolder {
        private final LinearLayout mLayout;
        private TextView textView;

        public PrintViewholder(View v) {
            super(v);
            mLayout = (LinearLayout) itemView.findViewById(R.id.solve_word_view_layout);
            textView = (TextView) itemView.findViewById(R.id.solve_word_view_letter);
            textView.setVisibility(View.GONE);
        }

        public void setItem(ArrayList<CellData> cellDatas) {
            mLayout.removeAllViews();
            mLayout.addView(textView);
            CellView lastAdded = null;
            for (CellData cellData : cellDatas) {
                CellView view;
                if (cellData.number == 0)
                    view = new CellView(mContext).black();
                else
                    view = new CellView(mContext).with(cellData.letter, cellData.number)
                            .withListener(mCellListener);
                view.setPunctuation(cellData.punctuation);
                if (lastAdded != null) {
                    view.setPrevious(lastAdded);
                    lastAdded.setNext(view);
                }
                lastAdded = view;
                mLayout.addView(view);
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
