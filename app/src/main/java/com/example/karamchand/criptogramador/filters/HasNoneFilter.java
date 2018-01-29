package com.example.karamchand.criptogramador.filters;

public class HasNoneFilter extends AbstractFilter {

    @Override
    public boolean isMoreRestrictiveDelegate(AbstractFilter other) {
        return other instanceof HasNoneFilter && params.length() > other.params.length();
    }

    @Override
    public boolean isLessRestrictiveDelegate(AbstractFilter other) {
        return other instanceof HasNoneFilter && params.length() < other.params.length();
    }

    @Override
    public boolean filter(String s) {
        return hasNoneOf(s.substring(1), params);
    }

    private boolean hasNoneOf(String substring, String filterNoneOf) {
        for (char c : filterNoneOf.toCharArray())
            if (substring.contains(Character.toString(c))) return false;
        return true;
    }

}
