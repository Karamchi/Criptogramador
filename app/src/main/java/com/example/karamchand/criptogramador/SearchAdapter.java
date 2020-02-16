package com.example.karamchand.criptogramador;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter {
    private ArrayList<String> mCorpus;
    private ArrayList<String> mAlphaCorpus;
    private ArrayList<String> mFilteredCorpus;
    private ArrayList<String> mFilters;
    private ArrayList<Integer> mConsonants = new ArrayList<>();

    public SearchAdapter(ArrayList<String> mCorpus, final ArrayList<String> mAlphaCorpus) {
        this.mCorpus = mCorpus;
        this.mAlphaCorpus = mAlphaCorpus;
        mFilteredCorpus = (ArrayList<String>) this.mCorpus.clone();
        mFilteredCorpus.add(0, Integer.toString(mFilteredCorpus.size()));
        mFilters = new ArrayList<>();
        for (int i = 0; i < 6; i++) mFilters.add("");
        new ConsonantsTask().execute();
    }

    public void changeCorpus(ArrayList<String> corpus, final ArrayList<String> alphaCorpus) {
        this.mCorpus = corpus;
        this.mAlphaCorpus = alphaCorpus;
        mFilteredCorpus = (ArrayList<String>) this.mCorpus.clone();
        mFilteredCorpus.add(0, Integer.toString(mFilteredCorpus.size()));
        new ConsonantsTask().execute();
        updateFilter();
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
        mFilters.set(filterType, s);
        updateFilter();
    }

    private void updateFilter() {
        mFilteredCorpus = new ArrayList<>();
        for (int i = 0; i < mCorpus.size(); i++) {
            if (fitsfilters(i, mFilters)) {
                mFilteredCorpus.add(mCorpus.get(i));
            }
        }
        mFilteredCorpus.add(0, Integer.toString(mFilteredCorpus.size()));
        notifyDataSetChanged();
    }

    private boolean fitsfilters(int stringIndex, ArrayList<String> mFilters) {
        String string = mAlphaCorpus.get(stringIndex);
        String starts = mFilters.get(SearchActivity.STARTS);
        if (!(starts.length() == 0) && !starts.contains(Character.toString(string.charAt(0))))
            return false;
        String substring = string.substring(1);
        if (!hasAllOf(substring, mFilters.get(SearchActivity.HAS_ALL)))
            return false;
        if (!hasNoneOf(substring, mFilters.get(SearchActivity.HAS_NONE)))
            return false;
        String hasAny = mFilters.get(SearchActivity.HAS_ANY);
        if (!(hasAny.length() == 0) && !hasAnyOf(substring, mFilters.get(SearchActivity.HAS_ANY)))
            return false;
        String letters = mFilters.get(SearchActivity.LETTERS);
        if (!(letters.length() == 0) && !(Integer.parseInt(letters) == string.length()))
            return false;
        String consonants = mFilters.get(SearchActivity.CONSONANTS);
        if (mConsonants.size() > stringIndex) {
            if (!(consonants.length() == 0) &&
                    !(Integer.parseInt(consonants) <= mConsonants.get(stringIndex)))
            return false;
        } else {
            if (!(consonants.length() == 0) &&
                    !(Integer.parseInt(consonants) <= substring.replaceAll("[aeiou]", "").length()))
            return false;
        }
        return true;
    }

    private boolean hasAnyOf(String substring, String filterAnyOf) {
        for (char c : filterAnyOf.toCharArray())
            if (substring.contains(Character.toString(c))) return true;
        return false;
    }

    private boolean hasNoneOf(String substring, String filterNoneOf) {
        for (char c : filterNoneOf.toCharArray())
            if (substring.contains(Character.toString(c))) return false;
        return true;
    }

    private boolean hasAllOf(String substring, String filterAllOf) {
        for (char c : filterAllOf.toCharArray())
            if (!substring.contains(Character.toString(c))) return false;
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
                mConsonants.add(mAlphaCorpus.get(i).substring(1).replaceAll("[aeiou]", "").length());
            }
            return null;
        }
    }
}
