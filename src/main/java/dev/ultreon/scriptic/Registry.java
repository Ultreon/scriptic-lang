package dev.ultreon.scriptic;

import dev.ultreon.scriptic.lang.LangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Registry<T extends LangObject<T>> {
    private static final Map<Class<?>, Registry<?>> REGISTRIES = new LinkedHashMap<>();
    private static boolean frozen;
    private final Map<String, Supplier<T>> registryMap = new LinkedHashMap<>();
    private final Map<Class<T>, String> classMap = new LinkedHashMap<>();
    private final List<RegistryEntry<? extends T>> patternEntries = new ArrayList<>();
    private final Class<T> type;
    private final Identifier id;

    protected Registry(Class<T> clazz, Identifier id) throws IllegalStateException {
        this.id = id;
        this.type = clazz;
    }

    public static Collection<Registry<?>> getRegistries() {
        return REGISTRIES.values();
    }

    public static void freeze() {
        frozen = true;
    }

    public Identifier id() {
        return this.id;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T extends LangObject<T>> Registry<T> create(Identifier registryName, @NotNull T... type) {
        Class<T> componentType = (Class<T>) type.getClass().getComponentType();
        if (REGISTRIES.containsKey(componentType)) {
            throw new IllegalStateException();
        }

        Registry<T> registry = new Registry<>(componentType, registryName);
        REGISTRIES.put(componentType, registry);

        return registry;
    }

    /**
     * Returns the identifier of the given registered instance.
     *
     * @param obj the registered instance.
     * @return the identifier of it.
     */
    public String getKey(T obj) {
        String s = this.classMap.get(obj.getClass());
        if (s == null) {
            throw new RegistryException("Cannot find object for: " + obj + " | type: " + this.type.getSimpleName());
        }
        return s;
    }

    /**
     * Returns the registered instance from the given {@link Identifier}
     *
     * @param key the namespaced key.
     * @return a registered instance of the type {@link T}.
     * @throws ClassCastException if the type is invalid.
     */
    public T create(@Nullable String key) {
        if (!this.registryMap.containsKey(key)) {
            throw new RegistryException("Cannot find object for: " + key + " | type: " + this.type.getSimpleName());
        }
        return this.registryMap.get(key).get();
    }

    public boolean contains(String rl) {
        return this.registryMap.containsKey(rl);
    }

    /**
     * Register an object.
     *
     * @param pattern the resource location.
     * @param factory the register item value.
     */
    public void register(String pattern, Supplier<T> factory, Class<T> type) {
        if (!this.type.isAssignableFrom(type))
            throw new IllegalArgumentException("Not allowed type detected, got " + factory.getClass() + " expected assignable to " + this.type);
        if (frozen)
            throw new IllegalStateException("Cannot modify registries after they are frozen.");
        if (this.registryMap.containsKey(pattern))
            throw new IllegalStateException("Cannot register " + pattern + " twice.");
        if (pattern == null)
            throw new IllegalArgumentException("Pattern cannot be null.");
        if (factory == null)
            throw new IllegalArgumentException("Factory cannot be null.");

        if (pattern.isEmpty())
            throw new IllegalArgumentException("Pattern cannot be empty.");

        this.registryMap.put(pattern, factory);
        this.classMap.put(type, pattern);
        this.patternEntries.add(new RegistryEntry<T>(Pattern.compile(pattern), factory, type));
    }

    public List<RegistryEntry<? extends T>> getPatternEntries() {
        return this.patternEntries;
    }

    public T matching(int lineNr, String code) throws CompileException {
        for (RegistryEntry<? extends T> entry : this.patternEntries) {
            Matcher matcher = entry.matcher(code);
            if (matcher.matches()) {
                return entry.create(lineNr, matcher);
            }
        }
        return null;
    }

    public Collection<Supplier<T>> values() {
        return Collections.unmodifiableCollection(this.registryMap.values());
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(this.registryMap.keySet());
    }

    public Set<Map.Entry<String, Supplier<T>>> entries() {
        // I do this because the IDE won't accept dynamic values and keys.
        ArrayList<Supplier<T>> values = new ArrayList<>(this.values());
        ArrayList<String> keys = new ArrayList<>(this.keys());

        if (keys.size() != values.size()) throw new IllegalStateException("Keys and values have different lengths.");

        Set<Map.Entry<String, Supplier<T>>> entrySet = new HashSet<>();

        for (int i = 0; i < keys.size(); i++) {
            entrySet.add(new AbstractMap.SimpleEntry<>(keys.get(i), values.get(i)));
        }

        return Collections.unmodifiableSet(entrySet);
    }

    public Class<T> getType() {
        return this.type;
    }

    public boolean isFrozen() {
        return frozen;
    }
}
