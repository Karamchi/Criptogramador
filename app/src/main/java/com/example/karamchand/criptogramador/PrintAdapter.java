package com.example.karamchand.criptogramador;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PrintAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private ArrayList<ArrayList<CellData>> mDataset = new ArrayList<>();

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
        ((PrintViewholder) holder).setItem(mDataset.get(position));
        if (position > 0) {
            ((PrintViewholder) holder).setPrevious(mDataset.get(position - 1));
        }
        if (position < mDataset.size() - 1) {
            ((PrintViewholder) holder).setNext(mDataset.get(position + 1));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void add(ArrayList<CellData> mLastRow) {
        mDataset.add(mLastRow);
    }

    private class PrintViewholder extends RecyclerView.ViewHolder {
        public PrintViewholder(View v) {
            super(v);
        }

        public void setItem(ArrayList<CellData> cellDatas) {
            CellView lastAdded = null;
            for (CellData cellData : cellDatas) {
                CellView view = new CellView(mContext).with(cellData.letter, cellData.number);
                view.setPunctuation(cellData.punctuation);
                if (lastAdded != null) {
                    view.setPrevious(lastAdded);
                    lastAdded.setNext(view);
                }
                lastAdded = view;
                ((LinearLayout) view.findViewById(R.id.solve_word_view_layout)).addView(view);
            }
        }

        public void setNext(ArrayList<CellData> cellDatas) {

        }

        public void setPrevious(ArrayList<CellData> cellDatas) {

        }
    }
}
