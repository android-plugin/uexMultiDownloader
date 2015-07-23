package org.zywx.wbpalmstar.plugin.uexmultidownloader.utils;

import java.io.File;
import java.io.IOException;

/**
 * 文件操作工具类
 *
 * @author AigeStudio 2015-05-08
 */
public final class FileUtil {
    /**
     * 根据URL路径获取文件名
     *
     * @param url URL路径
     * @return 文件名
     */
    public static String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/")).replace("/","");
    }

    /**
     * 创建文件夹
     *
     * @param path 文件夹路径
     * @return 创建了的文件夹File对象
     */
    public static File makeDir(String path) {
        File dir = new File(path);
        if (!isExist(dir)) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 创建文件
     * @return 文件File对象
     */
    public static File createFile(String path) {
        String dirPath=path.substring(0,path.lastIndexOf(File.separator));
        makeDir(dirPath);
        File file = new File(path);
        if (!isExist(file)) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 判断File对象所指的目录或文件是否存在
     *
     * @param file File对象
     * @return true表示存在 false反之
     */
    public static boolean isExist(File file) {
        return file.exists();
    }


    /**
     * 删除文件
     * @param filePath
     */
    public static void deleteFile(String filePath){
        if (filePath==null){
            return;
        }
        File file=new File(filePath);
        if (file.exists()){
            file.delete();
        }
    }

}
