# 轻量快速的JAVA EE开发框架——light ssh #

**lightssh 是灵活的、通用的、可扩展的J2EE开源框架。大势所趋，lightssh不是非主流，它集成了Spring、Hibernate、Struts2、Shiro、Quartz、SLF4J、CXF等最棒的开源组件。同时，自身附带了实际应用中很多常用功能。**

#### 功能特征 ####
  * **安全** 安全框架选用了Shiro，基于表单认证扩展实现了验证码。并集成实现了单点登录。
  * **日志** 从Log4J改为SLF4J，因实际应用中Log4J存在一些问题。数据库日志选用log4jdbc-remix，可打印出异常SQL语句。对Action、SQL执行超时进行了日志输出。
  * **持久化** 目前主要实现基于关系数据库的存储，使用JPA注解，选用Hibernate实现。选用JPA，还可以选用OpenJPA、TopLink等实现。
  * **事务** Spring申明式配置，只需要规范业务实现类命令即可实现。
  * **Web Service** 集成CXF，SOAP方式。
  * **自定义标签** 分页标签，可排序、动态显示列的表格标签等。
  * **数据校验** 前端页面数据校验由jQuery validate实现，后端由Struts2实现。
  * **页面布局** 由Sitemesh实现。
  * **定时任务** 选用Quartz实现。
  * **多样式** 可实现多种界面风格切换。
  * **国际化** 可支持多种言语。