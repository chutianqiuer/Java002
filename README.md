# Cloud Native Java 21 Development Environment

## Usage

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

## Included Software:

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

## Java Development Tools:

- Maven 3.9.9
- Gradle 8.11.1
- Eclipse Temurin JDK 21
- Java Language Server
- Java Test Runner
- Java Debugger
- Maven for Java
- Gradle for Java
- Java Project Manager
- Java IntelliCode

## Included VS Code Extensions:

- vscjava.vscode-java-pack
- redhat.vscode-yaml
- cnbcool.cnb-welcome
- tencent-cloud.coding-copilot

## Environment Variables:

- MAVEN_VERSION: 3.9.9
- MAVEN_HOME: /opt/maven
- GRADLE_VERSION: 8.11.1
- GRADLE_HOME: /opt/gradle
- PATH: /opt/maven/bin:/opt/gradle/bin:$PATH
- LANG: C.UTF-8
- LANGUAGE: C.UTF-8

## Features:

- Based on Eclipse Temurin JDK 21, providing long-term support Java runtime
- Supports both Maven and Gradle build tools
- Integrated VS Code development environment for cloud-based development
- Pre-installed Tencent Cloud Coding Copilot for AI-assisted programming
- Complete Java development toolchain, ready to use out of the box
- UTF-8 character set support for international development

