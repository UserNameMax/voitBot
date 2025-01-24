package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.webhook
import resources.R

fun Bot.Builder.setupWebHook(){
    val url = R.webHookUrl
    if (url != null){
        webhook {
            this.url = url
        }
    }
}

fun Bot.start(){
    if (R.webHookUrl == null){
        startPolling()
    } else {
        startWebhook()
    }
}