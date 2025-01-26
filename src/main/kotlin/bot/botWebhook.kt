package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.webhook
import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import resources.R

fun Bot.Builder.setupWebHook() {
    val url = R.webHookUrl
    if (url != null) {
        webhook {
            this.url = "$url/${R.botToken}"
            maxConnections = 50
            allowedUpdates = listOf("message")
        }
    }
}

fun Bot.start() {
    if (R.webHookUrl == null) {
        startPolling()
    } else {
        startWebhook()
        embeddedServer(CIO, host = "0.0.0.0", port = 8080) {
            routing {
                post("/${R.botToken}") {
                    val response = call.receiveText()
                    processUpdate(response)
                    call.respond(HttpStatusCode.OK)
                }
                get("/start") {
                    call.respondText("Hello world")
                }
            }
        }.start(wait = true)
    }
}