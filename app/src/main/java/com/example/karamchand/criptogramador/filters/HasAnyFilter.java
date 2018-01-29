package com.example.karamchand.criptogramador.filters;

public class HasAnyFilter extends AbstractFilter {

    @Override
    public boolean isMoreRestrictiveDelegate(AbstractFilter other) {
        return other instanceof HasAnyFilter && (params.length() < other.params.length()
                || other.params.length() == 0) && !(params.length() == 0);
    }

    @Override
    public boolean isLessRestrictiveDelegate(AbstractFilter other) {
        return other instanceof HasAnyFilter && (params.length() > other.params.length()
                || params.length() == 0) && !(other.params.length() == 0);
    }

    @Override
    public boolean filter(String s) {
        return params.length() == 0 || hasAnyOf(s.substring(1), params);
    }

    private boolean hasAnyOf(String substring, String filterAnyOf) {
        for (char c : filterAnyOf.toCharArray())
            if (substring.contains(Character.toString(c))) return true;
        return false;
    }

}
