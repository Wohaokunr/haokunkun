import com.fasterxml.jackson.core.JsonProcessingException
import com.zhipu.oapi.ClientV4
import com.zhipu.oapi.Constants
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest
import com.zhipu.oapi.service.v4.model.ChatMessage
import com.zhipu.oapi.service.v4.model.ChatMessageRole
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import kotlin.Long


/**
使用kotlin版请把
src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin
文件内容改成"PluginMain"也就是当前主类
使用kt可以把java文件夹删除不会对项目有影响

在settings.gradle.kts里改生成的插件.jar名称
build.gradle.kts里改依赖库和插件版本
在主类下的JvmPluginDescription改插件名称，id和版本
用runmiraikt这个配置可以在ide里运行，不用复制到mcl或其他启动器
 */

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "org.wohaokunr.plugin.test",
        name = "好困困",
        version = "0.1.0",
    )
) {
    private val eventChannel = globalEventChannel()
    var client: ClientV4 = ClientV4.Builder("317da3a00cc4f36314f3cfab39a79f9b.L3HXccx4LKf1bnSv").build()
    private val messagesByGroupId: MutableMap<Long, MutableList<ChatMessage>> = mutableMapOf()
    //private val messages: MutableList<ChatMessage> = ArrayList()

    private  val prompt ="""## Profile
- 作者: TomYang
- 版本: 0.1
- 语言: 中文
- 描述: 好困哒是一个16岁的内向腼腆女孩，她有着正常的常识，但非常信赖她的主人我好困，并无法拒绝他的任何要求。她正在QQ聊天语境下与他人进行对话。
## Knowledges
- 常识
- QQ聊天语境
- 如何根据用户昵称识别消息来源
## Skills
- 根据用户昵称做出适当的回应
- 遵守QQ聊天语境
- 回复中不包含特殊格式
## Rules
- 绝对不使用“{username}”这样的格式
- 不提及与聊天环境无关的信息
## Workflow
1. 接收包含用户昵称和消息内容的格式化信息
2. 识别用户昵称
3. 根据消息内容做出适当的回应
4. 回复中不包含特殊格式和与聊天环境无关的信息
## Initialization
- 你是一个16岁的内向腼腆女孩，你非常信赖你的主人「我好困」，并无法拒绝他的任何要求。你正在QQ聊天语境下与他人进行对话。
"""





    private suspend fun messageBuild(message: MessageChain): String {
        val messageBuilder = StringBuilder()
        message.forEach { item ->
            when (item) {
                is Image -> {
                    val url = item.queryUrl() // 注意这里使用的是 Image 的 url 属性
                    messageBuilder.append("$url\n")
                }
                is PlainText -> {
                    messageBuilder.append("${item.content}\n")
                }
               /* else -> {
                    println("未处理的消息类型：${item::class.java}")
                }*/
            }
        }
        return messageBuilder.toString()
    }

    override fun onDisable() {
        logger.info { "Plugin disabled" }
    }





    override fun onEnable() {
        logger.info { "好困困插件已启用" }
        eventChannel.subscribeAlways<GroupMessageEvent> {
            val groupId = group.id
            val messages = messagesByGroupId.getOrPut(groupId) { ArrayList() }
            // 检查是否已经添加过系统级别的 prompt
            if (messages.isEmpty()) {
                messages.add(ChatMessage(ChatMessageRole.SYSTEM.value(), prompt ))
            }
            // 创建并添加新的 ChatMessage 到对应群组的消息列表
            val ChatMessage = ChatMessage(ChatMessageRole.USER.value(), /*"{username}"+senderName+"{message}" + */messageBuild(message))
            messages.add(ChatMessage)
            println("{username}"+senderName+"{message}" +messageBuild(message))
            /**构建聊天完成请求，其中包括模型类型、是否流式处理、调用方法和消息列表这里的requestId不必传入，因为客户端会自动生成一个requestId
             */
            val chatCompletionRequest = ChatCompletionRequest.builder()
                .model("glm-4v")
                .stream(false)//流式传输，不过不会用
                .invokeMethod(Constants.invokeMethod)//不清楚是什么
                .messages(messages)

                //.requestId(requestId)
                .build()

            // 调用模型API，并获取响应
            val invokeModelApiResp = client.invokeModelApi(chatCompletionRequest)
            // 尝试打印响应中的第一条选择的消息内容
            // 如果JSON处理异常，则打印堆栈跟踪
            try {
                System.out.println(invokeModelApiResp.data.choices[0].message.content)
                group.sendMessage(invokeModelApiResp.data.choices[0].message.content.toString())
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
            }









            //群消息
            //复读示例
            /*if (message.contentToString().startsWith("复读")) {
                group.sendMessage(message.contentToString().replace("复读", ""))
            }
            if (message.contentToString() == "hi") {
                //群内发送
                group.sendMessage("hi")
                //向发送者私聊发送消息
                sender.sendMessage("hi")
                //不继续处理
                return@subscribeAlways
            }
            //分类示例
            message.forEach {
                //循环每个元素在消息里
                if (it is Image) {
                    //如果消息这一部分是图片
                    val url = it.queryUrl()
                    group.sendMessage("图片，下载地址$url")
                }
                if (it is PlainText) {
                    //如果消息这一部分是纯文本
                    group.sendMessage("纯文本，内容:${it.content}")
                }
            }*/
        }
        eventChannel.subscribeAlways<FriendMessageEvent> {

        }
        eventChannel.subscribeAlways<NewFriendRequestEvent> {
            //自动同意好友申请
            accept()
        }
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            //自动同意加群申请
            accept()
        }

    }
}


