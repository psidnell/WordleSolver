package com.psidnell;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Solutions extends ComponentImpl {

    private Map<String, Word> solutions;

    @Override
    public void init() {
        super.init();
        this.solutions = new HashMap<>(dictionary.getWords());
    }

    public Collection<Word> getSolutions() {
        return (Collection<Word>) solutions.values();
    }
}
