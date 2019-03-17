package info.xiancloud.core.util;

import info.xiancloud.core.Unit;
import info.xiancloud.core.conf.XianConfig;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassGraphUtil {
    public static void main(String[] args) {
        getNonAbstractSubClasses(Unit.class).stream().peek(classInfo -> System.out.println(classInfo.getName())).collect(Collectors.toSet());
        getSubclassInstances(Unit.class).stream().peek(unit -> System.out.println(unit.getName())).collect(Collectors.toSet());
    }

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
     * Reflection is not thread-safe scanning the jars files in the classpath,
     * must be synchronized otherwise a "java.lang.IllegalStateException: zip file closed" is thrown.
     */
    synchronized public static <T> Set<Class<? extends T>> getNonAbstractSubClasses(Class<T> parentClass, String... packageNames) {
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
                    .map(classInfo -> classInfo.loadClass(parentClass))
                    .collect(Collectors.toSet());
        }
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
        if (packageNames == null || packageNames.length == 0) {
            packageNames = defaultPackages();
        }
        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableAllInfo()
                             .whitelistPackages(packageNames)
                             .scan()) {
            return scanResult.getClassesWithAnnotation(annotationClass.getName())
                    .stream().map(ClassInfo::loadClass).collect(Collectors.toSet());
        }
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
        if (packageNames == null || packageNames.length == 0) {
            packageNames = defaultPackages();
        }
        Set<T> set;
        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableAllInfo()
                             .whitelistPackages(packageNames)
                             .scan()) {
            ClassInfoList classInfos = parentClass.isInterface() ?
                    scanResult.getClassesImplementing(parentClass.getName()) :
                    scanResult.getSubclasses(parentClass.getName());
            set = classInfos
                    .stream()
                    .filter(classInfo -> {
                        Class<T> subclass = classInfo.loadClass(parentClass);
                        if (!classInfo.isAbstract() && Reflection.canInitiate(subclass)) {
                            return true;
                        } else {
                            System.out.println(subclass + " can not be initiated, ignored.");
                            return false;
                        }
                    })
                    .map(classInfo -> {
                        try {
                            return classInfo.loadClass(parentClass).newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toSet());
        }
        return set;
    }

    private static String[] defaultPackages() {
        String[] packagesToScan = XianConfig.getStringArray("packagesToScan", new String[]{"com"});
        return ArrayUtil.concat(new String[]{"info.xiancloud"}, packagesToScan);
    }
}

