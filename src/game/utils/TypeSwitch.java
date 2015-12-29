package game.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Typed switch case statement, case methods should be named "of" use cases:
 *
 * match(value, new Case() {
 *     void of(String value) { do something with string }
 *     void of(Integer value) { do something with integer }
 * });
 *
 * String result = map(value, new CaseAndReturn<String>() {
 *     String of(Integer value) { return "int"; }
 * });
 */
public class TypeSwitch {

    public static class Mapping<T> {
        public final Class<T> type;
        public final Consumer<T> function;

        public Mapping(Class<T> type, Consumer<T> function) {
            this.type = type;
            this.function = function;
        }
    }

    public static <T> Mapping<T> of(Class<T> type, Consumer<T> function) {
        return new Mapping<>(type, function);
    }

    public static Mapping<Object> other(Consumer<Object> function) {
        return of(Object.class, function);
    }

    @SuppressWarnings("unchecked")
    public static void match(Object value, Mapping<?>... mappings) {
        Class<?> type = value == null ? Object.class : value.getClass();
        for (Mapping mapping: mappings) {
            if (mapping.type.isAssignableFrom(type)) {
                mapping.function.accept(value);
                break;
            }
        }
    }
}
