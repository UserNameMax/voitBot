package ydb

import executeQuery
import model.Variant
import tech.ydb.table.SessionRetryContext
import utils.takeIfNotEmpty

class VariantRepository(private val sessionRetryContext: SessionRetryContext) {
    private val votes: MutableMap<ULong, Int> = VotesYdbMap(sessionRetryContext)

    fun getVariants(): MutableMap<Variant, Int> {
        val result = mutableMapOf<Variant, Int>()
        sessionRetryContext.executeQuery("select id, name, url from Variants")
            .getResultSet(0).apply {
                while (next()) {
                    val variant = Variant(getColumn("name").text, getColumn("url").text.takeIfNotEmpty())
                    result[variant] = votes[getColumn("id").uint64.toULong()] ?: 0
                }
            }
        return result
    }

    fun getVariantsWithId(): MutableMap<Variant, ULong> {
        val result = mutableMapOf<Variant, ULong>()
        sessionRetryContext.executeQuery("select id, name, url from Variants")
            .getResultSet(0).apply {
                while (next()) {
                    val variant = Variant(getColumn("name").text, getColumn("url").text.takeIfNotEmpty())
                    result[variant] = getColumn("id").uint64.toULong()
                }
            }
        return result
    }

    fun addVariant(variant: Variant) {
        val query = buildString {
            append("insert into Variants(id, name")
            if (variant.url != null) append(", url")
            append(") values (${variant.hashCode()},\"${variant.name}\"")
            if (variant.url != null) append(",\"${variant.url}\"")
            append(")")
        }
        sessionRetryContext.executeQuery(query)
    }

    fun getVariantId(predicate: (Variant) -> Boolean): ULong? {
        val result = mutableListOf<ULong>()
        sessionRetryContext.executeQuery("select id, name, url from Variants")
            .getResultSet(0).apply {
                while (next()) {
                    val variant = Variant(getColumn("name").text, getColumn("url").text.takeIfNotEmpty())
                    if (predicate(variant)){
                        result.add(getColumn("id").uint64.toULong())
                    }
                }
            }
        return result.firstOrNull()
    }
}