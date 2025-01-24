import bot.routes
import bot.setupWebHook
import bot.start
import com.github.kotlintelegrambot.bot
import resources.R

fun main() {
    bot {
        token = R.botToken
        routes()
        setupWebHook()
    }.start()
}