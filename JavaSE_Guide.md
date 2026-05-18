# Java SE 核心知识点总结

## 1. 基本语法

### 1.1 变量与数据类型

```java
// 基本类型（8种）
byte    b = 1;      // 1字节 -128~127
short   s = 1;      // 2字节
int     i = 1;      // 4字节（默认）
long    l = 1L;     // 8字节
float   f = 1.0f;   // 4字节
double  d = 1.0;    // 8字节（默认）
char    c = 'A';    // 2字节 Unicode
boolean bo = true;  // 1位

// 引用类型
String str = "Hello";  // 字符串
int[] arr = {1, 2, 3}; // 数组
```

### 1.2 判断与循环

```java
// if-else
if (score >= 90) {
    grade = "A";
} else if (score >= 60) {
    grade = "B";
} else {
    grade = "C";
}

// switch（支持 String、enum、byte-short-int-char）
switch (day) {
    case 1:
    case 2:
        weekday = "Mon/Tue";
        break;
    default:
        weekday = "Unknown";
}

// for 循环
for (int i = 0; i < 10; i++) {
    sum += i;
}

// 增强 for（遍历数组/集合）
for (int item : arr) {
    System.out.println(item);
}

// while
while (condition) {
    // do something
}
```

### 1.3 方法

```java
// 语法：访问修饰符 返回类型 方法名(参数列表)
// 方法重载：同名不同参（参数个数、类型、顺序不同）
public int add(int a, int b) {
    return a + b;
}

public double add(double a, double b) {
    return a + b;
}
```

---

## 2. 面向对象

### 2.1 类与对象

```java
public class Person {
    // 属性（字段）
    private String name;
    private int age;

    // 构造方法（可重载）
    public Person() {}

    public Person(String name, int age) {
        this.name = name;  // this 指向当前对象
        this.age = age;
    }

    // Getter/Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // 方法
    public void say() {
        System.out.println("I'm " + name);
    }
}

// 创建对象
Person p = new Person("张三", 25);
```

### 2.2 继承

```java
// 单继承（Java 不支持多继承）
public class Student extends Person {
    private String school;

    public Student(String name, int age, String school) {
        super(name, age);  // 调用父类构造
        this.school = school;
    }

    @Override  // 重写父类方法
    public void say() {
        super.say();  // 调用父类实现
        System.out.println("I study at " + school);
    }
}
```

### 2.3 多态

```java
// 父类引用指向子类对象（向上转型）
Person p = new Student("李四", 20, "北大");

// 运行时绑定方法（动态分派）
p.say();  // 调用 Student 的 say()

// 向下转型（需要强制转换）
if (p instanceof Student) {
    Student s = (Student) p;
    s.getSchool();
}
```

### 2.4 接口

```java
// 接口：抽象行为的规范
public interface Flyable {
    int MAX_SPEED = 1000;  // 常量（隐式 static final）

    void fly();  // 抽象方法（隐式 public abstract）
}

// 类实现接口
public class Bird implements Flyable {
    @Override
    public void fly() {
        System.out.println("Bird flies");
    }
}

// JDK8+：默认方法与静态方法
public interface Drawable {
    default void draw() {
        System.out.println("Drawing...");
    }

    static void print() {
        System.out.println("Print");
    }
}

// JDK9+：私有方法（接口内部共享代码）
public interface Process {
    private void init() { /* 初始化代码 */ }
    private void cleanup() { /* 清理代码 */ }

    default void process() {
        init();
        // 业务逻辑
        cleanup();
    }
}
```

### 2.5 访问修饰符

| 修饰符       | 本类   | 同包   | 子类   | 任意位置 |
| ---------- | ---- | ---- | ---- | ----- |
| private    | ✓    |      |      |       |
| 默认（default）| ✓    | ✓    |      |       |
| protected  | ✓    | ✓    | ✓    |       |
| public     | ✓    | ✓    | ✓    | ✓     |

---

## 3. 集合框架

```
Collection (interface)
├── List (interface) - 有序、可重复
│   ├── ArrayList     - 数组实现，随机访问 O(1)
│   ├── LinkedList    - 链表实现，插入删除 O(1)
│   └── Vector        - 同步（已过时）
│       └── Stack     - 栈
├── Set (interface) - 无序、去重
│   ├── HashSet       - 哈希表，O(1) 查找
│   ├── LinkedHashSet - 保持插入顺序
│   └── TreeSet       - 红黑树，有序
│
Map (interface) - 键值对
├── HashMap       - O(1) 查找（允许 null 键值）
├── LinkedHashMap - 保持插入顺序
├── TreeMap       - 红黑树，按 key 排序
└── Hashtable     - 同步（已过时，不允许 null）
```

### 3.1 List

```java
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.get(0);         // 获取元素
list.remove(0);      // 按索引删除
list.contains("A");  // 是否包含

// 遍历
for (String s : list) { System.out.println(s); }
list.forEach(s -> System.out.println(s));
```

### 3.2 Set

```java
Set<String> set = new HashSet<>();
set.add("A");
set.add("A");  // 重复，不会加入
set.size();    // 1

// 判断是否包含
set.contains("A");  // true
```

### 3.3 Map

```java
Map<String, Integer> map = new HashMap<>();
map.put("语文", 90);
map.put("数学", 95);

map.get("语文");         // 90
map.containsKey("数学");  // true
map.containsValue(95);   // true

// 遍历
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}

map.forEach((k, v) -> System.out.println(k + ": " + v));
```

### 3.4 工具类

```java
// Collections（操作集合）
Collections.sort(list);
Collections.reverse(list);
Collections.shuffle(list);
Collections.binarySearch(list, "目标");  // 二分查找（需先排序）

// Arrays（操作数组）
Arrays.sort(arr);
Arrays.binarySearch(arr, 5);
Arrays.copyOf(arr, newLength);
Arrays.asList(1, 2, 3);  // 数组转 List
```

---

## 4. 异常处理

### 4.1 异常体系

```
Throwable
├── Error（系统级错误，如 OutOfMemoryError）— 不捕获
└── Exception
    ├── RuntimeException（运行时异常）— 可不处理
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   └── ClassCastException
    └── 受检异常（Checked Exception）— 必须处理
        ├── IOException
        └── SQLException
```

### 4.2 try-catch-finally

```java
try {
    // 可能抛出异常的代码
    int result = 10 / 0;
} catch (ArithmeticException e) {
    // 捕获特定异常
    System.out.println("除数不能为零");
} catch (Exception e) {
    // 捕获所有异常（子在前，父在后）
    e.printStackTrace();
} finally {
    // 无论如何都会执行（即使 return）
    // 常用于关闭资源
    System.out.println("总是执行");
}
```

### 4.3 throw 与 throws

```java
// throw：抛出异常实例
public void withdraw(double amount) {
    if (amount > balance) {
        throw new IllegalArgumentException("余额不足");
    }
}

// throws：声明方法可能抛出的异常（方法签名的一部分）
public void readFile(String path) throws IOException {
    // 方法内部可以不处理，交给调用者
    FileReader reader = new FileReader(path);
}
```

### 4.4 自定义异常

```java
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    // 可以添加额外字段和构造器
    private double deficit;

    public InsufficientBalanceException(String message, double deficit) {
        super(message);
        this.deficit = deficit;
    }
}
```

---

## 5. 泛型

### 5.1 泛型类

```java
// 类型参数 T（可以多个：T, E, K, V）
public class Box<T> {
    private T content;

    public T getContent() { return content; }
    public void setContent(T content) { this.content = content; }
}

Box<String> stringBox = new Box<>();
stringBox.setContent("Hello");
String content = stringBox.getContent();  // 无需强制转换
```

### 5.2 泛型方法

```java
public static <T> T getFirst(T[] array) {
    if (array == null || array.length == 0) {
        return null;
    }
    return array[0];
}

String first = getFirst(new String[]{"A", "B"});
```

### 5.3 泛型约束

```java
// 上界：T 必须是 Number 或其子类
public class NumberBox<T extends Number> {
    private T value;
}

// 下界：? 必须是 Integer 或其父类
void addNumbers(List<? super Integer> list) {
    // 可以添加 Integer 及其子类
    list.add(1);
    list.add(2);
}

// 上界读取：? extends Number 表示可读 Number
void printNumbers(List<? extends Number> list) {
    for (Number n : list) {
        System.out.println(n);
    }
    // 不能添加（除了 null）
}
```

---

## 6. IO / NIO

### 6.1 传统 IO

```java
// 字节流
InputStream  is = new FileInputStream("file.txt");
OutputStream os = new FileOutputStream("output.txt");

// 字符流（处理文本）
Reader reader = new FileReader("file.txt");
Writer writer = new FileWriter("output.txt");

// 缓冲流（提升性能）
BufferedReader br = new BufferedReader(new FileReader("file.txt"));
BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));

String line;
while ((line = br.readLine()) != null) {  // 逐行读取
    bw.write(line);
    bw.newLine();
}

// 关闭资源（JDK7+ 自动资源管理）
try (FileReader fr = new FileReader("file.txt")) {
    // 使用 fr
} catch (IOException e) {
    e.printStackTrace();
}
```

### 6.2 NIO（New IO）

```java
// 核心概念：Channel、Buffer、Selector
Path path = Paths.get("file.txt");

// 读取文件
String content = Files.readString(path);

// 写入文件
Files.writeString(path, "Hello NIO");

// 复制文件
Files.copy(source, target);

// 遍历目录
try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
    for (Path entry : stream) {
        System.out.println(entry.getFileName());
    }
}
```

---

## 7. 多线程

### 7.1 创建线程

```java
// 方式1：继承 Thread
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running");
    }
}
new MyThread().start();

// 方式2：实现 Runnable（更灵活）
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running");
    }
}
new Thread(new MyRunnable()).start();

// 方式3：Lambda（JDK8+）
new Thread(() -> System.out.println("Lambda thread")).start();

// 方式4：Callable + Future（可返回结果）
ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Integer> future = executor.submit(() -> {
    Thread.sleep(1000);
    return 42;
});
System.out.println(future.get());  // 阻塞等待结果
```

### 7.2 线程同步

```java
// synchronized 关键字
public class Counter {
    private int count = 0;

    // 同步方法（锁定 this）
    public synchronized void increment() {
        count++;
    }

    // 同步代码块（更细粒度）
    public void decrement() {
        synchronized (this) {
            count--;
        }
    }
}

// ReentrantLock（可重入锁）
private final ReentrantLock lock = new ReentrantLock();

public void increment() {
    lock.lock();
    try {
        count++;
    } finally {
        lock.unlock();  // 必须在 finally 中释放
    }
}

// volatile（保证可见性，不保证原子性）
private volatile boolean flag = false;
```

### 7.3 线程池

```java
// Executors 工厂方法创建线程池
ExecutorService executor = Executors.newFixedThreadPool(4);

// 提交任务
executor.submit(() -> {
    // 执行任务
});

// 关闭线程池
executor.shutdown();      // 等待任务完成
executor.shutdownNow();    // 立即停止

// 常用线程池类型
// newFixedThreadPool - 固定大小
// newCachedThreadPool - 可伸缩
// newSingleThreadExecutor - 单线程
// newScheduledThreadPool - 定时任务

// 推荐：ThreadPoolExecutor（更灵活）
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    2,                      // 核心线程数
    4,                      // 最大线程数
    60, TimeUnit.SECONDS,  // 空闲线程存活时间
    new LinkedBlockingQueue<>(100)  // 任务队列
);
```

### 7.4 线程协作

```java
// wait/notify（Object 方法）
synchronized (obj) {
    while (condition) {
        obj.wait();  // 释放锁，等待通知
    }
    // 处理
    obj.notify();    // 唤醒一个等待线程
    obj.notifyAll(); // 唤醒所有等待线程
}

// Condition（配合 Lock）
private final Lock lock = new ReentrantLock();
private final Condition cond = lock.newCondition();

lock.lock();
try {
    while (!condition) {
        cond.await();
    }
    // 处理
    cond.signal();
} finally {
    lock.unlock();
}

// CountDownLatch（倒数计数器）
CountDownLatch latch = new CountDownLatch(3);
latch.countDown();  // 减一
latch.await();       // 等待倒数到零

// CyclicBarrier（屏障）
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    // 所有线程到达后执行
});
barrier.await();  // 等待其他线程
```

---

## 8. Lambda / Stream

### 8.1 Lambda 表达式

```java
// 语法：(参数) -> { 方法体 }
// 省略规则：参数类型、return、括号

// 无参数
() -> System.out.println("Hello")

// 单参数（可省略括号）
x -> x * 2

// 多参数
(x, y) -> x + y

// 多行方法体
(x, y) -> {
    int sum = x + y;
    return sum;
}

// 函数式接口：只有一个抽象方法的接口
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

Calculator add = (a, b) -> a + b;
Calculator multiply = (a, b) -> a * b;
```

### 8.2 常用函数式接口

| 接口            | 方法         | 用途      |
| ------------- | ---------- | ------- |
| `Predicate<T>` | `test(T)`   | 条件判断    |
| `Function<T,R>`| `apply(T)`  | 转换      |
| `Consumer<T>`  | `accept(T)` | 消费（无返回） |
| `Supplier<T>`  | `get()`     | 生产      |
| `UnaryOperator` | `apply(T)` | 一元运算   |
| `BinaryOperator`| `apply(T,T)`| 二元运算   |

### 8.3 Stream

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// 创建 Stream
Stream.of(1, 2, 3);
list.stream();
Arrays.stream(arr);

// 中间操作（返回 Stream）
.filter(n -> n % 2 == 0)    // 过滤
.map(n -> n * 2)            // 转换
.flatMap(list -> list.stream())  // 扁平化
.distinct()                 // 去重
.sorted()                   // 排序
.limit(3)                   // 限制数量
.skip(2)                    // 跳过

// 终端操作（返回结果）
.collect(Collectors.toList())  // 收集为 List
.count()                         // 计数
.forEach(System.out::println)   // 遍历
.reduce(0, Integer::sum)         // 归约
.max(Integer::compareTo)        // 最大值
.min(Integer::compareTo)         // 最小值
.findFirst()                     // 第一个
.findAny()                       // 任意一个
.anyMatch(n -> n > 3)            // 任意匹配
.allMatch(n -> n > 0)            // 全部匹配
.noneMatch(n -> n < 0)           // 都不匹配

// 示例
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
List<String> result = names.stream()
    .filter(name -> name.length() > 3)
    .map(String::toUpperCase)      // 方法引用
    .sorted()
    .collect(Collectors.toList()); // [ALICE, CHARLIE]
```

---

## 9. 反射

### 9.1 获取 Class 对象

```java
// 三种方式
Class<?> c1 = MyClass.class;
Class<?> c2 = obj.getClass();
Class<?> c3 = Class.forName("com.example.MyClass");
```

### 9.2 操作类成员

```java
Class<?> clazz = MyClass.class;

// 获取构造方法
Constructor<?>[] constructors = clazz.getConstructors();
Constructor<?> constructor = clazz.getConstructor(String.class, int.class);
Object instance = constructor.newInstance("张三", 25);

// 获取方法
Method[] methods = clazz.getMethods();        // 包括继承的
Method method = clazz.getMethod("methodName", int.class);
method.invoke(instance, 10);                  // 调用方法

// 获取字段
Field[] fields = clazz.getDeclaredFields();   // 包括私有的
Field field = clazz.getDeclaredField("fieldName");
field.setAccessible(true);                     // 可访问私有成员
Object value = field.get(instance);
field.set(instance, "新值");

// 获取父类/接口
Class<?> superClass = clazz.getSuperclass();
Class<?>[] interfaces = clazz.getInterfaces();
```

### 9.3 实际应用场景

```java
// Spring：自动注入
@Autowired
private ObjectMapper objectMapper;  // 通过类型+字段名反射注入

// MyBatis：结果映射
ResultSet rs = stmt.executeQuery();
ResultSetMetaData metaData = rs.getMetaData();
String columnName = metaData.getColumnLabel(1);

// JUnit：测试框架
@Test
public void testMethod() {
    // 反射调用 @Before/@After 方法
}
```

---

## 10. 注解

### 10.1 内置注解

```java
@Override       // 编译期检查是否重写
@Deprecated     // 标记已过时
@SuppressWarnings("unchecked")  // 抑制警告
@FunctionalInterface  // 函数式接口检查
```

### 10.2 元注解（注解的注解）

```java
@Target(ElementType.METHOD)    // 作用目标：方法
@Retention(RetentionPolicy.RUNTIME)  // 保留策略
@Documented                      // 包含在 Javadoc 中
@Inherited                       // 可被继承
```

### 10.3 自定义注解

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    String value() default "默认值";  // 普通属性

    int[] types() default {};        // 数组属性

    Class<?> clazz() default Void.class;  // Class 属性
}

// 使用
@MyAnnotation(value = "test", types = {1, 2})
public void myMethod() {}
```

### 10.4 注解处理器（结合反射）

```java
public class AnnotationProcessor {

    public static void process(Class<?> clazz) throws Exception {
        // 检查类上的注解
        if (clazz.isAnnotationPresent(MyAnnotation.class)) {
            MyAnnotation annotation = clazz.getAnnotation(MyAnnotation.class);
            System.out.println("Value: " + annotation.value());
        }

        // 检查方法上的注解
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MyAnnotation.class)) {
                MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
                System.out.println("Method: " + method.getName());
            }
        }
    }
}
```

---

## 11. JVM

### 11.1 内存区域

```
线程共享区：
├── 堆（Heap） - 对象实例、数组（GC 主要管理区域）
│   ├── 新生代（Eden + Survivor）
│   └── 老年代（Old Generation）
└── 方法区（Metaspace/JDK8+） - 类信息、常量、静态变量

线程私有区：
├── 虚拟机栈（Stack） - 方法调用栈帧
│   └── 栈帧：局部变量表、操作数栈、动态链接、返回地址
├── 本地方法栈（Native Stack） - native 方法
└── 程序计数器（PC Register） - 当前指令地址
```

### 11.2 垃圾回收（GC）

```java
// 触发 GC
System.gc();  // 建议 JVM 触发 GC，不保证立即执行

// 手动解除引用
Object obj = new Object();
obj = null;  // 断开引用
System.gc();

// 引用类型
import java.lang.ref.*;
WeakReference<Object> wr = new WeakReference<>(new Object());  // 弱引用
SoftReference<Object> sr = new SoftReference<>(new Object());  // 软引用（内存不足时回收）
```

### 11.3 GC 算法

| 算法        | 原理                    | 优点         | 缺点         |
| --------- | --------------------- | ---------- | ---------- |
| 标记-清除     | 标记存活对象，清除未标记对象     | 简单          | 产生内存碎片    |
| 复制        | 将存活对象复制到另一半空间      | 无碎片         | 内存利用率低    |
| 标记-整理     | 标记后整理内存碎片          | 无碎片         | 效率相对较低    |
| 分代收集     | 新生代（复制） + 老年代（标记-整理） | 效率高         | 实现复杂       |

### 11.4 常用 JVM 参数

```bash
# 堆大小
-Xms256m          # 初始堆大小
-Xmx512m          # 最大堆大小
-Xmn128m          # 新生代大小

# 垃圾收集器
-XX:+UseG1GC      # 使用 G1 收集器
-XX:+UseParallelGC  # 使用并行收集器

# 其他
-XX:+PrintGCDetails  # 打印 GC 详情
-XshowSettings:vm     # 显示 JVM 设置
```

### 11.5 类加载机制

```
加载 → 验证 → 准备 → 解析 → 初始化 → 使用 → 卸载

加载器层级：
Bootstrap ClassLoader（C++ 实现）
    ↓
Extension ClassLoader（Java 实现）
    ↓
Application ClassLoader（Java 实现）
    ↓
自定义 ClassLoader

双亲委派模型：类加载请求向上传递直到父加载器，只有父加载器找不到时才自己加载
```

---

## 学习建议

1. **循序渐进**：先掌握基础语法和 OOP，再深入集合、异常，最后学习并发和 JVM
2. **多写代码**：每个知识点都要动手实践，不要只看不做
3. **阅读源码**：学习 Java 源码（ArrayList、HashMap 等）
4. **理解原理**：理解背后的设计思想，而不仅仅是 API 用法
5. **项目实践**：通过实际项目巩固知识，如实现一个简单的 CRUD 应用
