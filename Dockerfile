FROM eclipse-temurin:21-jdk

# 安装基础工具
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    git \
    vim \
    unzip \
    procps \
    openssh-server \
    && rm -rf /var/lib/apt/lists/*

# 安装 code-server 和扩展
RUN curl -fsSL https://code-server.dev/install.sh | sh \
    && code-server --install-extension redhat.vscode-yaml \
    && code-server --install-extension cnbcool.cnb-welcome \
    && code-server --install-extension eamodio.gitlens \
    && code-server --install-extension tencent-cloud.coding-copilot \
    && code-server --install-extension vscjava.vscode-java-pack \
    && echo done

# 安装 Maven
ENV MAVEN_VERSION=3.9.9
ENV MAVEN_HOME=/opt/maven
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt \
    && mv /opt/apache-maven-${MAVEN_VERSION} ${MAVEN_HOME} \
    && rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

# 安装 Gradle
ENV GRADLE_VERSION=8.11.1
ENV GRADLE_HOME=/opt/gradle
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && mv /opt/gradle-${GRADLE_VERSION} ${GRADLE_HOME} \
    && rm gradle-${GRADLE_VERSION}-bin.zip

# 将 Maven 和 Gradle 添加到 PATH
ENV PATH=${MAVEN_HOME}/bin:${GRADLE_HOME}/bin:$PATH

# 指定字符集支持命令行输入中文
ENV LANG=C.UTF-8
ENV LANGUAGE=C.UTF-8
