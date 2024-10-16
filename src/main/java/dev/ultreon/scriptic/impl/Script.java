package dev.ultreon.scriptic.impl;

import com.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.scriptic.*;
import dev.ultreon.scriptic.impl.struct.EventStruct;
import dev.ultreon.scriptic.lang.CodeContext;
import dev.ultreon.scriptic.lang.obj.Event;
import dev.ultreon.scriptic.lang.obj.Struct;
import dev.ultreon.scriptic.lang.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Script {
    private final Path filePath;
    private final List<EventStruct> compiledEvents = new ArrayList<>();
    private final List<Struct<?>> compiledStructs = new ArrayList<>();
    private final Map<String, Object> rootProperties;

    private static final Map<Path, Script> scripts = new HashMap<>();
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<Identifier, Object> runtimeVars = new HashMap<>();

    private static Logger logger = new StdOutLogger();
    private boolean compiled;
    private @NotNull String[] args;

    private Script(Path filePath, ScriptEngine engine) throws IOException {
        this(filePath, engine, new String[0]);
    }

    private Script(Path filePath, ScriptEngine engine, @NotNull String[] args) throws IOException {
        this.filePath = filePath;
        this.args = args;

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
        rootProperties.put("script", this);

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

    public Object get(String name) {
        return variables.get(name);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> T cast(String name, T... typeGetter) {
        return (T) typeGetter.getClass().getComponentType().cast(get(name));
    }

    public void set(String name, Object value) {
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
        importFileStream(fileStream, engine, new String[0]);
    }

    private static void importFileStream(Stream<Path> fileStream, ScriptEngine engine, String[] args) throws ScriptException, CompileException {
        ScriptException e = null;
        for (var file : Objects.requireNonNull(fileStream.toArray(Path[]::new))) {
            try {
                importFromPath(file, engine, args);
            } catch (IOException ioException) {
                if (e == null) e = new ScriptException("Script errors occurred");

                e.addSuppressed(ioException);
            }
        }

        if (e != null)
            throw e;
    }

    private static @Nullable Script importFromPath(Path path, ScriptEngine engine) throws IOException, ScriptException, CompileException {
        return importFromPath(path, engine, new String[0]);
    }

    private static @Nullable Script importFromPath(Path path, ScriptEngine engine, String[] args) throws IOException, ScriptException, CompileException {
        if (Files.isRegularFile(path) && (path.getFileName().endsWith(".sc") || path.getFileName().endsWith(".txt"))) {
            if (Files.isExecutable(path)) {
                ScripticLang.getLogger().debug("Importing script: %s".formatted(path.toString()));
                try {
                    Script script = new Script(path, engine, args);
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

    static Script importScript(Path path, ScriptEngine engine, String[] args) throws IOException, CompileException {
        Script script = new Script(path, engine, args);
        script.compile();
        return script;
    }

    static Script importScript(String path, ScriptEngine engine, String[] args) throws IOException, CompileException {
        return importScript(Path.of(path), engine, args);
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

        while (!parser.isEOF()) {
            int row = parser.row();
            var compiled = Registries.compileStruct(row, parser);
            if (compiled == null) {
                continue;
            }
            if (compiled instanceof EventStruct) {
                compiledEvents.add((EventStruct) compiled);
            } else {
                compiledStructs.add(compiled);
            }
        }

        this.compiled = true;
    }

    public void invoke() throws IOException, ScriptException, CompileException {
        if (!compiled) {
            throw new ScriptException("Script hasn't been compiled yet!");
        }
        invokeEventInternal(LangEvents.SCRIPT_INIT, rootProperties);
    }

    public Path getFilePath() {
        return filePath;
    }

    /**
     * Invokes the event with the given name and properties.
     *
     * @param event      The name of the event to invoke.
     * @param properties The properties to pass to the event.
     */
    public static void invokeEvent(Event event, Map<String, Object> properties) {
        for (var script : scripts.values()) {
            try {
                script.invokeEventInternal(event, properties);
            } catch (ScriptException e) {
                logger.error("Failed to invoke event " + event.getClass().getName() + " in script " + script.getFilePath(), e);
            }
        }
    }

    private void invokeEventInternal(Event event, Map<String, Object> properties) throws ScriptException {
        for (EventStruct evt : compiledEvents) {
            if (evt.getEventType() == event) {
                evt.invoke(CodeContext.of(evt, event, properties, args));
            }
        }
    }

    private boolean isParent(Path child, Path parent) {
        if (child.equals(parent)) {
            return true;
        }
        return child.getParent() != null && isParent(child.getParent(), parent);
    }
}
