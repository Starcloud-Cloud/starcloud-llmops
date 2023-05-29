## 平台简介

**starcloud-llmops**，打造中国第一流的大语言模型运营开发平台。

> 有任何问题，或者想要的功能，可以在 _Issues_ 中提。
>
> 😜 给项目点点 Star 吧，这对我们真的很重要！

![架构图]()

* 核心功能使用Java开发，避免学习理解python体系
* 完整符合LLM应用开发标准模型开发。实现主流LLM开发流程链路功能
* 后端采用 Spring Boot 多模块架构、MySQL + MyBatis Plus、Redis + Redisson
* 权限认证使用 Spring Security & Token & Redis，支持多终端、多种用户的认证系统，支持 SSO 单点登录
* 支持加载动态权限菜单，按钮级别权限控制，本地缓存提升性能
* 支持 SaaS 多租户，可自定义每个租户的权限，提供透明化的多租户底层封装
* 集成阿里云、腾讯云等短信渠道，集成 MinIO、阿里云、腾讯云、七牛云等云存储服务

##  项目关系

![项目大图]()


### 后端项目


| 项目                    | Star | 简介                          |
|-----------------------|------|-----------------------------|
| [starcloud-llmops](1) | star | 基于 Spring Boot 的大语言模型运营开发平台 |

### 前端项目

| 项目                             | Star | 简介                            |
|--------------------------------|------|-------------------------------|
| [starcloud-llmops-ui-admin](1) | star | 基于 Vue3 + element-plus 实现的运营管理后台 |
| [starcloud-llmops-ui-user](1)  | star | 基于 React + MaterialUI 实现的用户后台 |
