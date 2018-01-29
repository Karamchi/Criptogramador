package com.example.karamchand.criptogramador.filters;

public class HasAllFilter extends AbstractFilter {

    @Override
    public boolean isMoreRestrictiveDelegate(AbstractFilter other) {
        return other instanceof HasAllFilter && params.length() > other.params.length();
    }

    @Override
    public boolean isLessRestrictiveDelegate(AbstractFilter other) {
        return other instanceof HasAllFilter && params.length() < other.params.length();
    }

    @Override
    public boolean filter(String s) {
        return hasAllOf(s.substring(1), params);
    }

    private boolean hasAllOf(String substring, String filterAllOf) {
        for (char c : filterAllOf.toCharArray())
            if (!substring.contains(Character.toString(c))) return false;
        return true;
    }
}
