import com.fasterxml.jackson.core.JsonProcessingException
import com.zhipu.oapi.ClientV4
import com.zhipu.oapi.Constants
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest
import com.zhipu.oapi.service.v4.model.ChatMessage
import com.zhipu.oapi.service.v4.model.ChatMessageRole
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info
import java.lang.Boolean


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
                messages.add(ChatMessage(ChatMessageRole.SYSTEM.value(), "这里是系统级的提示"))
            }
            // 创建并添加新的 ChatMessage 到对应群组的消息列表
            val ChatMessage = ChatMessage(ChatMessageRole.USER.value(), message.contentToString())
            messages.add(ChatMessage)
            /**构建聊天完成请求，其中包括模型类型、是否流式处理、调用方法和消息列表这里的requestId不必传入，因为客户端会自动生成一个requestId
             */
            val chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)//流式传输，不过我不会用
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


