package info.xiancloud.core.util;

import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.log.SystemOutLogger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * scan classpath;<br>
 * Warning: xianframe's log is forbidden here, else you will get a class loading dead lock.
 *
 * @author happyyangyuan
 */
public class TraverseClasspath {

    /**
     * Reflection is not thread-safe scanning the jars files in the classpath,
     * must be synchronized otherwise a "java.lang.IllegalStateException: zip file closed" is thrown.
     */
    synchronized public static <T> Set<Class<? extends T>> getNonAbstractSubClasses(Class<T> parentClass, String... packages) {
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(packages)
                    .setScanners(new SubTypesScanner())
            );
            Set<Class<? extends T>> subClasses = reflections.getSubTypesOf(parentClass);
            Iterator<Class<? extends T>> it = subClasses.iterator();
            while (it.hasNext()) {
                Class subClass = it.next();
                if (!Reflection.canInitiate(subClass)) {
                    //here is in danger of class loading deadlock if you use LOG.java to print the log.
                    SystemOutLogger.singleton.warn(subClass + " can not be initiated, ignored!", null, TraverseClasspath.class.getSimpleName());
                    it.remove();
                }
            }
            return subClasses;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Class<?>> getWithAnnotatedClass(Class<? extends Annotation> annotationClass, String... packages) {
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(packages)
                    .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
            );
            return reflections.getTypesAnnotatedWith(annotationClass);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Set<T> getSubclassInstances(Class<T> clazz, String... packageNames) {
        Set<T> set = new HashSet<>();
        for (Class<? extends T> tClass : getNonAbstractSubClasses(clazz, packageNames)) {
            try {
                set.add(tClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return set;
    }

    /**
     * 获取所有的子类class
     */
    public static <T> Set<Class<? extends T>> getNonAbstractSubclasses(Class<T> clazz) {
        return getNonAbstractSubClasses(clazz, defaultPackages());
    }

    /**
     * Get all subclass initiatable instances.
     */
    public static <T> Set<T> getSubclassInstances(Class<T> clazz) {
        return getSubclassInstances(clazz, defaultPackages());
    }

    private static String[] defaultPackages() {
        String[] packagesToScan = XianConfig.getStringArray("packagesToScan", new String[]{"com.", "info."});
        return ArrayUtil.concat(new String[]{"info.xiancloud"}, packagesToScan);
    }

}
