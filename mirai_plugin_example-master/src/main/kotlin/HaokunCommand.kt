import org.jsoup.Jsoup
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.RawCommand
import okhttp3.OkHttpClient
import okhttp3.Request
import net.mamoe.mirai.message.data.MessageChain

object HaokunCommand : RawCommand(
    PluginMain,
    "指令测试",
    usage = "/指令测试",
    description = "这是一个测试指令",
    prefixOptional = false
) {
    private val httpClient = OkHttpClient()

    override suspend fun CommandContext.onCommand(args: MessageChain) {
        try {
            val request = Request.Builder()
                .url("https://v.api.aa1.cn/api/yiyan/index.php")
                .build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val doc = Jsoup.parse(responseBody)
                val text = doc.select("p").text()
                sender.sendMessage("请求成功，返回的文本是：$text")
            } else {
                sender.sendMessage("请求失败，HTTP状态码：${response.code}")
            }
        } catch (e: Exception) {
            sender.sendMessage("请求失败，请检查网络连接或API地址: ${e.message}")
        }
    }
}
