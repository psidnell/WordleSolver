package com.psidnell.util;

import com.psidnell.Settings;
import com.psidnell.Wordle;
import com.psidnell.solution.AdaptiveSolutionEvaluator;
import com.psidnell.words.Dictionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Can't be bothered to use Spring
 */
public class Context {

    private final Map<String, Component> components = new HashMap<String, Component>();

    public Context() throws Exception {
        this(new Dictionary(), new AdaptiveSolutionEvaluator(), new Wordle(), Settings.getInstance());
    }

    public Context(
            Dictionary dictionary,
            AdaptiveSolutionEvaluator strategy,
            Wordle wordle,
            Settings settings
    ) throws Exception {

        components.put("dictionary", dictionary);
        components.put("wordle", wordle);
        components.put("strategy", strategy);
        components.put("settings", settings);

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
