package com.example.karamchand.criptogramador.filters;

public class StartsWithFilter extends AbstractFilter {

    @Override
    public boolean isMoreRestrictiveDelegate(AbstractFilter other) {
        return other instanceof StartsWithFilter && (params.length() < other.params.length()
                || other.params.length() == 0) && !(params.length() == 0);
    }

    @Override
    public boolean isLessRestrictiveDelegate(AbstractFilter other) {
        return other instanceof StartsWithFilter && (params.length() > other.params.length()
                || params.length() == 0) && !(other.params.length() == 0);
    }

    @Override
    public boolean filter(String s) {
        return params.length() == 0 || params.contains(Character.toString(s.charAt(0)));
    }

}
