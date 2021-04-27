package cn.zhh.api.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;

/**
 * 模块的ClassLoader，继承自URLClassLoader，同时可以强制指定一些包下的class，由本ClassLoader自己加载，不通过父ClassLoader加载，突破双亲委派机制。
 */
public class ModuleClassLoader extends URLClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleClassLoader.class);

    /**
     * java的包必须排除，避免安全隐患
     */
    public static final String[] DEFAULT_EXCLUDED_PACKAGES = new String[]{"java.", "javax.", "sun.", "oracle."};

    /**
     * 需要排除的包
     */
    private final Set<String> excludedPackages = new HashSet<String>();

    /**
     * 需要子加载器优先加载的包
     */
    private final List<String> overridePackages;

    public ModuleClassLoader(List<URL> urls, ClassLoader parent, List<String> overridePackages) {
        super(urls.toArray(new URL[]{}), parent);
        this.overridePackages = overridePackages;
        this.excludedPackages.addAll(Sets.newHashSet(DEFAULT_EXCLUDED_PACKAGES));
    }

    /**
     * 覆盖双亲委派机制
     *
     * @see ClassLoader#loadClass(String, boolean)
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> result = null;
        synchronized (ModuleClassLoader.class) {
            if (isEligibleForOverriding(name)) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Load class for overriding: {}", name);
                }
                result = loadClassForOverriding(name);
            }
            if (result != null) {
                //链接类
                if (resolve) {
                    resolveClass(result);
                }
                return result;
            }
        }
        //使用默认类加载方式
        return super.loadClass(name, resolve);

    }

    /**
     * 加载一个子模块覆盖的类
     *
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> loadClassForOverriding(String name) throws ClassNotFoundException {
        //查找已加载的类
        Class<?> result = findLoadedClass(name);
        if (result == null) {
            //加载类
            result = findClass(name);
        }
        return result;
    }

    /**
     * 判断该名字是否是需要覆盖的 class
     *
     * @param name
     * @return
     */
    private boolean isEligibleForOverriding(final String name) {
        checkNotNull(name, "name is null");
        return !isExcluded(name) && any(overridePackages, new Predicate<String>() {
            @Override
            public boolean apply(String prefix) {
                return name.startsWith(prefix);
            }
        });
    }

    /**
     * 判断class是否排除
     *
     * @param className
     * @return
     */
    protected boolean isExcluded(String className) {
        checkNotNull(className, "className is null");
        for (String packageName : this.excludedPackages) {
            if (className.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }

}