package utils

import com.github.kotlintelegrambot.entities.Message
import model.Variant

fun Message.toVariant(): Pair<Variant, ULong> {
    val variant = Variant(text ?: "", this.entities?.firstOrNull { it.url != null }?.url)
    return variant to variant.hashCode().toULong()
}