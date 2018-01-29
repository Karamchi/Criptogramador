package com.example.karamchand.criptogramador.filters;

public class ConsonantsFilter extends AbstractFilter {

    private Integer mNumberParam;

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
        return mNumberParam == null ||
                mNumberParam <= consonants(s);
    }

    private int consonants(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (!"aeiou".contains(Character.toString(c))) {
                count++;
            }
        }
        return count;
    }

}
