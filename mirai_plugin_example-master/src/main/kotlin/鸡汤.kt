import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import java.net.URL



object Jitang : RawCommand(
    PluginMain, "鸡汤",
    usage = "/鸡汤",
    description = "发送一条鸡汤文给用户",
    prefixOptional = false
) {


    /**
     * 当命令发送者执行命令时的处理函数。
     * 此函数被设计为异步执行，它会从一个远程API获取鸡汤文并发送给命令发送者。
     *
     * @param args 命令发送者发送的命令参数，用于解析和执行相应的操作。
     */
    override suspend fun CommandSender.onCommand(args: MessageChain) {


        runBlocking {
            try {
                val response = URL("https://v.api.aa1.cn/api/api-wenan-dujitang/index.php?aa1=json").readText()
                val jsonArray = JsonParser.parseString(response).asJsonArray
                if (jsonArray.size() > 0) {
                    val jsonObject = jsonArray[0].asJsonObject
                    val dujitang = jsonObject.get("dujitang").asString
                    MessageChainBuilder().append(dujitang).build().also { sendMessage(it) }
                } else {
                    sendMessage("未能获取到鸡汤文，请稍后再试。")
                }
            } catch (e: Exception) {
                sendMessage("获取鸡汤文失败，请稍后再试。")
            }

        }
    }
}
