package cn.zhh.api;

import cn.zhh.api.impl.ModuleClassLoader;
import com.google.common.collect.Lists;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.ServiceLoader;

public class Test {

    public static void main(String[] args) throws Exception {
        Path path = Paths.get("/Users/mozat-gz/IdeaProjects/dynamic-module/module/target/module-1.0-SNAPSHOT.jar");
        URL url = path.toUri().toURL();
        ModuleClassLoader moduleClassLoader = new ModuleClassLoader(Lists.newArrayList(url), Thread.currentThread().getContextClassLoader(), Lists.newArrayList());
        ServiceLoader<MyModuleConfig> load = ServiceLoader.load(MyModuleConfig.class, moduleClassLoader);
        for (Iterator<MyModuleConfig> iterator = load.iterator(); iterator.hasNext(); ) {
            System.out.println(iterator.next().getName());
        }
        FileSystems.getDefault().newWatchService().
    }
}
