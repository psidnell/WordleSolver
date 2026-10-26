package com.psidnell;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Map<String, Component> components = new HashMap<String, Component>();

    public Context() throws Exception {

        components.put("dictionary", new Dictionary());
        components.put("wordle", new Wordle());
        components.put("solutions", new Solutions());

        components.values().forEach((component) -> {
            component.setContext(this);
        });

        for (Component component : components.values()) {
            component.init();
        }
    }

    public <T extends Component> T get(String name, Class<T> clazz) {
        //noinspection unchecked
        return (T) components.get(name);
    }
}
