package com.example.context;

import com.example.model.Config;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * FileSystemXmlApplicationContext 示例类
 *
 * 【功能说明】
 * FileSystemXmlApplicationContext是ApplicationContext接口的实现类之一
 * 它从文件系统路径中加载XML配置文件来创建Spring容器
 *
 * 【工作原理】
 * 1. 构造函数接收文件系统路径（可以是绝对路径或相对路径）
 * 2. 解析XML配置文件，获取Bean定义信息
 * 3. 实例化所有单例Bean（默认行为）
 * 4. 容器准备就绪
 *
 * 【与ClassPathXmlApplicationContext的区别】
 * - FileSystemXmlApplicationContext: 从文件系统路径加载，路径可以是绝对或相对路径
 * - ClassPathXmlApplicationContext: 从classpath加载，配置文件必须在类路径中
 *
 * 【何时使用】
 * 1. 配置文件放在文件系统任意位置，不在classpath中
 * 2. 需要在运行时动态变更配置文件
 * 3. 配置文件独立于应用部署
 * 4. 需要在不同环境使用不同配置文件
 *
 * 【路径说明】
 * - 绝对路径：以/开头的Unix路径，或盘符开头的Windows路径
 *   例如：/opt/spring/config/beans.xml 或 C:\config\beans.xml
 * - 相对路径：相对于当前工作目录的路径
 *   例如：src/main/resources/beans.xml
 * - classpath前缀：可以使用classpath:前缀明确指定从classpath加载
 *   例如：classpath:beans.xml
 *
 * 【优缺点】
 * 优点：
 * - 配置位置灵活，可以在文件系统任意位置
 * - 支持运行时变更配置，无需重新编译
 * - 适合外部化配置管理
 * - 可以加载多个位置的配置文件
 *
 * 缺点：
 * - 需要确保配置文件路径正确
 * - 路径配置在不同环境可能不一致
 * - 需要处理文件系统权限问题
 * - 部署时需要确保配置文件被正确部署
 */
public class FileSystemXmlApplicationContextDemo {

    /**
     * 演示FileSystemXmlApplicationContext的基本用法
     *
     * 本方法展示了：
     * 1. 如何创建FileSystemXmlApplicationContext实例
     * 2. 不同路径格式的使用方式
     * 3. 相对路径与绝对路径的处理
     */
    public static void demo() {
        System.out.println(">>> 演示FileSystemXmlApplicationContext的使用");

        // 注意：由于我们没有实际的外部XML文件，这里演示如何构造路径
        // 在实际使用中，配置文件通常放在项目外部

        // 第一步：路径格式说明
        System.out.println();
        System.out.println("1. 路径格式说明：");
        System.out.println("   FileSystemXmlApplicationContext支持以下路径格式：");
        System.out.println("   - 绝对路径：file:/opt/spring/beans.xml");
        System.out.println("   - 相对路径：./src/main/resources/beans.xml");
        System.out.println("   - classpath前缀：classpath:beans.xml");
        System.out.println("   - 多文件加载：new FileSystemXmlApplicationContext(\"file1.xml\",\"file2.xml\")");

        // 第二步：创建容器的不同方式
        System.out.println();
        System.out.println("2. 创建容器的不同方式：");

        // 方式一：使用相对路径
        // 相对于当前工作目录的路径
        System.out.println("   2.1 使用相对路径（相对于当前工作目录）:");
        String relativePath = "src/main/resources/beans.xml";
        System.out.println("       路径: " + relativePath);
        System.out.println("       当前工作目录: " + System.getProperty("user.dir"));
        System.out.println("       完整路径: " + System.getProperty("user.dir") + "/" + relativePath);

        // 方式二：使用绝对路径
        System.out.println("   2.2 使用绝对路径:");
        String absolutePath = "/workspace/1/demo004-SpringContext/src/main/resources/beans.xml";
        System.out.println("       路径: " + absolutePath);

        // 方式三：使用file:前缀明确指定为文件系统路径
        System.out.println("   2.3 使用file:前缀:");
        String filePrefixPath = "file:./src/main/resources/beans.xml";
        System.out.println("       路径: " + filePrefixPath);

        // 第三步：实际创建并使用容器
        // 由于FileSystemXmlApplicationContext需要文件系统存在配置文件
        // 我们使用相对路径来加载实际的beans.xml文件
        System.out.println();
        System.out.println("3. 创建并使用FileSystemXmlApplicationContext容器：");

        // 使用相对路径创建容器
        // 注意：如果路径不存在，构造函数会抛出BeanDefinitionStoreException
        try {
            // 首先检查文件是否存在
            java.io.File xmlFile = new java.io.File(relativePath);
            if (xmlFile.exists()) {
                // 方式一：使用相对路径字符串
                FileSystemXmlApplicationContext context =
                    new FileSystemXmlApplicationContext(relativePath);
                System.out.println("   容器创建成功（使用相对路径）！");

                // 获取Bean并演示
                Config config = context.getBean("xmlConfigBean", Config.class);
                System.out.println("   获取到的Bean: " + config);
                System.out.println("   消息: " + config.getMessage());

                // 关闭容器
                context.close();
            } else {
                System.out.println("   配置文件不存在，跳过实际加载演示");
                System.out.println("   文件路径: " + xmlFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("   加载失败: " + e.getMessage());
        }

        // 第四步：classpath前缀的使用
        System.out.println();
        System.out.println("4. classpath前缀的使用：");
        System.out.println("   可以使用classpath:前缀让FileSystemXmlApplicationContext从classpath加载：");
        System.out.println("   FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(");
        System.out.println("       \"classpath:beans.xml\");");

        // 第五步：同时加载多个配置文件
        System.out.println();
        System.out.println("5. 同时加载多个配置文件：");
        System.out.println("   String[] configLocations = {\"file1.xml\", \"file2.xml\", \"file3.xml\"};");
        System.out.println("   FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(configLocations);");

        // 第六步：使用ConfigurableFileSystemXmlApplicationContext
        System.out.println();
        System.out.println("6. 高级用法 - ConfigurableFileSystemXmlApplicationContext：");
        System.out.println("   这个类提供了更多的配置选项，例如：");
        System.out.println("   - setConfigLocations(): 设置配置文件位置");
        System.out.println("   - getEnvironment(): 获取环境对象");
        System.out.println("   - setValidateXml(): 设置是否验证XML");

        System.out.println();
        System.out.println("<<< FileSystemXmlApplicationContext演示结束");
    }
}
