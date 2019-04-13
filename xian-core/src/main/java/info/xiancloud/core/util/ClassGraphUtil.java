package info.xiancloud.core.util;

import info.xiancloud.core.Constant;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.log.SystemOutLogger;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Classpath scanner using ClassGraph library.
 *
 * @author happyyangyuan
 */
public class ClassGraphUtil {

    /**
     * Reflection is not thread-safe scanning the jars files in the classpath,
     * must be synchronized otherwise a "java.lang.IllegalStateException: zip file closed" is thrown.
     */
    synchronized public static <T> Set<ClassInfo> getNonAbstractSubClassInfoSet(Class<T> parentClass, String... packageNames) {
        if (packageNames == null || packageNames.length == 0) {
            packageNames = defaultPackages();
        }
        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableAllInfo()
                             .whitelistPackages(packageNames)
                             .scan()) {
            ClassInfoList classInfos = parentClass.isInterface() ?
                    scanResult.getClassesImplementing(parentClass.getName()) :
                    scanResult.getSubclasses(parentClass.getName());
            return classInfos
                    .stream()
                    .filter(classInfo -> !classInfo.isAbstract())
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Scan classpath for all concrete subclasses of specified interface or class
     *
     * @return all concrete subclasses of specified interface or class
     */
    @SuppressWarnings("all")
    synchronized public static <T> Set<Class<? extends T>> getNonAbstractSubClasses(Class<T> parentClass, String... packageNames) {
        return getNonAbstractSubClassInfoSet(parentClass, packageNames).stream().map(classInfo -> {
            try {
                //Here we must use parentClass's classloader to make sure we are always using the same classloader.
                return (Class<? extends T>) parentClass.getClassLoader().loadClass(classInfo.getName());
            } catch (ClassNotFoundException e) {
                SystemOutLogger.SINGLETON.error("Failed to load class " + classInfo.getName(), e, "");
                System.exit(Constant.SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR);
                //dead code
                return null;
            }
        }).collect(Collectors.toSet());
    }

    public synchronized static ClassInfoList getWithAnnotatedClassInfoList(Class<? extends Annotation> annotationClass, String... packageNames) {
        if (packageNames == null || packageNames.length == 0) {
            packageNames = defaultPackages();
        }
        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableAllInfo()
                             .whitelistPackages(packageNames)
                             .scan()) {
            return scanResult.getClassesWithAnnotation(annotationClass.getName());
        }
    }

    public synchronized static Set<Class<?>> getWithAnnotatedClass(Class<? extends Annotation> annotationClass, String... packageNames) {
        return getWithAnnotatedClassInfoList(annotationClass, packageNames).stream().map(classInfo -> {
            try {
                //Here we must use parentClass's classloader to make sure we are always using the same classloader.
                return annotationClass.getClassLoader().loadClass(classInfo.getName());
            } catch (ClassNotFoundException e) {
                SystemOutLogger.SINGLETON.error("Failed to load class " + classInfo.getName(), e, "");
                System.exit(Constant.SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR);
                //dead code
                return null;
            }
        }).collect(Collectors.toSet());
    }

    /**
     * search and create new instances of concrete subclasses, 1 for each.
     * Note that we only initiate subclasses which are with default constructor, those without default constructors
     * we judge them as canInitiate=false.
     *
     * @param parentClass  parent class
     * @param packageNames package name prefix
     * @param <T>          class generic type
     * @return a set of subclass instances
     */
    public synchronized static <T> Set<T> getSubclassInstances(Class<T> parentClass, String... packageNames) {
        return getNonAbstractSubClasses(parentClass, packageNames).stream()
                .filter(subclass -> {
                    if (Reflection.canInitiate(subclass)) {
                        return true;
                    } else {
                        System.out.println(subclass + " can not be initiated, ignored.");
                        return false;
                    }
                })
                .map(subclass -> {
                    try {
                        return subclass.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toSet());
    }

    private static String[] defaultPackages() {
        String[] packagesToScan = XianConfig.getStringArray("packagesToScan", new String[]{""});
        return ArrayUtil.concatV2(String.class, new String[]{"info.xiancloud"}, packagesToScan);
    }
}

