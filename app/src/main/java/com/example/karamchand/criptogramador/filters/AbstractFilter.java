package com.example.karamchand.criptogramador.filters;

public abstract class AbstractFilter {
    protected String params;

    public AbstractFilter with(String mParams) {
        params = mParams;
        return this;
    }

    public boolean isMoreRestrictive(AbstractFilter other) {
        if (isEmpty()) return false;
        if (other.isEmpty()) return true;
        return isMoreRestrictiveDelegate(other);
    }

    public boolean isLessRestrictive(AbstractFilter other) {
        if (isEmpty()) return true;
        if (other.isEmpty()) return false;
        return isLessRestrictiveDelegate(other);
    }

    protected abstract boolean isMoreRestrictiveDelegate(AbstractFilter other);
    protected abstract boolean isLessRestrictiveDelegate(AbstractFilter other);

    public abstract boolean filter(String s);

    public boolean isEmpty() {
        return params == null || params.length() == 0;
    };
}
