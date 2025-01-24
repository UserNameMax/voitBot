package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import model.Variant
import utils.toVariant
import utils.variantsAsString
import vote.VoteService
import vote.sessionRetryContext
import vote.voteService
import ydb.VariantRepository

fun Bot.Builder.routes() = dispatch {
    val variantRepository = VariantRepository(sessionRetryContext())
    command("get") {
        val buttons = variantRepository.getVariantsWithId().map { entry: Map.Entry<Variant, ULong> ->
            val (variant, id) = entry
            InlineKeyboardButton.CallbackData(text = variant.name, callbackData = id.toString())
        }
        val inlineKeyboardMarkup = InlineKeyboardMarkup.create(*buttons.chunked(2).toTypedArray())
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = variantRepository.variantsAsString(),
            parseMode = ParseMode.MARKDOWN,
            replyMarkup = inlineKeyboardMarkup
        )
    }

    callbackQuery {
        val service: VoteService = voteService(callbackQuery.from.id.toInt())
        val id = this.callbackQuery.data.toULong()
        service.vote(id)
        bot.sendMessage(
            chatId = ChatId.fromId(callbackQuery.message?.chat?.id ?: 0),
            text = variantRepository.variantsAsString(),
            parseMode = ParseMode.MARKDOWN
        )
    }

    message {
        if (message.text?.firstOrNull() == '/') return@message
        runCatching {
            val service: VoteService = voteService(message.from?.id?.toInt() ?: 0)
            val id: ULong = variantRepository.getVariantId { it.name == message.text } ?: message.toVariant()
                .apply { variantRepository.addVariant(first) }.second
            service.vote(id)
            bot.sendMessage(
                chatId = ChatId.fromId(message.chat.id),
                text = variantRepository.variantsAsString(),
                parseMode = ParseMode.MARKDOWN
            )
        }.onFailure {
            it.printStackTrace()
        }
    }
}