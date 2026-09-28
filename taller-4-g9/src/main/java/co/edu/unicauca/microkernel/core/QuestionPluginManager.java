package co.edu.unicauca.microkernel.core;

import co.edu.unicauca.microkernel.common.QuestionPlugin;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class QuestionPluginManager {

    private static QuestionPluginManager instance;

    private Map<String, QuestionPlugin> plugins;

    private QuestionPluginManager() {

        plugins = new HashMap<>();

        loadPlugins();
    }

    public static QuestionPluginManager getInstance() {

        if (instance == null) {
            instance = new QuestionPluginManager();
        }

        return instance;
    }

    private void loadPlugins() {

        try {

            Properties properties = new Properties();

            InputStream input =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream("plugins.properties");

            if (input == null) {
                System.out.println(
                        "No se encontró plugins.properties"
                );
                return;
            }

            properties.load(input);

            for (String key : properties.stringPropertyNames()) {

                String className =
                        properties.getProperty(key);

                Class<?> pluginClass =
                        Class.forName(className);

                Object pluginObject =
                        pluginClass
                                .getDeclaredConstructor()
                                .newInstance();

                if (pluginObject instanceof QuestionPlugin) {

                    QuestionPlugin plugin =
                            (QuestionPlugin) pluginObject;

                    plugins.put(
                            plugin.getName(),
                            plugin
                    );

                    System.out.println(
                            "Plugin registrado: "
                            + plugin.getName()
                    );
                }
            }

            input.close();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error cargando plugins",
                    e
            );
        }
    }

    public QuestionPlugin getPlugin(String type) {

        for (QuestionPlugin plugin : plugins.values()) {

            if (plugin.supports(type)) {
                return plugin;
            }
        }

        return null;
    }
}
