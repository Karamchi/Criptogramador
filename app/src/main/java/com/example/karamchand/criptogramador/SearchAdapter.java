package com.example.karamchand.criptogramador;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Process;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.karamchand.criptogramador.filters.AbstractFilter;
import com.example.karamchand.criptogramador.filters.ConsonantsFilter;
import com.example.karamchand.criptogramador.filters.HasAllFilter;
import com.example.karamchand.criptogramador.filters.HasAnyFilter;
import com.example.karamchand.criptogramador.filters.HasNoneFilter;
import com.example.karamchand.criptogramador.filters.LettersFilter;
import com.example.karamchand.criptogramador.filters.StartsWithFilter;

import java.util.ArrayList;
import java.util.HashSet;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;

public class SearchAdapter extends RecyclerView.Adapter {
    private final ArrayList<String> mCorpus;
    private final ArrayList<String> mAlphaCorpus;
    private final SearchListener mListener;
    private ArrayList<String> mFilteredCorpus;
    private ArrayList<Integer> mConsonants = new ArrayList<>();
    private ArrayList<String> mFilteredAlphaCorpus;
    private ArrayList<AbstractFilter> mFilters;
    private UpdateAllTask mRunningTask;

    public SearchAdapter(ArrayList<String> mCorpus, ArrayList<String> mAlphaCorpus, SearchListener l) {
        this.mCorpus = mCorpus;
        this.mAlphaCorpus = mAlphaCorpus;
        mFilteredCorpus = mCorpus;
        mFilteredAlphaCorpus = mAlphaCorpus;
        mListener = l;
        l.onDataSearched(mCorpus.size());
        mFilters = new ArrayList<>();
        for (int i = 0; i < 6; i++) mFilters.add(new StartsWithFilter().with(""));
        new ConsonantsTask().execute();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view_holder, parent, false);
        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SearchViewHolder) holder).setItem(mFilteredCorpus.get(position));
    }

    @Override
    public int getItemCount() {
        return mFilteredCorpus.size();
    }

    public void filter(String s, int filterType) {
        AbstractFilter filter = null;
        switch (filterType) {
            case SearchActivity.STARTS:
                filter = new StartsWithFilter().with(s);
                break;
            case SearchActivity.HAS_ALL:
                filter = new HasAllFilter().with(s);
                break;
            case SearchActivity.HAS_ANY:
                filter = new HasAnyFilter().with(s);
                break;
            case SearchActivity.HAS_NONE:
                filter = new HasNoneFilter().with(s);
                break;
            case SearchActivity.LETTERS:
                filter = new LettersFilter().with(s);
                break;
            case SearchActivity.CONSONANTS:
                filter = new ConsonantsFilter().with(s);
        }
        AbstractFilter oldFilter = mFilters.get(filterType);
        mFilters.set(filterType, filter);

        if (allEmpty()) {
            mFilteredAlphaCorpus = mAlphaCorpus;
            mFilteredCorpus = mCorpus;
            mListener.onDataSearched(mCorpus.size());
            notifyDataSetChanged();
            return;
        }
        if (filter.isMoreRestrictive(oldFilter)) {
            updateMoreRestrictive(filter);
        } else if (filter.isLessRestrictive(oldFilter)) {
            updateLessRestrictive(filter);
        } else {
            updateAll();
        }

    }

    private boolean allEmpty() {
        for (AbstractFilter f : mFilters)
            if (!f.isEmpty()) return false;
        return true;
    }

    private void updateLessRestrictive(AbstractFilter filter) {
        updateAll();
    }

    private void updateMoreRestrictive(AbstractFilter filter) {
        ArrayList<String> newFilter = new ArrayList<>();
        ArrayList<String> newFilterAlpha = new ArrayList<>();
        for (int i = 0; i < mFilteredCorpus.size(); i++) {
            if (filter.filter(mFilteredAlphaCorpus.get(i))) {
                newFilter.add(mFilteredCorpus.get(i));
                newFilterAlpha.add(mFilteredAlphaCorpus.get(i));
            }
        }
        mFilteredCorpus = newFilter;
        mFilteredAlphaCorpus = newFilterAlpha;
        notifyDataSetChanged();
        mListener.onDataSearched(mFilteredCorpus.size());
    }

    private void updateAll() {
        if (mRunningTask != null) mRunningTask.cancel(true);
        mRunningTask = new UpdateAllTask();
        mRunningTask.execute();
    }

    private boolean fitsfilters(String string) {
        for (AbstractFilter f : mFilters)
            if (!f.filter(string)) return false;
        return true;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

        private final View view;

        public SearchViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

        public void setItem(String s) {
            ((TextView) view.findViewById(R.id.view_holder)).setText(s);
        }
    }

    public class ConsonantsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            for (int i = 0; i < mAlphaCorpus.size(); i++) {
                mConsonants.add(mAlphaCorpus.get(i).substring(1).replaceAll("a|e|i|o|u", "").length());
            }
            return null;
        }
    }

    public class UpdateAllTask extends AsyncTask<String, Void, String> {

        private ArrayList<String> newFilter;
        private ArrayList<String> newFilterAlpha;

        @Override
        protected String doInBackground(String... strings) {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
            newFilter = new ArrayList<>();
            newFilterAlpha = new ArrayList<>();
            for (int i = 0; i < mCorpus.size(); i++) {
                if (fitsfilters(mAlphaCorpus.get(i))) {
                    newFilter.add(mCorpus.get(i));
                    newFilterAlpha.add(mAlphaCorpus.get(i));
                }
            }
            return null;
        }

        @Override

        protected void onPostExecute(String s) {
            notifyDataSetChanged();
            mFilteredCorpus = newFilter;
            mFilteredAlphaCorpus = newFilterAlpha;
            mListener.onDataSearched(mFilteredCorpus.size());
        }
    }

    public interface SearchListener {
        void onDataSearched(int size);
    }
}
