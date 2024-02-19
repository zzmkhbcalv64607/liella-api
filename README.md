
<p align="center">
    <img src=https://liellaliyuu.oss-cn-chengdu.aliyuncs.com/article/227858c049dcacc89870aa672b2c13a5.jpg width=188/>
</p>
<h1 align="center">liella-API 管理系统</h1>
<p align="center"><strong>liella-API 管理系统是一个为用户和开发者提供全面API接口调用服务的系统 🛠</strong></p>


## 项目介绍 🙋



**😀 作为用户您可以通过注册登录账户，获取接口调用权限，并根据自己的需求浏览和选择适合的接口。您可以在线进行接口调试，快速验证接口的功能和效果。** 

**🤝 您可以将自己的接口接入到liella-API 管理系统上，并发布给其他用户使用。 您可以管理和各个接口，以便更好地分析和优化接口性能。** 


 **🏁 无论您是用户还是开发者，liella-API 管理系统都致力于提供稳定、安全、高效的接口调用服务，帮助您实现更快速、便捷的开发和调用体验。**




## 目录结构 📑


| 目录                                                     | 描述               |
|--------------------------------------------------------| ------------------ |
| **🏘️ liella-backend**             | liella-API后端服务模块 |
| **🏘️ liella-common**               | 公共服务模块       |
| **🕸️ liella-gateway**             | 网关模块           |
| **🔗 liella-interface**          | 接口模块           |
| **🛠 liella-client-sdk** | 开发者调用sdk      |

## 项目选型 🎯

### **后端**

- Spring Boot 2.7.0
- Spring MVC
- MySQL 数据库
- Dubbo 分布式（RPC、Nacos）
- Spring Cloud Gateway 微服务网关
- API 签名认证（Http 调用）
- Swagger + Knife4j 接口文档
- Spring Boot Starter（SDK 开发）
- Apache Commons Lang3 工具类
- MyBatis-Plus 及 MyBatis X 自动生成
- Hutool、Apache Common Utils、Gson 等工具库

### 前端

- React 18

- Ant Design Pro 5.x 脚手架

- Ant Design & Procomponents 组件库

- Umi 4 前端框架

- OpenAPI 前端代码生成

## 项目架构图

![](https://liellaliyuu.oss-cn-chengdu.aliyuncs.com/article/3fb2eca7193d2dacbc136f63b2f2ded5.png)
## 页面展示

![](https://liellaliyuu.oss-cn-chengdu.aliyuncs.com/article/7a63e36a3261507d4ac060809e620d25.png)
![](https://liellaliyuu.oss-cn-chengdu.aliyuncs.com/article/0c445ecd924f9e33528fc7d5485de1e4.png)
![](https://liellaliyuu.oss-cn-chengdu.aliyuncs.com/article/18f8c9b987c5616038e2460e746f9769.png)
![](https://liellaliyuu.oss-cn-chengdu.aliyuncs.com/article/51c59b981bb7f517407c18c4fed9474d.png)
![](https://liellaliyuu.oss-cn-chengdu.aliyuncs.com/article/0f34ade84f8dc1f36a00109a6074a218.png)
![](https://liellaliyuu.oss-cn-chengdu.aliyuncs.com/article/5a17225e61122a64a93805bf0d6dad8a.png)





## 亮点

### API签名认证
我们在调用接口的时候不能是所有的人都可以调用，只有拥有指定签名的人再能调用，就像是当管理员执行删除操作的时候，后端需要检测这个用户是不是管理员。
因此，要想拥有这个签名第一步就是得签发签名，第二步则是使用签名或者是校验签名。
综上所述，其优点有两个：
（1）保证了安全性
（2）适用于无需保存登录的场景。只认签名，不关注登录状态。
## 使用BitMap实现签到与统计
最初在想到制作签到与统计的时候想到的方法是使用Mysql，但是如果使用Mysql进行的签到与统计时会出现一个问题，每一次签到都会产生一条记录，作为自己的学习项目的话，影响不会特别大，但是如果上线或者其他地方的话这种方式会很消耗内存的。在查询各方资料后，最终选择使用redis的BitMap来实现签到功能。
优点：
  reids 的查询速度是快于mysql的，在需要查询统计次数的话，速度更加快。
  在这里我们使用的是BitMap 位图功能来实现，每次签到与未签到用0或1来标识 ，一次存31个数字，只用了2字节只用用极小的空间实现了签到功能。
具体实现方式：[SpringBoot+Redis BitMap 实现签到与统计功能]
