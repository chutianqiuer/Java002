package com.example.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Web应用初始化器
 *
 * 传统web.xml配置 vs Java配置：
 *
 * 传统方式（web.xml）：
 * <web-app>
 *     <servlet>
 *         <servlet-name>dispatcher</servlet-name>
 *         <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
 *         <init-param>
 *             <param-name>contextClass</param-name>
 *             <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
 *         </init-param>
 *         <init-param>
 *             <param-name>contextConfigLocation</param-name>
 *             <param-value>com.example.config.AppConfig</param-value>
 *         </init-param>
 *         <load-on-startup>1</load-on-startup>
 *     </servlet>
 *     <servlet-mapping>
 *         <servlet-name>dispatcher</servlet-name>
 *         <url-pattern>/</url-pattern>
 *     </servlet-mapping>
 * </web-app>
 *
 * Java配置方式（本类）：
 * - 继承 AbstractAnnotationConfigDispatcherServletInitializer
 * - 替代传统的 web.xml 配置
 * - 完全基于Java代码配置，无需XML
 *
 * 工作原理：
 * Servlet 3.0+ 容器会自动发现实现了 WebApplicationInitializer 接口的类
 * 并调用其中的方法来完成Spring MVC的初始化
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    /**
     * 获取根配置类
     *
     * 根配置类定义的是父容器（Root Application Context）的配置。
     * 父容器通常包含：
     * - 数据源配置
     * - 事务管理器
     * - Service层Bean
     * - Repository层Bean
     *
     * 子容器（Web容器）可以访问父容器中的Bean，反之不行。
     *
     * @return 根配置类的Class数组
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        // 返回null表示不使用根配置类
        // 如果有业务层的配置（如@Service、@Repository），应该在这里指定
        return new Class<?>[]{AppConfig.class};
    }

    /**
     * 获取Web配置类
     *
     * Web配置类定义的是子容器（Servlet Application Context）的配置。
     * 子容器包含：
     * - @Controller 控制器
     * - @RequestMapping 映射
     * - ViewResolver
     * - Interceptor
     * - MessageConverter等Web相关组件
     *
     * @return Web配置类的Class数组
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        // 返回WebConfig作为Servlet配置类
        // WebConfig继承自WebMvcConfigurerAdapter，提供了Spring MVC的配置
        return new Class<?>[]{WebConfig.class};
    }

    /**
     * 获取Servlet映射
     *
     * 配置DispatcherServlet处理的URL模式。
     * "/" 表示处理所有请求，包括静态资源。
     * "/*" 会匹配所有路径，包括*.jsp，这会导致JSP无法被正确处理。
     *
     * @return URL模式数组
     */
    @Override
    protected String[] getServletMappings() {
        // "/" 映射到DispatcherServlet
        // 所有请求都会经过DispatcherServlet处理
        return new String[]{"/"};
    }

    /**
     * 配置过滤器
     *
     * 可以在这里注册Filters，这些过滤器会应用到所有请求。
     *
     * @return FilterRegistrationBean数组
     */
    // @Override
    // protected Filter[] getServletFilters() {
    //     // 示例：配置字符编码过滤器
    //     CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
    //     encodingFilter.setEncoding("UTF-8");
    //     encodingFilter.setForceEncoding(true);
    //
    //     return new Filter[]{encodingFilter};
    // }
}
