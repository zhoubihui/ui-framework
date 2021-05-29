package com.pumpkin.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

/**
 * @className: FileUtils
 * @description: 文件工具类
 * @author: pumpkin
 * @date: 2021/5/8 7:36 上午
 * @version: 1.0
 **/
public class FileUtils {

    /**
     * 通过加载器，从根目录读取文件
     * 注意：path的第一个字符不能是/
     * @param path
     * @return
     */
    public static InputStream getResourceAsStream(String path) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(path);
    }

    /**
     * 根据类加载器，找到相对路径的绝对路径
     * @param relativelyPath
     * @return
     */
    public static String getResource(String relativelyPath) {
        return ClassLoader.getSystemClassLoader().getResource(relativelyPath).getPath();
    }

    /**
     * 从指定目录下查找文件名和fileName匹配的文件路径,只查找当前目录
     * @param relativelyDirectoryPath 相对路径的目录
     * @param fileName
     * @return
     */
    public static String getFilePathFromDirectory(String relativelyDirectoryPath, String fileName) {
        Collection<File> files = org.apache.commons.io.FileUtils.listFiles(
                new File(getResource(relativelyDirectoryPath)),
                new NameEqualFilter(fileName),
                null);
        Optional<String> first = files.stream().map(File::getAbsolutePath).findFirst();
        if (first.isPresent()) {
            String absolutePath = first.get();
            int index = absolutePath.indexOf(relativelyDirectoryPath);
            //返回相对路径
            return absolutePath.substring(index);
        }
        return "";
    }

    static class NameEqualFilter extends AbstractFileFilter {
        private final String name;
        NameEqualFilter(String name) {
            this.name = name;
        }

        @Override
        public boolean accept(File file) {
            return FilenameUtils.getBaseName(file.getName()).equals(name);
        }
    }
}
