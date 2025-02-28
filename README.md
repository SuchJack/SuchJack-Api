# API开放平台后端
> 项目上线地址：https://api.such-jack.top/
> 演示账号：test001 密码12345678
---
### 项目简介
提供 API接口供开发者调用的平台，基于 Spring Boot 后端 + React 前端的 全栈微服务项目，
管理员可以接入并发布接口、统计分析各接口调用情况;用户可以注册登录并开通接口调用权限、浏览接口、在线调试，还能使用 客户端 SDK轻松在代码中调用接口。

---
### 技术栈
1. 前端：
- React 18
- Ant Design Pro 5.x 脚手架 + Ant Design & Procomponents 组件库
- Umi 4 前端框架
- OpenApi代码生成
2. 后端：
- Java、SpringBoot2.7.x
- MySQL 数据库 + MyBatis-Plus 及 MyBatis X 自动生成
- API 签名认证（Http 调用）
- Spring Boot Starter（SDK 开发）
- Dubbo 分布式（RPC、Nacos）
- Swagger + Knife4j 接口文档生成
- Spring Cloud Gateway 微服务网关
- Hutool、Apache Common Utils、Gson 等工具库

---
### 项目模块介绍

1.  api-backend：后台管理系统
    - 管理员可以发布接口、设置接口的调用数量、设定是否下线接口等功能，以及查看用户使用接口的情况，例如使用次数，错误调用等。

2. api-frontend：用户前台系统
   - 提供一个访问界面，供开发者浏览所有的接口，可以购买或开通接口，并获得一定量的调用次数。

3. api-interface：模拟接口系统
   - 提供各种模拟接口供开发者使用和测试，例如，提供一个随机头像生成接口。

4. api-gateway：API 网关系统
   - 负责接口的流量控制，计费统计，安全防护等功能，提供一致的接口服务质量，和简化 API 的管理工作。

5. api-client-sdk：第三方调用 SDK 系统
   - 提供一个简化的工具包，使得开发者可以更方便地调用接口，例如提供预封装的 HTTP 请求方法、接口调用示例等。

---
### 结构图

![image](https://github.com/user-attachments/assets/f5116952-1613-468f-a5bd-3a312d1dc43e)

 
---
### 部署步骤

1. 克隆一份代码

2. 复制一份application.yml为application-local.yml

3. 修改application-local.yml的配置为你的配置

---

### 版本更新

#### v1.2.0 2024-11-12

优化sdk、interface调用（使用反射机制动态请求转发，利于后续接口版本控制）。

优化结构：重构整个项目，优化项目目录、pom父子工程依赖版本控制等。

配置优化：优化dubbo依赖、配置。

抽取重复代码，提取成工具类放在common模块中。

#### v1.1.0 2024-8-25

优化项目文件架构：改为聚合模块的微服务模式。

优化调用接口：将分开的POST、GET接口改为统一的调用接口，优化请求格式校验。

新增前端界面：添加用户管理模块，且对后端用户管理的CRUD做优化调整。

#### v1.0.0 2024-8-23

学习+动手工期约2周：2024-8-5~2024-8-15

优化了前端页面：去除页面水印、美化前端页面

优化了SDK接口的调用逻辑：前端传入接口id后->后端数据库进行查找url->将url、ak/sk传入sdk进行封装请求->转发给网关校验->网关调用interface接口。

优化请求：区分GET、POST请求的调用，分别走不同的方法调用。

---

#### // TODO LIST
1. 接口设计：需要设计清晰易用的 API 接口，并且提供详细的接口文档，以方便开发者使用。
2. 性能和可用性：平台需要承载大量的接口请求，因此需要考虑到性能和可用性问题。例如，设计高效的数据存储和检索策略，确保 API 网关的高性能等。
3. 安全：平台需要防止各种安全攻击，例如 DDOS 攻击，也需要保护用户的隐私和数据安全。
4. 计费和流量控制：需要设计合理的计费策略和流量控制机制，以确保平台的稳定运行和收入来源。
5. 易用性和用户体验：需要为开发者提供简单易用的接口调用工具和友好的用户界面，提供优质的用户体验。

---

> 参考资料：
>
> 学习：https://www.codefather.cn/course/1790979723916521474
>
> 部署：https://pidanxia.ink/api-deploy/
