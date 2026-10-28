package com.psidnell.util;

import com.psidnell.Dictionary;

public class ComponentImpl implements Component {
    private Context context;
    protected Dictionary dictionary;

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        this.dictionary = context.get("dictionary", Dictionary.class);
    }
}
