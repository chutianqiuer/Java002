package com.example.resource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.*;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * ResourceLoader 示例类
 *
 * 【功能说明】
 * ResourceLoader是Spring框架用于加载资源的接口
 * ApplicationContext继承了ResourceLoader接口，因此所有ApplicationContext实现类都可以加载资源
 *
 * 【Resource接口】
 * Resource是Spring对底层资源的抽象，封装了对各种资源的访问：
 * 1. UrlResource：访问URL资源（http、ftp等）
 * 2. ClassPathResource：访问classpath资源
 * 3. FileSystemResource：访问文件系统资源
 * 4. ServletContextResource：访问Web应用根目录下的资源
 * 5. InputStreamResource：访问输入流资源
 * 6. ByteArrayResource：访问字节数组资源
 *
 * 【ResourceLoader接口】
 * ResourceLoader提供了统一的资源加载方式：
 * - Resource getResource(String location)：根据路径获取资源
 * - ClassLoader getClassLoader()：获取类加载器
 *
 * 【路径格式】
 * Spring支持多种路径格式：
 * 1. classpath:/path/to/file - 从classpath加载
 * 2. file:/path/to/file - 从文件系统加载
 * 3. http://example.com/file - 从URL加载
 * 4. /path/to/file - 相对路径，根据具体实现决定
 *
 * 【与ClassLoader的区别】
 * ClassLoader：用于加载类和资源文件，仅从classpath加载
 * ResourceLoader：更通用的资源加载接口，支持多种资源协议
 *
 * 【使用场景】
 * 1. 读取配置文件（properties、xml等）
 * 2. 读取静态资源（图片、样式表等）
 * 3. 读取模板文件（Freemarker、Thymeleaf等）
 * 4. 访问Web应用资源
 */
public class ResourceLoaderDemo {

    /**
     * 演示ResourceLoader的各种功能
     *
     * 本方法展示了：
     * 1. 如何使用ApplicationContext加载资源
     * 2. 不同类型Resource的使用方式
     * 3. 读取资源内容的各种方式
     */
    public static void demo() {
        System.out.println(">>> 演示ResourceLoader的使用");

        // 创建ApplicationContext（它实现了ResourceLoader接口）
        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext();

        // 方式一：使用ApplicationContext作为ResourceLoader
        // ApplicationContext实现了ResourceLoader接口
        System.out.println();
        System.out.println("1. 使用ApplicationContext作为ResourceLoader：");
        ResourceLoader resourceLoader = context;

        // 方式二：获取ResourceLoader实例
        System.out.println("   ApplicationContext本身就是一个ResourceLoader");
        System.out.println("   获取Resource: context.getResource(\"classpath:application.properties\")");

        // 第二部分：加载不同类型的资源
        System.out.println();
        System.out.println("2. 加载不同类型的资源：");

        // 2.1 从classpath加载
        System.out.println("   2.1 从classpath加载（classpath:前缀）:");
        Resource classpathResource = resourceLoader.getResource("classpath:application.properties");
        System.out.println("       资源描述: " + classpathResource.getDescription());
        System.out.println("       是否存在: " + classpathResource.exists());
        System.out.println("       是否可读: " + classpathResource.isReadable());

        // 2.2 从文件系统加载（file:前缀）
        System.out.println("   2.2 从文件系统加载（file:前缀）:");
        Resource fileResource = resourceLoader.getResource("file:src/main/resources/beans.xml");
        System.out.println("       资源描述: " + fileResource.getDescription());
        System.out.println("       是否存在: " + fileResource.exists());

        // 2.3 使用URL格式加载
        System.out.println("   2.3 从URL加载（http:前缀）:");
        System.out.println("       注意：实际运行可能需要网络连接");
        System.out.println("       示例：resourceLoader.getResource(\"https://example.com/data.json\")");

        // 第三部分：Resource接口的方法
        System.out.println();
        System.out.println("3. Resource接口的主要方法：");
        System.out.println("   - exists(): 判断资源是否存在");
        System.out.println("   - isReadable(): 判断资源是否可读");
        System.out.println("   - isOpen(): 判断资源是否已打开");
        System.out.println("   - getDescription(): 获取资源描述");
        System.out.println("   - getInputStream(): 获取输入流");
        System.out.println("   - getFile(): 获取文件对象（仅文件系统资源可用）");
        System.out.println("   - getURL(): 获取URL（如果资源支持）");

        // 第四部分：读取资源内容
        System.out.println();
        System.out.println("4. 读取资源内容：");

        // 4.1 使用InputStream读取
        System.out.println("   4.1 使用InputStream读取:");
        try {
            InputStream inputStream = classpathResource.getInputStream();
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("       内容长度: " + content.length() + " 字符");
            System.out.println("       内容预览: " + content.substring(0, Math.min(100, content.length())) + "...");
            inputStream.close();
        } catch (IOException e) {
            System.out.println("       读取失败: " + e.getMessage());
        }

        // 4.2 使用StreamUtils读取
        System.out.println("   4.2 使用StreamUtils读取:");
        try {
            InputStream inputStream = classpathResource.getInputStream();
            String content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            System.out.println("       内容长度: " + content.length() + " 字符");
            inputStream.close();
        } catch (IOException e) {
            System.out.println("       读取失败: " + e.getMessage());
        }

        // 4.3 使用Reader读取
        System.out.println("   4.3 使用Reader读取:");
        try {
            InputStream inputStream = classpathResource.getInputStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            char[] buffer = new char[100];
            int read = reader.read(buffer);
            System.out.println("       读取字符数: " + read);
            System.out.println("       内容预览: " + new String(buffer, 0, read) + "...");
            reader.close();
        } catch (IOException e) {
            System.out.println("       读取失败: " + e.getMessage());
        }

        // 第五部分：不同Resource类型
        System.out.println();
        System.out.println("5. 不同Resource类型：");

        // ClassPathResource
        System.out.println("   5.1 ClassPathResource:");
        ClassPathResource cpResource = new ClassPathResource("application.properties");
        System.out.println("       类: " + cpResource.getClass().getSimpleName());
        System.out.println("       路径: " + cpResource.getPath());
        System.out.println("       文件名: " + cpResource.getFilename());

        // FileSystemResource
        System.out.println("   5.2 FileSystemResource:");
        FileSystemResource fsResource = new FileSystemResource("src/main/resources/beans.xml");
        System.out.println("       类: " + fsResource.getClass().getSimpleName());
        System.out.println("       路径: " + fsResource.getPath());
        System.out.println("       文件名: " + fsResource.getFilename());
        System.out.println("       是否文件: " + fsResource.isFile());

        // UrlResource
        System.out.println("   5.3 UrlResource:");
        try {
            UrlResource urlResource = new UrlResource("https://www.springframework.org/schema/beans/spring-beans.xsd");
            System.out.println("       类: " + urlResource.getClass().getSimpleName());
            System.out.println("       协议: " + urlResource.getURL().getProtocol());
            System.out.println("       主机: " + urlResource.getURL().getHost());
            System.out.println("       路径: " + urlResource.getURL().getPath());
        } catch (java.net.MalformedURLException e) {
            System.out.println("       URL格式错误: " + e.getMessage());
        }

        // 第六部分：ClassPathResource vs FileSystemResource
        System.out.println();
        System.out.println("6. ClassPathResource vs FileSystemResource：");
        System.out.println("   | 特性         | ClassPathResource          | FileSystemResource           |");
        System.out.println("   |--------------|---------------------------|-----------------------------|");
        System.out.println("   | 资源位置     | classpath中                | 文件系统任意位置             |");
        System.out.println("   | 路径格式     | classpath:/path 或 /path   | 绝对或相对文件路径           |");
        System.out.println("   | 重新加载     | 依赖ClassLoader            | 直接访问文件系统             |");
        System.out.println("   | 适合场景     | 打包到jar中的资源          | 外部配置文件                |");

        // 第七部分：使用Resource作为参数注入
        System.out.println();
        System.out.println("7. 在Bean中注入Resource：");
        System.out.println("   方式一：通过@Value注入");
        System.out.println("       @Value(\"classpath:config.json\")");
        System.out.println("       private Resource configFile;");
        System.out.println("");
        System.out.println("   方式二：通过参数注入");
        System.out.println("       @Bean");
        System.out.println("       public Resource templateResource(ResourceLoader loader) {");
        System.out.println("           return loader.getResource(\"classpath:template.html\");");
        System.out.println("       }");

        // 第八部分：通配符路径
        System.out.println();
        System.out.println("8. 通配符路径（使用PatternResourceLoader）：");
        System.out.println("   Resource[] resources = context.getResources(\"classpath:config/*.properties\");");
        System.out.println("   支持的通配符：");
        System.out.println("   - ?: 匹配单个字符");
        System.out.println("   - *: 匹配任意数量的字符（不包括目录分隔符）");
        System.out.println("   - **: 匹配任意数量的目录");

        // 使用通配符获取资源
        try {
            Resource[] xmlResources = context.getResources("classpath:*.xml");
            System.out.println("   找到 " + xmlResources.length + " 个XML资源文件:");
            for (Resource r : xmlResources) {
                System.out.println("     - " + r.getFilename());
            }
        } catch (IOException e) {
            System.out.println("   获取通配符资源失败: " + e.getMessage());
        }

        // 关闭容器
        context.close();

        System.out.println();
        System.out.println("<<< ResourceLoader演示结束");
    }
}
