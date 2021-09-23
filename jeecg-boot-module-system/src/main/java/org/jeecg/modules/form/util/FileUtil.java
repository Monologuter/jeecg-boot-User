package org.jeecg.modules.form.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static java.nio.charset.StandardCharsets.*;

/**
 * 文件工具类
 *
 * @author: HuQi
 * @date: 2021年07月08日 09:09
 */
public class FileUtil {
    
    /**
     * @Description: 读取文件内容
     * @param in 输入流数据
     * @param split 每行的字符分割串
     * @return java.lang.String 
     * @Author HuQi
     * @create 2021-08-03 09:36
     */
    public static String readFile(InputStream in, String split) {
        StringBuilder sb = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(in, UTF_8))) {
            String readLine;
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine).append(split);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * @Description: 判断指定的文件是否存在。
     * @param fileName 文件路径
     * @return boolean
     * @Author HuQi
     * @create 2021-08-03 09:42
     */
    public static boolean isFileExist(String fileName) {
        return new File(fileName).isFile();
    }

    /**
     * @Description: 创建指定的目录。 如果指定的目录的父目录不存在则创建其目录书上所有需要的父目录。
     *                注意：可能会在返回false的时候创建部分父目录。
     * @param file 文件路径
     * @return boolean
     * @Author HuQi
     * @create 2021-08-03 09:43
     */
    public static boolean makeDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            return parent.mkdirs();
        }
        return false;
    }

    /**
     * @Description: 返回文件的URL地址。
     * @param file 文件路径
     * @return java.net.URL
     * @Author HuQi
     * @create 2021-08-03 09:44
     */
    public static URL getURL(File file) throws MalformedURLException {
        String fileURL = "file:/" + file.getAbsolutePath();
        URL url = new URL(fileURL);
        return url;
    }

    /**
     * @Description: 从文件路径得到文件名。
     * @param filePath 文件路径
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 09:44
     */
    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    /**
     * @Description: 从文件名得到文件绝对路径。
     * @param fileName 文件名
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 09:44
     */
    public static String getFilePath(String fileName) {
        File file = new File(fileName);
        return file.getAbsolutePath();
    }

    /**
     * @Description: 将DOS/Windows格式的路径转换为UNIX/Linux格式的路径。
     * @param filePath 文件路径
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 09:45
     */
    public static String toUNIXpath(String filePath) {
        return filePath.replace("", "/");
    }

    /**
     * @Description: 从文件名得到UNIX风格的文件绝对路径。
     * @param fileName 文件名
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 09:46
     */
    public static String getUNIXfilePath(String fileName) {
        File file = new File(fileName);
        return toUNIXpath(file.getAbsolutePath());
    }

    /**
     * @Description: 得到文件后缀名
     * @param fileName 文件名
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 09:46
     */
    public static String getFileExt(String fileName) {
        int point = fileName.lastIndexOf('.');
        int length = fileName.length();
        if (point == -1 || point == length - 1) {
            return "";
        } else {
            return fileName.substring(point + 1, length);
        }
    }

    /**
     * @Description: 得到文件的名字部分。 实际上就是路径中的最后一个路径分隔符后的部分。
     * @param fileName 文件名
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 09:47
     */
    public static String getNamePart(String fileName) {
        int point = getPathLastIndex(fileName);
        int length = fileName.length();
        if (point == -1) {
            return fileName;
        } else if (point == length - 1) {
            int secondPoint = getPathLastIndex(fileName, point - 1);
            if (secondPoint == -1) {
                if (length == 1) {
                    return fileName;
                } else {
                    return fileName.substring(0, point);
                }
            } else {
                return fileName.substring(secondPoint + 1, point);
            }
        } else {
            return fileName.substring(point + 1);
        }
    }

    /**
     * @Description: 得到文件名中的父路径部分。 对两种路径分隔符都有效。 不存在时返回""。
     *      如果文件名是以路径分隔符结尾的则不考虑该分隔符，例如"/path/"返回""。
     * @param fileName 文件名
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 09:47
     */
    public static String getPathPart(String fileName) {
        int point = getPathLastIndex(fileName);
        int length = fileName.length();
        if (point == -1) {
            return "";
        } else if (point == length - 1) {
            int secondPoint = getPathLastIndex(fileName, point - 1);
            if (secondPoint == -1) {
                return "";
            } else {
                return fileName.substring(0, secondPoint);
            }
        } else {
            return fileName.substring(0, point);
        }
    }

    /**
     * @Description: 得到路径分隔符在文件路径中最后出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
     * @param fileName 文件名
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:48
     */
    public static int getPathLastIndex(String fileName) {
        int point = fileName.lastIndexOf("/");
        if (point == -1) {
            point = fileName.lastIndexOf("");
        }
        return point;
    }

    /**
     * @Description: 得到路径分隔符在文件路径中指定位置前最后出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
     * @param fileName 文件名
     * @param fromIndex 开始位置
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:57
     */
    public static int getPathLastIndex(String fileName, int fromIndex) {
        int point = fileName.lastIndexOf("/", fromIndex);
        if (point == -1) {
            point = fileName.lastIndexOf("", fromIndex);
        }
        return point;
    }

    /**
     * @Description: 得到路径分隔符在文件路径中首次出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
     * @param fileName 文件名
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:58
     */
    public static int getPathIndex(String fileName) {
        int point = fileName.indexOf("/");
        if (point == -1) {
            point = fileName.indexOf("");
        }
        return point;
    }

    /**
     * @Description: 得到路径分隔符在文件路径中指定位置后首次出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
     * @param fileName 文件名
     * @param fromIndex 开始位置
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:58
     */
    public static int getPathIndex(String fileName, int fromIndex) {
        int point = fileName.indexOf("/", fromIndex);
        if (point == -1) {
            point = fileName.indexOf("", fromIndex);
        }
        return point;
    }

    /**
     * @Description: 将文件名中的类型部分去掉。
     * @param filename 文件名
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 09:58
     */
    public static String removeFileExt(String filename) {
        int index = filename.lastIndexOf(".");
        if (index != -1) {
            return filename.substring(0, index);
        } else {
            return filename;
        }
    }

    /**
     * @Description: 得到相对路径。 文件名不是目录名的子节点时返回文件名。
     * @param pathName 路径名
     * @param fileName 文件名
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 09:58
     */
    public static String getSubpath(String pathName, String fileName) {
        int index = fileName.indexOf(pathName);
        if (index != -1) {
            return fileName.substring(index + pathName.length() + 1);
        } else {
            return fileName;
        }
    }

    /**
     * @Description: 删除一个文件。
     * @param filename 文件名
     * @return boolean
     * @Author HuQi
     * @create 2021-08-03 09:59
     */
    public static boolean deleteFile(String filename) throws IOException {
        File file = new File(filename);
        if (file.isDirectory()) {
            throw new IOException("IOException -> BadInputException: not a file.");
        }
        if (!file.exists()) {
            throw new IOException("IOException -> BadInputException: file is not exist.");
        }
        if (!file.delete()) {
            throw new IOException("Cannot delete file. filename = " + filename);
        }
        return false;
    }

    /**
     * @Description: 删除文件夹及其下面的子文件夹
     * @param dir 文件夹路径
     * @Author HuQi
     * @create 2021-08-03 09:59
     */
    public static void deleteDir(File dir) throws IOException {
        if (dir.isFile()) {
            throw new IOException("IOException -> BadInputException: not a directory.");
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        throw new IOException("Cannot delete file. filename = " + file);
                    }
                } else {
                    deleteDir(file);
                }
            }
        }
        if (!dir.delete()) {
            throw new IOException("Cannot delete file. filename = " + dir);
        }
    }
    
    /**
      *@Description: 创建文件路径
      *@param folders 文件夹路径
      *@Author: HuQi
      *@Date: 2021-07-19 12:00
     **/
    public static void CreateFolders(final String folders) {
        StringTokenizer st = new StringTokenizer(folders, File.separator);
        StringBuilder sb = new StringBuilder();
        String osname = System.getProperty("os.name");
        if (osname.compareToIgnoreCase("linux") == 0) {
            sb.append(File.separator);
        }

        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            File file = new File(sb.toString());
            if (!file.exists()) {
                if (!file.mkdir()){
                    System.out.println("Cannot create destDir. destDir = " + file);
                }
            }
            sb.append(File.separator);
        }

    }
    
    /**
     * @Description: 复制文件
     * @param src 源文件路径
     * @param dst 目的文件路径
     * @Author HuQi
     * @create 2021-08-03 09:56
     */
    public static void copy(File src, File dst) throws Exception {
        int BUFFER_SIZE = 4096;
        try(InputStream in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
    }

    /**
     * @Description: 复制文件，支持把源文件内容追加到目标文件末尾
     * @param src 源文件路径
     * @param dst 目的文件路径
     * @param append 是否追加标识
     * @Author HuQi
     * @create 2021-08-03 09:53
     */
    public static void copy(File src, File dst, boolean append) throws Exception {
        int BUFFER_SIZE = 4096;
        try(InputStream in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(dst, append), BUFFER_SIZE)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
    }

    /**
     * @Description: 复制文件目录到另一个目录
     * @param fromDir 源目录
     * @param toDir 目的目录
     * @Author HuQi
     * @create 2021-08-03 09:50
     */
    public static void copyDir(String fromDir,String toDir) throws Exception {
        //创建目录的File对象
        File dirSouce = new File(fromDir);
        //判断源目录是不是一个目录
        if (!dirSouce.isDirectory()) {
            //如果不是目录那就不复制
            return;
        }
        //创建目标目录的File对象
        File destDir = new File(toDir);
        //如果目的目录不存在
        if(!destDir.exists()){
            //创建目的目录
            if (!destDir.mkdir()){
                throw new IOException("Cannot create destDir. destDir = " + destDir);
            }
        }
        //获取源目录下的File对象列表
        File[] files = dirSouce.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            //拼接新的fromDir(fromFile)和toDir(toFile)的路径
            String strFrom = fromDir + File.separator + file.getName();
            System.out.println(strFrom);
            String strTo = toDir + File.separator + file.getName();
            System.out.println(strTo);
            //判断File对象是目录还是文件
            //判断是否是目录
            if (file.isDirectory()) {
                //递归调用复制目录的方法
                copyDir(strFrom,strTo);
            }
            //判断是否是文件
            if (file.isFile()) {
                System.out.println("正在复制文件："+file.getName());
                //递归调用复制文件的方法
                copy(new File(strFrom),new File(strTo));
            }
        }
    }

    /**
     * @Description: 将字符串写入文件
     * @param txt 字符串（文件内容）
     * @param filePath 到文件路径
     * @Author HuQi
     * @create 2021-08-03 09:48
     */
    public static void stringToFile(String txt, String filePath) {
        try(BufferedWriter out = new BufferedWriter(new FileWriter(filePath))) {
            out.write(txt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 获取指定目录下文件名路径及其对应的文件内容，用于代码生成器上传代码至GitLab功能
     * @param fromDir 文件夹路径
     * @return java.util.Map<java.lang.String,java.lang.Object> 文件路径对应文件内容的Map
     * @Author HuQi
     * @create 2021-08-04 13:23
     */
    public static Map<String, Object> getDirFileToMap(String fromDir) {
        Map<String, Object> map = new HashMap();
        //创建目录的File对象
        File dirSouce = new File(fromDir);
        //判断源目录是不是一个目录
        if (!dirSouce.isDirectory()) {
            //如果不是目录那就不继续
            return map;
        }
        //获取源目录下的File对象列表
        File[] files = dirSouce.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            String content;
            Map<String, Object> addMap;
            //拼接新的fromDir(fromFile)和toDir(toFile)的路径
            String strFrom = fromDir + File.separator + file.getName();
            //判断File对象是目录还是文件
            //判断是否是目录
            if (file.isDirectory()) {
                //递归调用复制目录的方法
                addMap = getDirFileToMap(strFrom);
                map.putAll(addMap);
            }
            //判断是否是文件
            if (file.isFile()) {
                try {
                    content = readFile(new FileInputStream(file), "\\r\\n");
                    map.put(strFrom, content);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * @Description: 获取指定目录下文件名路径列表
     * @param fromDir 文件夹路径
     * @return java.util.List<java.lang.String>
     * @Author HuQi
     * @create 2021-08-24 15:12
     */
    public static List<String> getDirFileList(String fromDir) {
        List<String> li = new ArrayList<>();
        //创建目录的File对象
        File dirSouce = new File(fromDir);
        //判断源目录是不是一个目录
        if (!dirSouce.isDirectory()) {
            //如果不是目录那就不继续
            return li;
        }
        //获取源目录下的File对象列表
        File[] files = dirSouce.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            List<String> addLi;
            //拼接新的fromDir(fromFile)和toDir(toFile)的路径
            String strFrom = fromDir + File.separator + file.getName();
            //判断File对象是目录还是文件
            //判断是否是目录
            if (file.isDirectory()) {
                //递归调用复制目录的方法
                addLi = getDirFileList(strFrom);
                li.addAll(addLi);
            }
            //判断是否是文件
            if (file.isFile()) {
                try {
                    li.add(strFrom);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return li;
    }
}
