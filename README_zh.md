# 云原生 Java 21 开发环境

## 使用方法

```yaml
$:
  vscode:
    - docker:
        image: docker.cnb.cool/examples/language/java-21
      services:
        - vscode
        - docker
      stages:
        - name: ls
          script: ls
```

## 包含的软件：

- Java 21 (Eclipse Temurin)
- Maven 3.9.9
- Gradle 8.11.1
- code-server
- curl
- wget
- git
- vim
- unzip
- procps
- openssh-server

## Java 开发工具：

- Maven 3.9.9
- Gradle 8.11.1
- Eclipse Temurin JDK 21
- Java 语言服务器
- Java 测试运行器
- Java 调试器
- Maven for Java
- Gradle for Java
- Java 项目管理器
- Java IntelliCode

## 包含的 VS Code 扩展：

- vscjava.vscode-java-pack
- redhat.vscode-yaml
- cnbcool.cnb-welcome
- tencent-cloud.coding-copilot

## 环境变量：

- MAVEN_VERSION: 3.9.9
- MAVEN_HOME: /opt/maven
- GRADLE_VERSION: 8.11.1
- GRADLE_HOME: /opt/gradle
- PATH: /opt/maven/bin:/opt/gradle/bin:$PATH
- LANG: C.UTF-8
- LANGUAGE: C.UTF-8

## 特性：

- 基于 Eclipse Temurin JDK 21，提供长期支持的 Java 运行环境
- 同时支持 Maven 和 Gradle 两种主流构建工具
- 集成 VS Code 开发环境，支持云端开发
- 预装腾讯云 Coding Copilot，提供 AI 辅助编程
- 完整的 Java 开发工具链，开箱即用
- UTF-8 字符集支持，完美支持中文开发

