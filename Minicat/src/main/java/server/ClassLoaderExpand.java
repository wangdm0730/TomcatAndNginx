package server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author wangdm
 * @description 查找固定文件夹内的class文件
 */
public class ClassLoaderExpand extends ClassLoader {
    //    /**
//     * name class 类的文件名
//     */
//    @Override
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        byte[] datas = loadClassData(name);
//        return defineClass(name, datas, 0, datas.length);
//    }


    @Override
    protected Class<?> findClass(String basePath, String name) {
        String myPath = "file:///"+basePath+name.replaceAll("\\.","/")+".class";
        System.out.println("myServletPath---->"+myPath);
        byte[] classBytes = null;
        Path path = null;
        try {
            path = Paths.get(new URI(myPath));
            classBytes = Files.readAllBytes(path);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        Class<?> aClass = defineClass(name, classBytes, 0, classBytes.length);
        return aClass;
    }

}

