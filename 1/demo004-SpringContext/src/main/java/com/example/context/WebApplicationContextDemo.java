package com.example.context;

/**
 * WebApplicationContext 示例说明类
 *
 * 【功能说明】
 * WebApplicationContext是专门为Web应用设计的ApplicationContext接口
 * 它继承自ApplicationContext接口，并添加了与Web容器交互的功能
 *
 * 【与普通ApplicationContext的区别】
 * 1. WebApplicationContext可以获取ServletContext
 * 2. Bean的作用域默认是singleton，但支持request、session、globalSession作用域
 * 3. 由Web服务器（Tomcat、Jetty等）在启动时自动创建
 * 4. 可以使用@Value等注解从properties文件注入配置
 *
 * 【常见的WebApplicationContext实现类】
 * 1. XmlWebApplicationContext: 从XML文件加载配置的Web应用上下文
 * 2. AnnotationConfigWebApplicationContext: 从注解配置类加载的Web应用上下文
 * 3. GroovyWebApplicationContext: 从Groovy脚本加载配置的Web应用上下文（Spring 5.0+）
 * 4. AnnotationConfigServletWebApplicationContext: Servlet级别的注解配置上下文
 * 5. AnnotationConfigReactiveWebApplicationContext: 响应式Web应用的注解配置上下文
 *
 * 【作用域说明】
 * - singleton（默认）：整个Web应用只有一个Bean实例
 * - prototype：每次请求创建一个新实例
 * - request：每个HTTP请求创建一个新实例（仅Web应用有效）
 * - session：每个HTTP会话创建一个新实例（仅Web应用有效）
 * - globalSession：每个全局HTTPS会话创建一个新实例（仅Portlet应用有效）
 * - application：每个ServletContext创建一个新实例（仅Web应用有效）
 *
 * 【如何创建（代码示例）】
 * WebApplicationContext不能像普通ApplicationContext那样直接new创建
 * 它由Spring的ContextLoaderListener或AbstractDispatcherServletInitializer自动创建
 *
 * 【使用场景】
 * 1. Spring MVC Web应用
 * 2. Spring Boot Web应用
 * 3. 使用Spring Security保护Web资源
 * 4. 任何需要与Web容器集成的Spring应用
 */
public class WebApplicationContextDemo {

    /**
     * 演示WebApplicationContext的使用方式和配置方法
     *
     * 注意：由于WebApplicationContext需要Web容器环境才能运行
     * 这里主要提供说明和示例代码，实际运行需要部署到Web服务器
     */
    public static void explain() {
        System.out.println(">>> WebApplicationContext 说明");

        // 第一部分：基本概念
        System.out.println();
        System.out.println("1. WebApplicationContext基本概念：");
        System.out.println("   WebApplicationContext是专门为Web应用设计的应用上下文");
        System.out.println("   它是ApplicationContext的子接口，增加了：");
        System.out.println("   - 与Web容器（ServletContext）集成");
        System.out.println("   - 额外的作用域：request、session、globalSession");
        System.out.println("   - 获取ServletContext和ServletConfig的能力");

        // 第二部分：实现类说明
        System.out.println();
        System.out.println("2. 常见的WebApplicationContext实现类：");
        System.out.println("   | 实现类                              | 配置方式     | 适用场景                    |");
        System.out.println("   |------------------------------------|-------------|---------------------------|");
        System.out.println("   | XmlWebApplicationContext           | XML配置     | 传统Spring MVC项目         |");
        System.out.println("   | AnnotationConfigWebApplicationContext | 注解配置  | 现代Spring MVC项目         |");
        System.out.println("   | AnnotationConfigServletWebApplicationContext | 注解+Servlet | Spring Boot嵌入式容器 |");
        System.out.println("   | GroovyWebApplicationContext       | Groovy脚本   | 需要动态配置的场景         |");

        // 第三部分：配置方式
        System.out.println();
        System.out.println("3. WebApplicationContext配置方式：");

        // 方式一：传统web.xml配置（适用于Spring 5.x之前的版本）
        System.out.println("   方式一：web.xml中配置Listener（传统方式）");
        System.out.println("   <context-param>");
        System.out.println("       <param-name>contextConfigLocation</param-name>");
        System.out.println("       <param-value>/WEB-INF/applicationContext.xml</param-value>");
        System.out.println("   </context-param>");
        System.out.println("   <listener>");
        System.out.println("       <listener-class>");
        System.out.println("           org.springframework.web.context.ContextLoaderListener");
        System.out.println("       </listener-class>");
        System.out.println("   </listener>");

        // 方式二：Java配置方式（Spring 3.x之后的推荐方式）
        System.out.println();
        System.out.println("   方式二：Java配置方式（Spring Boot推荐）");
        System.out.println("   public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {");
        System.out.println("       @Override");
        System.out.println("       protected Class<?>[] getRootConfigClasses() {");
        System.out.println("           return new Class<?>[] { RootConfig.class };");
        System.out.println("       }");
        System.out.println("       @Override");
        System.out.println("       protected Class<?>[] getServletConfigClasses() {");
        System.out.println("           return new Class<?>[] { WebConfig.class };");
        System.out.println("       }");
        System.out.println("       @Override");
        System.out.println("       protected String[] getServletMappings() {");
        System.out.println("           return new String[] { \"/\" };");
        System.out.println("       }");
        System.out.println("   }");

        // 方式三：Spring Boot方式（最简洁）
        System.out.println();
        System.out.println("   方式三：Spring Boot方式（最简洁）");
        System.out.println("   @SpringBootApplication");
        System.out.println("   public class Application extends SpringBootServletInitializer {");
        System.out.println("       public static void main(String[] args) {");
        System.out.println("           SpringApplication.run(Application.class, args);");
        System.out.println("       }");
        System.out.println("   }");

        // 第四部分：获取WebApplicationContext的方式
        System.out.println();
        System.out.println("4. 获取WebApplicationContext的方式：");

        System.out.println("   4.1 在Controller中注入：");
        System.out.println("       @Autowired");
        System.out.println("       private WebApplicationContext context;");

        System.out.println("   4.2 通过ServletContext获取：");
        System.out.println("       ServletContext servletContext = getServletContext();");
        System.out.println("       WebApplicationContext context = WebApplicationContextUtils");
        System.out.println("           .getWebApplicationContext(servletContext);");

        System.out.println("   4.3 在Servlet中获取：");
        System.out.println("       WebApplicationContext context = getServletContext()");
        System.out.println("           .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);");

        // 第五部分：作用域示例
        System.out.println();
        System.out.println("5. Web作用域示例：");
        System.out.println("   @Controller");
        System.out.println("   public class MyController {");
        System.out.println("       // 默认singleton作用域 - 整个应用只有一个实例");
        System.out.println("       private int counter; // 非线程安全！");
        System.out.println("");
        System.out.println("       @RequestScope // 每个请求创建一个新实例");
        System.out.println("       private class RequestScopeBean {");
        System.out.println("           private String requestId;");
        System.out.println("       }");
        System.out.println("");
        System.out.println("       @SessionScope // 每个session创建一个新实例");
        System.out.println("       private class SessionScopeBean {");
        System.out.println("           private String userId;");
        System.out.println("       }");
        System.out.println("   }");

        // 第六部分：与普通ApplicationContext的关系
        System.out.println();
        System.out.println("6. WebApplicationContext的层次结构：");
        System.out.println("   在Web应用中，存在两个级别的容器：");
        System.out.println("   - Root WebApplicationContext: 由ContextLoaderListener创建");
        System.out.println("     用于服务整个应用，通常配置数据源、安全等全局Bean");
        System.out.println("   - Servlet WebApplicationContext: 由DispatcherServlet创建");
        System.out.println("     只服务于特定的Servlet，用于配置Web层组件（Controller等）");
        System.out.println("   子容器可以访问父容器的Bean，反之不行");

        System.out.println();
        System.out.println("<<< WebApplicationContext说明结束");
        System.out.println("提示：实际运行需要部署到Web服务器，如Tomcat");
    }
}
