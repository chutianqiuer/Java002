package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * 本控制器演示Spring MVC的核心功能，包括：
 *
 * 1. @Controller vs @RestController 的区别
 *    - @Controller：返回视图（View），配合ViewResolver渲染JSP
 *    - @RestController：返回数据（body），相当于@Controller + @ResponseBody
 *
 * 2. @RequestMapping及其衍生注解
 *    - @GetMapping：处理GET请求
 *    - @PostMapping：处理POST请求
 *    - @PutMapping：处理PUT请求
 *    - @DeleteMapping：处理DELETE请求
 *
 * 3. 参数绑定注解
 *    - @RequestParam：绑定请求参数
 *    - @PathVariable：绑定URL路径变量
 *    - @RequestBody：绑定请求体（JSON）
 *
 * 4. Model、ModelMap、ModelAndView的使用
 *    - Model：接口，存储模型数据
 *    - ModelMap：类，比Model功能更丰富
 *    - ModelAndView：包含模型和视图的对象
 */
@Controller
@RequestMapping("/users")
public class UserController {

    /**
     * 依赖注入用户服务
     *
     * @Autowired 注解用于自动注入UserService实例
     * Spring会自动查找类型为UserService的Bean并注入
     */
    @Autowired
    private UserService userService;

    /**
     * 转向到用户列表页面
     *
     * 本方法演示最基本的Spring MVC工作流程：
     * 1. 请求 /users/list 到达服务器
     * 2. DispatcherServlet接收请求
     * 3. HandlerMapping根据URL找到此方法（/users/list -> userList）
     * 4. 执行方法，获取用户数据
     * 5. 将数据放入Model，返回视图名
     * 6. ViewResolver解析视图名，找到JSP
     * 7. JSP渲染数据，生成HTML
     *
     * @param model Model对象，用于向视图传递数据
     * @return 视图名 "userList"
     */
    @GetMapping("/list")
    public String userList(Model model) {
        // 调用服务层获取用户列表
        List<User> users = userService.getAllUsers();

        // 将数据添加到Model，视图可以通过EL表达式访问
        model.addAttribute("users", users);
        model.addAttribute("title", "用户列表");

        // 返回视图名（逻辑视图）
        // ViewResolver会将其解析为 /WEB-INF/views/userList.jsp
        return "userList";
    }

    /**
     * 获取用户详情
     *
     * 演示 @PathVariable 注解的使用 - 从URL路径中提取参数
     *
     * URL示例：/users/1
     * - {id} 会匹配 1
     * - @PathVariable("id") Long id 会将1赋值给id参数
     *
     * @param id    用户ID（从URL路径中获取）
     * @param model Model对象
     * @return 视图名
     */
    @GetMapping("/{id}")
    public String getUser(@PathVariable("id") Long id, Model model) {
        // 根据ID查询用户
        User user = userService.getUserById(id);

        if (user == null) {
            // 用户不存在，抛出异常（会被全局异常处理器捕获）
            throw new IllegalArgumentException("用户不存在，ID：" + id);
        }

        // 添加用户到模型
        model.addAttribute("user", user);
        model.addAttribute("title", "用户详情 - " + user.getUsername());

        return "userDetail";
    }

    /**
     * 转向到创建用户页面
     *
     * @param model Model对象
     * @return 视图名
     */
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("title", "创建用户");
        // 返回表单视图
        return "userForm";
    }

    /**
     * 处理创建用户请求
     *
     * 演示 @RequestParam 注解的使用 - 绑定请求参数
     *
     * 表单提实时会发送以下参数：
     * - username: 用户名
     * - password: 密码
     * - email: 邮箱
     * - age: 年龄
     *
     * @RequestParam 注解参数说明：
     * - value/name: 参数名
     * - required: 是否必须（默认true）
     * - defaultValue: 默认值
     *
     * @param username 用户名
     * @param password 密码
     * @param email    邮箱
     * @param age      年龄
     * @param model    Model对象
     * @return 视图名
     */
    @PostMapping("/create")
    public String createUser(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "email", required = false, defaultValue = "") String email,
            @RequestParam(value = "age", required = false, defaultValue = "0") Integer age,
            Model model) {

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setAge(age);

        // 设置一个随机ID（实际应该由数据库生成）
        user.setId(System.currentTimeMillis());

        // 调用服务层创建用户
        boolean success = userService.createUser(user);

        if (success) {
            model.addAttribute("message", "用户创建成功！");
            model.addAttribute("user", user);
            return "success";
        } else {
            model.addAttribute("error", "用户创建失败！");
            return "error";
        }
    }

    /**
     * 使用ModelAndView返回数据和视图
     *
     * ModelAndView是Spring MVC中封装模型数据和视图信息的对象。
     * 优点：可以在方法中同时设置模型数据和视图名
     *
     * @param id 用户ID
     * @return ModelAndView对象
     */
    @GetMapping("/mav/{id}")
    public ModelAndView getUserWithMav(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView();

        // 设置视图名
        mav.setViewName("userDetail");

        // 获取用户
        User user = userService.getUserById(id);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在，ID：" + id);
        }

        // 添加模型数据
        mav.addObject("user", user);
        mav.addObject("title", "用户详情 - " + user.getUsername());

        return mav;
    }

    /**
     * 使用ModelMap传递数据
     *
     * ModelMap是Model接口的实现类，比Model功能更丰富。
     * 可以使用链式调用添加数据。
     *
     * @param username 用户名
     * @param modelMap ModelMap对象
     * @return 视图名
     */
    @GetMapping("/search")
    public String searchUser(
            @RequestParam(value = "username", required = false) String username,
            ModelMap modelMap) {

        if (username != null && !username.isEmpty()) {
            User user = userService.getUserByUsername(username);
            modelMap.addAttribute("user", user);
            modelMap.addAttribute("title", "搜索结果");
        } else {
            modelMap.addAttribute("users", userService.getAllUsers());
            modelMap.addAttribute("title", "用户列表");
        }

        return "userList";
    }

    /**
     * 获取所有用户（返回JSON）
     *
     * 注意：这是一个普通的@Controller方法，不是@RestController
     * 需要使用 @ResponseBody 注解来返回JSON数据
     *
     * @return 用户列表（会被Jackson转换为JSON）
     */
    @GetMapping("/json")
    @ResponseBody
    public List<User> getUsersJson() {
        return userService.getAllUsers();
    }

    /**
     * 获取用户JSON（RESTful风格）
     *
     * @param id 用户ID
     * @return 用户对象
     */
    @GetMapping("/json/{id}")
    @ResponseBody
    public User getUserJson(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在，ID：" + id);
        }
        return user;
    }

    /**
     * 演示request对象的使用
     *
     * 可以直接将HttpServletRequest作为方法参数，
     * Spring MVC会自动注入。
     *
     * @param request HttpServletRequest对象
     * @param model   Model对象
     * @return 视图名
     */
    @GetMapping("/info")
    public String getUserInfo(HttpServletRequest request, Model model) {
        // 获取请求信息
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 添加到模型
        model.addAttribute("contextPath", contextPath);
        model.addAttribute("requestURI", requestURI);
        model.addAttribute("method", method);
        model.addAttribute("title", "请求信息");

        return "userInfo";
    }

    /**
     * 演示Map作为模型数据的载体
     *
     * Spring MVC会自动将Map的内容复制到模型中
     *
     * @return 包含数据的Map
     */
    @GetMapping("/map")
    public Map<String, Object> getUserMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("users", userService.getAllUsers());
        map.put("count", userService.getAllUsers().size());
        map.put("title", "用户Map");
        return map;
    }
}
