package io.github.xypercode.scriptic.impl;

import com.ultreon.libs.commons.v0.Identifier;
import io.github.xypercode.scriptic.*;
import io.github.xypercode.scriptic.lang.obj.Event;
import io.github.xypercode.scriptic.lang.obj.compiled.CEvent;
import io.github.xypercode.scriptic.lang.obj.compiled.CValue;
import io.github.xypercode.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Script {
    private final Path filePath;
    private final List<CEvent> compiledEvents = new ArrayList<>();
    private final Map<Identifier, Object> rootProperties;

    private static final Map<Path, Script> scripts = new HashMap<>();
    private final Map<String, CValue<?>> variables = new HashMap<>();
    private final Map<Identifier, Object> runtimeVars = new HashMap<>();

    private static Logger logger = new StdOutLogger();
    private boolean compiled;

    private Script(Path filePath, ScriptEngine engine) throws IOException {
        this.filePath = filePath;

        if (!Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Script file doesn't exist, or is symlinked.");
        }

        if (filePath.isAbsolute())
            throw new IOException("File must be relative");
        if (!isParent(filePath, Path.of("scripts")) && !engine.allowsExternalScripts())
            throw new IOException("File must be in the same directory as the script");
        if ("Linux".equalsIgnoreCase(System.getProperty("os.name")) && !Files.isExecutable(filePath))
            throw new IOException("File must be executable");

        rootProperties = new HashMap<>();
        rootProperties.put(ScripticLang.id("script"), this);

        if (scripts.containsKey(filePath)) {
            throw new IllegalStateException("Script already loaded");
        }
        scripts.put(filePath, this);
    }

    public static void setLogger(Logger logger) {
        Script.logger = logger;
    }

    public static Logger getLogger() {
        return logger;
    }

    public CValue<?> get(String name) {
        return variables.get(name);
    }

    @SafeVarargs
    public final <T> CValue<T> cast(String name, T... typeGetter) throws ScriptException {
        return new CValue<>(get(name).cast(typeGetter));
    }

    public void set(String name, CValue<?> value) {
        variables.put(name, value);
    }

    public void setRuntimeVar(Identifier name, Object value) {
        runtimeVars.put(name, value);
    }

    public Object getRuntimeVar(Identifier name) {
        return runtimeVars.get(name);
    }

    public <T> T getRuntimeVar(Identifier name, Class<T> type) {
        return type.cast(runtimeVars.get(name));
    }

    static void importScripts(ScriptEngine engine) throws IOException, ScriptException, CompileException {
        importScripts(Path.of("scripts"), engine);
    }

    static void importScripts(Path folder, ScriptEngine engine) throws IOException, ScriptException, CompileException {
        if (!Files.exists(folder))
            Files.createDirectories(folder);

        if (!Files.isDirectory(folder, LinkOption.NOFOLLOW_LINKS))
            throw new IllegalArgumentException("Path must be a directory");

        try (Stream<Path> fileStream = Files.list(folder)) {
            importFileStream(fileStream, engine);
        }
    }

    private static void importFileStream(Stream<Path> fileStream, ScriptEngine engine) throws ScriptException, CompileException {
        ScriptException e = null;
        for (var file : Objects.requireNonNull(fileStream.toArray(Path[]::new))) {
            try {
                importFromPath(file, engine);
            } catch (IOException ioException) {
                if (e == null) e = new ScriptException("Script errors occurred");

                e.addSuppressed(ioException);
            }
        }

        if (e != null)
            throw e;
    }

    private static @Nullable Script importFromPath(Path path, ScriptEngine engine) throws IOException, ScriptException, CompileException {
        if (Files.isRegularFile(path) && (path.getFileName().endsWith(".sc") || path.getFileName().endsWith(".txt"))) {
            if (Files.isExecutable(path)) {
                ScripticLang.getLogger().debug("Importing script: %s".formatted(path.toString()));
                try {
                    Script script = new Script(path, engine);
                    script.invoke();
                    return script;
                } catch (RuntimeException e) {
                    throw new ScriptException("Runtime error in script " + path + ": " + e.getMessage());
                }
            } else {
                throw new AccessDeniedException("File must be executable");
            }
        }
        return null;
    }

    static Script importScript(Path path, ScriptEngine engine) throws IOException, CompileException {
        Script script = new Script(path, engine);
        script.compile();
        return script;
    }

    static Script importScript(String path, ScriptEngine engine) throws IOException, ScriptException, CompileException {
        return importScript(Path.of(path), engine);
    }

    public static boolean isDefaultProperty(Identifier name) {
        return name.equals(ScripticLang.id("script"));
    }

    void compile() throws IOException, CompileException {
        final var code = Files.readString(filePath)
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        if (compiled) {
            throw new CompileException("Double compile detected!", 1, 1);
        }

        var parser = new Parser(code);

        try {
            while (!parser.isEOF()) {
                var compiled = Registries.compileEvent(parser.row(), parser);
                if (compiled == null) {
                    throw new CompileException("Failed to compile event", parser.row());
                }
                compiledEvents.add(compiled);
            }
        } catch (CompileException e) {
            throw e;
        }

        this.compiled = true;
    }

    public void invoke() throws IOException, ScriptException, CompileException {
        if (!compiled) {
            throw new ScriptException("Script hasn't been compiled yet!");
        }
        invokeEventInternal(LangEvents.SCRIPT_INIT, new HashMap<>());
    }

    private void invokeEventInternal(Event event, Map<Identifier, Object> properties) throws ScriptException {
        invokeEventInternal(event.getRegistryName(), properties);
    }

    public Path getFilePath() {
        return filePath;
    }

    private void invokeEventInternal(Identifier event, Map<Identifier, Object> properties) throws ScriptException {
        this.invokeEventInternal(event, properties, cEvent -> true);
    }

    private void invokeEventInternal(Event event, Map<Identifier, Object> properties, Predicate<CEvent> filter) throws ScriptException {
        invokeEventInternal(event.getRegistryName(), properties, filter);
    }

    private void invokeEventInternal(Identifier event, Map<Identifier, Object> properties, Predicate<CEvent> filter) throws ScriptException {
        for (var property : properties.keySet()) {
            if (rootProperties.containsKey(property)) {
                throw new IllegalArgumentException("Can't override root property: " + property);
            }
        }
        properties.putAll(rootProperties);

        for (var compiledEvent : compiledEvents) {
            if (!filter.test(compiledEvent)) {
                continue;
            }

            var registryName = compiledEvent.getListensTo().getRegistryName();
            if (registryName == null) {
                throw new IllegalStateException("Event has no registry name: " + compiledEvent.getListensTo().getClass().getName());
            }

            if (registryName.equals(event)) {
                compiledEvent.call(compiledEvent.getEffect(), properties);
            }
        }
    }

    /**
     * Invokes the event with the given name and properties.
     *
     * @param event      The name of the event to invoke.
     * @param properties The properties to pass to the event.
     */
    public static void invokeEvent(Identifier event, Map<Identifier, Object> properties) throws ScriptException {
        for (var script : scripts.values()) {
            script.invokeEventInternal(event, properties);
        }
    }

    /**
     * Invokes the event with the given name and properties.
     *
     * @param event      The name of the event to invoke.
     * @param properties The properties to pass to the event.
     */
    public static void invokeEvent(Event event, Map<Identifier, Object> properties) {
        for (var script : scripts.values()) {
            try {
                script.invokeEventInternal(event, properties);
            } catch (ScriptException e) {
                logger.error("Failed to invoke event " + event.getRegistryName() + " in script " + script.getFilePath(), e);
            }
        }
    }

    /**
     * Invokes the event with the given name and properties.
     *
     * @param event      The name of the event to invoke.
     * @param properties The properties to pass to the event.
     */
    public static void updateEvents(Identifier event, Map<Identifier, Object> properties, Predicate<CEvent> filter) throws ScriptException {
        for (var script : scripts.values()) {
            script.invokeEventInternal(event, properties, filter);
        }
    }

    /**
     * Invokes the event with the given name and properties.
     *
     * @param event      The name of the event to invoke.
     * @param properties The properties to pass to the event.
     */
    public static void invokeEvent(Event event, Map<Identifier, Object> properties, Predicate<CEvent> filter) throws ScriptException {
        for (var script : scripts.values()) {
            script.invokeEventInternal(event, properties, filter);
        }
    }

    private boolean isParent(Path child, Path parent) {
        if (child.equals(parent)) {
            return true;
        }
        return child.getParent() != null && isParent(child.getParent(), parent);
    }
}
