## **好困困**

### 介绍

**好困困**插件是一个用于**Mirai机器人**的插件，它提供了一种灵快的机器人对话体验。

### 当前功能

#### 运行插件：
目前未提供发行版，请**自行编译**，**填写智谱apikey**后运行。

#### 构建说明：

修改 **src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin** 文件，确保内容为 "**PluginMain**"。
在 **settings.gradle.kts** 中修改生成的插件 .jar **名称**。
在 **build.gradle.kts** 中**更新依赖库和插件版本**。
在 **JvmPluginDescription** 中**更改插件名称**、**ID** 和**版本**。


#### 开发环境

支持的语言：Kotlin，Java
依赖库：
* com.fasterxml.jackson.core
* com.zhipu.oapi
* net.mamoe.mirai

#### 注意事项

1. 确保在 ClientV4.Builder 中使用正确的 API 密钥。
2. 插件使用了 ChatGLM4 模型进行消息处理，确保网络连接稳定以避免调用失败。


###### 声明

* 本插件由 [我好困] 开发。
* 欢迎请使用**issues** 提交问题反馈~~或代码贡献~~。
* 本项目遵循 **AGPLv3** 许可证，详细信息见 **LICENSE** 文件。