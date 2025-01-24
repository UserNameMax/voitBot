package ydb

import executeQuery
import tech.ydb.table.SessionRetryContext

class VotesYdbMap(private val sessionRetryContext: SessionRetryContext) : MutableMap<ULong, Int> {

    private inner class TableEntity(
        override val key: ULong,
        override val value: Int
    ) : MutableMap.MutableEntry<ULong, Int> {
        override fun setValue(newValue: Int): Int {
            put(key, newValue)
            return value
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<ULong, Int>>
        get() = fetchAll()
    override val keys: MutableSet<ULong>
        get() = fetchAll().map { it.key }.toMutableSet()
    override val size: Int
        get() = fetchAll().size
    override val values: MutableCollection<Int>
        get() = fetchAll().map { it.value }.toMutableSet()

    override fun clear() {
        sessionRetryContext.executeQuery("delete from Votes where true")
    }

    override fun isEmpty(): Boolean = size == 0

    override fun remove(key: ULong): Int? {
        val oldValue = get(key)
        sessionRetryContext.executeQuery("delete from Votes where variantCode=$key")
        return oldValue
    }

    override fun putAll(from: Map<out ULong, Int>) {
        from.forEach { entry: Map.Entry<ULong, Int> ->
            put(entry)
        }
    }

    override fun put(key: ULong, value: Int): Int? {
        val oldValue = get(key)
        put(TableEntity(key, value))
        return oldValue
    }

    override fun get(key: ULong): Int? = fetchAll(variant = key).firstOrNull()?.value

    override fun containsValue(value: Int): Boolean = fetchAll(voites = value).isNotEmpty()

    override fun containsKey(key: ULong): Boolean = fetchAll(variant = key).isNotEmpty()

    private fun fetchAll(
        variant: ULong? = null,
        voites: Int? = null
    ): MutableSet<MutableMap.MutableEntry<ULong, Int>> {
        val result = mutableSetOf<MutableMap.MutableEntry<ULong, Int>>()
        val query = buildString {
            append("select variantCode, votes from Votes")
            if (variant != null || voites != null) {
                append(" where")
            }
            if (variant != null) {
                append(" variantCode = $variant")
            }
            if (voites != null) {
                append(" votes = $voites")
            }
        }
        sessionRetryContext.executeQuery(query)
            .getResultSet(0).apply {
                while (next()) {
                    val entity =
                        TableEntity(getColumn("variantCode").uint64.toULong(), getColumn("votes").uint64.toInt())
                    result.add(entity)
                }
            }
        return result
    }

    private fun put(entyti: Map.Entry<ULong, Int>) {
        if (containsKey(entyti.key)) {
            sessionRetryContext.executeQuery("update Votes set votes=${entyti.value} where variantCode=${entyti.key}")
        } else {
            sessionRetryContext.executeQuery("insert into Votes(variantCode,votes) values (${entyti.key},${entyti.value})")
        }
    }
}