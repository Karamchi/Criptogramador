package com.example.karamchand.criptogramador.filters;

public class LettersFilter extends AbstractFilter {

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
        return other instanceof LettersFilter
                && mNumberParam != null && ((LettersFilter) other).mNumberParam == null;
    }

    @Override
    public boolean isLessRestrictiveDelegate(AbstractFilter other) {
        return other instanceof LettersFilter
                && mNumberParam == null && ((LettersFilter) other).mNumberParam != null;
    }

    @Override
    public boolean filter(String s) {
        return mNumberParam == null || mNumberParam == s.length();
    }

}
