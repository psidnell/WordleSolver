package com.psidnell.util;

import com.psidnell.Dictionary;
import com.psidnell.Wordle;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Map<String, Component> components = new HashMap<String, Component>();

    public Context() throws Exception {
        this(new Dictionary(), new Wordle());
    }

    public Context(
            Dictionary dictionary,
            Wordle wordle
    ) throws Exception {

        components.put("dictionary", dictionary);
        components.put("wordle", wordle);

        for (Component component : components.values()) {
            component.setContext(this);
        }

        for (Component component : components.values()) {
            component.init();
        }
    }

    public <T extends Component> T get(String name, Class<T> clazz) {
        //noinspection unchecked
        return (T) components.get(name);
    }
}
