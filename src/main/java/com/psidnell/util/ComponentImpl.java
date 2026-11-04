package com.psidnell.util;

import com.psidnell.words.Dictionary;

public class ComponentImpl implements Component {
    protected Dictionary dictionary;
    private Context context;

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        this.dictionary = context.get("dictionary", Dictionary.class);
    }
}
