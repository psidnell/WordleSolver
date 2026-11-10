package com.psidnell.util;

import com.psidnell.Settings;
import com.psidnell.Wordle;
import com.psidnell.solution.Strategy;
import com.psidnell.words.Dictionary;

public class ComponentImpl implements Component {
    protected Dictionary dictionary;
    protected Context context;
    protected Wordle wordle;
    protected Strategy strategy;
    protected Settings settings;

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        this.dictionary = context.get("dictionary", Dictionary.class);
        this.wordle = context.get("wordle", Wordle.class);
        this.strategy = context.get("strategy", Strategy.class);
        this.settings = context.get("settings", Settings.class);
    }
}
