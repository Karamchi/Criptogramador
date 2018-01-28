package com.example.karamchand.criptogramador;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class SearchAdapter extends RecyclerView.Adapter {
    private final ArrayList<String> mCorpus;
    private final ArrayList<String> mAlphaCorpus;
    private ArrayList<String> mFilteredCorpus;
    private ArrayList<String> mFilters;

    public SearchAdapter(ArrayList<String> mCorpus, ArrayList<String> mAlphaCorpus) {
        this.mCorpus = mCorpus;
        this.mAlphaCorpus = mAlphaCorpus;
        this.mFilteredCorpus = (ArrayList<String>) this.mCorpus.clone();
        mFilteredCorpus.add(0, Integer.toString(mFilteredCorpus.size()));
        mFilters = new ArrayList<>();
        for (int i = 0; i < 6; i++) mFilters.add("");
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
            if (fitsfilters(mAlphaCorpus.get(i), mFilters)) {
                mFilteredCorpus.add(mCorpus.get(i));
            }
        }
        mFilteredCorpus.add(0, Integer.toString(mFilteredCorpus.size()));
        notifyDataSetChanged();
    }

    private boolean fitsfilters(String string, ArrayList<String> mFilters) {
//        string = PhraseActivity.toAlpha(string);
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
        if (!(consonants.length() == 0) &&
                !(Integer.parseInt(consonants) <= string.replaceAll("a|e|i|o|u", "").length()))
            return false;
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
            ((TextView)view.findViewById(R.id.view_holder)).setText(s);
        }
    }
}
