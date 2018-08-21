package info.xiancloud.core.util;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.log.SystemOutLogger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * scan classpath;<br>
 * Warning:
 * <p>
 * Here is in danger of class loading deadlock if you use xian framework's {@link LOG} or {@link SystemOutLogger subclass loggers} to print the log.
 *
 * @author happyyangyuan
 */
public class TraverseClasspath {

    /**
     * Reflection is not thread-safe scanning the jars files in the classpath,
     * must be synchronized otherwise a "java.lang.IllegalStateException: zip file closed" is thrown.
     */
    synchronized public static <T> Set<Class<? extends T>> getNonAbstractSubClasses(Class<T> parentClass, String... packages) {
        if (packages == null || packages.length == 0) {
            packages = defaultPackages();
        }
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(packages)
                    .setScanners(new SubTypesScanner())
            );
            Set<Class<? extends T>> subClasses = reflections.getSubTypesOf(parentClass);
            Iterator<Class<? extends T>> it = subClasses.iterator();
            while (it.hasNext()) {
                Class subClass = it.next();
                if (Modifier.isAbstract(subClass.getModifiers())) {
                    System.out.println(subClass + " is abstract class, ignored!");
                    it.remove();
                }
            }
            return subClasses;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static Set<Class<?>> getWithAnnotatedClass(Class<? extends Annotation> annotationClass, String... packageNames) {
        if (packageNames == null || packageNames.length == 0) {
            packageNames = defaultPackages();
        }
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(packageNames)
                    .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
            );
            return reflections.getTypesAnnotatedWith(annotationClass);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * search and create new instances of concrete subclasses, 1 for each.
     * Note that we only initiate subclasses which are with default constructor, those without default constructors
     * we judge them as canInitiate=false.
     *
     * @param parentClass        parent class
     * @param packageNames package name prefix
     * @param <T>          class generic type
     * @return a set of subclass instances
     */
    public synchronized static <T> Set<T> getSubclassInstances(Class<T> parentClass, String... packageNames) {
        if (packageNames == null || packageNames.length == 0) {
            packageNames = defaultPackages();
        }
        Set<T> set = new HashSet<>();
        for (Class<? extends T> subclass : getNonAbstractSubClasses(parentClass, packageNames)) {
            if (Reflection.canInitiate(subclass)) {
                try {
                    set.add(subclass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(subclass + " can not be initiated, ignored.");
            }
        }
        return set;
    }

    private static String[] defaultPackages() {
        String[] packagesToScan = XianConfig.getStringArray("packagesToScan", new String[]{"com."});
        return ArrayUtil.concat(new String[]{"info.xiancloud."}, packagesToScan);
    }

}
