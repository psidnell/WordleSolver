package com.psidnell;

public class ComponentImpl implements Component {
    private Context context;
    protected Dictionary dictionary;
    protected Solutions solutions;

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        this.dictionary = context.get("dictionary", Dictionary.class);
        this.solutions = context.get("solutions", Solutions.class);
    }
}
