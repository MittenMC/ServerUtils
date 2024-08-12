package com.github.mittenmc.serverutils.gui.instanced;

import com.github.mittenmc.serverutils.ServerUtils;
import com.github.mittenmc.serverutils.gui.group.GroupedPagesMenu;
import com.github.mittenmc.serverutils.gui.pages.ItemGenerator;
import com.github.mittenmc.serverutils.player.profile.PlayerProfile;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 * A simple factory class which can create a {@link GroupedPagesMenu}
 * @author GavvyDizzle
 * @version 1.1.5
 * @since 1.1.5
 */
public class GroupedPagesMenuFactory {
    public static <V extends PlayerProfile, E extends Enum<E>, I extends Comparable<? super I> & ItemGenerator> GroupedPagesMenu<V, E, I>
    createMenu(Class<? extends GroupedPagesMenu<V, E, I>> menuClass, Object... args) {
        try {
            Constructor<? extends GroupedPagesMenu<V, E, I>> constructor = findConstructor(menuClass, args);
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            ServerUtils.getInstance().getLogger().log(Level.SEVERE, "Unable to instantiate an instance of the menu: " + menuClass.getName(), e);
            return null;
        }
    }

    private static <V extends PlayerProfile, E extends Enum<E>, I extends Comparable<? super I> & ItemGenerator> Constructor<? extends GroupedPagesMenu<V, E, I>>
    findConstructor(Class<? extends GroupedPagesMenu<V, E, I>> menuClass, Object... args) throws NoSuchMethodException {
        for (Constructor<?> constructor : menuClass.getDeclaredConstructors()) {
            if (matchesParameterTypes(constructor.getParameterTypes(), args)) {
                return (Constructor<? extends GroupedPagesMenu<V, E, I>>) constructor;
            }
        }
        throw new NoSuchMethodException("No suitable constructor found for " + menuClass.getName());
    }

    private static boolean matchesParameterTypes(Class<?>[] parameterTypes, Object[] args) {
        if (parameterTypes.length != args.length) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (args[i] != null) {
                Class<?> argClass = args[i].getClass();
                if (!parameterTypes[i].isAssignableFrom(argClass) && !isPrimitiveAssignable(parameterTypes[i], argClass)) {
                    return false;
                }
            } else if (parameterTypes[i].isPrimitive()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPrimitiveAssignable(Class<?> parameterType, Class<?> argClass) {
        if (parameterType.isPrimitive()) {
            if (parameterType == boolean.class && argClass == Boolean.class) return true;
            if (parameterType == byte.class && argClass == Byte.class) return true;
            if (parameterType == char.class && argClass == Character.class) return true;
            if (parameterType == short.class && argClass == Short.class) return true;
            if (parameterType == int.class && argClass == Integer.class) return true;
            if (parameterType == long.class && argClass == Long.class) return true;
            if (parameterType == float.class && argClass == Float.class) return true;
            return parameterType == double.class && argClass == Double.class;
        }
        return false;
    }
}

