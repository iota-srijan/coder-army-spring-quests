package com.coderarmy.hibernate.support;

import com.coderarmy.hibernate.repository.StudentRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.fail;

/**
 * Calls repository methods by name, so this project compiles before you declare them.
 * If a method is missing, the test fails with the exact signature to add.
 */
public final class Repo {

    private Repo() {
    }

    public static Method method(String name, int paramCount, String signature) {
        return Arrays.stream(StudentRepository.class.getMethods())
                .filter(m -> m.getName().equals(name) && m.getParameterCount() == paramCount)
                .findFirst()
                .orElseGet(() -> fail("Missing repository method. Declare in StudentRepository:  " + signature));
    }

    @SuppressWarnings("unchecked")
    public static <T> T call(StudentRepository repo, String signature, String name, Object... args) {
        Method m = method(name, args.length, signature);
        try {
            return (T) m.invoke(repo, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException re) throw re;
            throw new IllegalStateException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
