package com.example.karamchand.criptogramador.filters;

import java.util.HashMap;

public class ConsonantsFilter extends AbstractFilter {

    private final HashMap<String, Integer> mConsonants;
    private Integer mNumberParam;

    public ConsonantsFilter(HashMap<String, Integer> consonants) {
        mConsonants = consonants;
    }

    @Override
    public AbstractFilter with(String mParams) {
        try {
            mNumberParam = Integer.parseInt(mParams);
        } catch (Exception e) {}
        return super.with(mParams);
    }

    @Override
    public boolean isMoreRestrictiveDelegate(AbstractFilter other) {
        return other instanceof ConsonantsFilter
                && mNumberParam != null && ((ConsonantsFilter) other).mNumberParam == null;
    }

    @Override
    public boolean isLessRestrictiveDelegate(AbstractFilter other) {
        return other instanceof ConsonantsFilter
                && mNumberParam == null && ((ConsonantsFilter) other).mNumberParam != null;
    }

    @Override
    public boolean filter(String s) {
        return mNumberParam == null
                || (mConsonants.containsKey(s) && mConsonants.get(s) <= mNumberParam)
                || mNumberParam <= consonants(s);
    }

    private int consonants(String s) {
        return s.substring(1).replaceAll("a|e|i|o|u", "").length();
    }

}
