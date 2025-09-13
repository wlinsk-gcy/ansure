# Ansure AI Q&A Platform

采用 Spring Boot 3.4.7 构建的答题和AI聊天平台，具有人工智能驱动的问题生成、实时流式聊天和灵活的评分策略。

## 🚀 快速开始

### 先决条件

- **Java 21** or higher
- **Maven 3.6+** 
- **MySQL 8.0+**
- **Redis 5.0+**

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd ansure
   ```

2. **数据库设置**
   ```bash
   # Create database and import schema
   mysql -u root -p < sql/init.sql
   ```

3. **配置文件**
   
   修改当前配置文件: `application-dev.properties`

   **Database Configuration:**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/wlinsk-ai-aq?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password
   ```

   **Redis Configuration:**
   ```properties
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   spring.redisson.single.host=localhost
   spring.redisson.single.port=6379
   ```

   **AI Configuration (Silicon Flow or Other Providers):**
   ```properties
   spring.ai.openai.api-key=your_api_key
   spring.ai.openai.base-url=https://api.siliconflow.cn
   spring.ai.openai.chat.options.model=Qwen/Qwen3-32B
   ```

   **File Storage (Aliyun OSS):**
   ```properties
   aliyun.oss.endpoint=your_oss_endpoint
   aliyun.oss.accessKeyId=your_access_key_id
   aliyun.oss.accessKeySecret=your_access_key_secret
   aliyun.oss.bucketName=your_bucket_name
   ```
   
   **Email Server (163.com):**
   ```properties
   email.host=smtp.163.com
   email.port=465
   email.smtpCode=your_smtp_code
   email.smtpEmail=your_smtp_email
   ```

4. **运行服务**
   ```bash
   # Development mode
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   
   # Or build and run
   mvn clean package
   java -jar target/ansure-1.0.0.jar --spring.profiles.active=local
   ```

5. **访问服务**
   - API Base URL: `http://localhost:8080/api`
   - API Doc Json: `http://localhost:8080/api/v3/api-docs`
   - Swagger UI: `http://localhost:8080/api/swagger-ui.html` 在搜索框输入：`/api/v3/api-docs`

## 📋 特点

### 核心功能
- **AI聊天助手**：与OpenAI兼容模型进行实时流式聊天
- **灵感创建题库**: 支持自定义题库创建与AI实时生成题库
- **用户管理**: 使用 [管理员 / 用户 / 游客] 多角色进行基于角色的访问控制
- **支持第三方登录**: OAuth 集成 (Gitee,...)
- **文件管理**: 通过阿里云 OSS 上传和管理

### 技术特点
- **Spring AI 集成**: 利用 Spring AI 框架进行 LLM 交互
- **工具调用**: AI可以通过工具调用应用功能
- **内存管理**: Redis 支持的对话上下文聊天内存
- **实时交流**: 用于流式处理响应的服务器发送事件 （SSE）
- **分布式锁**: 用于分布式锁的 Redisson
- **API 文档**: 交互式 API 文档 Knife4j

## 🏗️ 基础架构

### 项目结构
```
src/main/java/com/wlinsk/ansure/
├── basic/                   # 基础架构包
│   ├── config/              # 配置类
│   ├── enums/               # 枚举类
│   ├── exception/           # 异常处理器
│   ├── utils/               # 工具包
│   └── handler/             # 自定义处理器
├── controller/              # API接口
│   ├── user/                # C端接口
│   ├── manager/             # B端接口
│   └── base/                # 公共接口
├── service/                 # 业务层
│   ├── user/                # C端业务层
│   ├── manage/              # B端业务层
│   └── scoring_handler/     # 评分策略工厂
├── model/                   # 实体包
│   ├── entity/              # 数据库实体
│   ├── dto/                 # 序列化实体
│   ├── vo/                  # 视图实体
│   └── bo/                  # 业务实体
├── mapper/                  # 持久层
└── tools/                   # AI工具包
```

### 数据库表结构
当前项目使用5张主表:
- `tb_user` - 用户表
- `tb_app` - 应用表
- `tb_question` - 题库表
- `tb_scoring_result` - 评分策略表
- `tb_user_answer_record` - 答题记录表
- `tb_chat_session` - 聊天会话表

## 🔧 配置指南

### 环境变量
- `dev` - 开发环境 (default)
- `test` - 测试环境
- `prod` - 生产环境

### 所需中间件

#### 1. MySQL
```sql
CREATE DATABASE `wlinsk-ai-aq` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

#### 2. Redis
在默认端口 6379 上启动 Redis 或相应地更新配置。

#### 3. AI 大模型集成
当前项目使用 Silicon Flow 作为 AI 提供商。你也可以集成其他供应商。
你需要：
- Silicon Flow API key
- Access to Qwen/Qwen3-32B model

### 其他服务

#### 1. Aliyun OSS (文件管理)
文件上传功能所需：
- 具有公共读取权限的 OSS bucket
- 有效的OSS Access key

#### 2. Email Service (邮件服务)
邮件发送功能所需：
- 有效的smtpCode
- 已授权的smtpEmail

#### 3. 第三方登录集成
暂时仅支持Gitee，所需配置：
- Gitee的有效client-id
- Gitee的有效clientSecret

## 🛠️ 开发

### 构建命令
```bash
# Compile
mvn compile

# Run tests
mvn test

# Package
mvn package

# Clean and install
mvn clean install

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### API 测试
- Swagger UI: `http://localhost:8080/api/doc.html`
- OpenAPI Docs: `http://localhost:8080/api/v3/api-docs`

### 日志
- 在dev以外的环境，日志会存储在 `./logs/` 目录中
- 可根据配置 `wlinsk.log.path` 修改日志存储目录

## 📝 API 概述

### 关键接口

**聊天服务:**
- `POST /api/chat/do` - 发送消息 (SSE streaming)
- `POST /api/chat/stop/{sessionId}` - 停止聊天

**用户管理:**
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/profile` - 获取用户头像

**答题服务:**
- `GET /api/app/list` - 应用列表
- `POST /api/app/create` - 创建应用
- `POST /api/question/add` - 配置题目
- `POST /api/answer/submit` - 用户答题

**文件管理:**
- `POST /api/file/upload` - 文件上传

## 🔒 安全策略

### 认证鉴权
- 自定义拦截器实现认证鉴权
- 核心角色为： (USER, ADMIN, BAN)
- 支持Oauth协议的第三方登录


## 📚 其他资源

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [MyBatis Plus Documentation](https://baomidou.com/)
- [Knife4j Documentation](https://doc.xiaominfo.com/)