package utils

import ydb.VariantRepository

fun VariantRepository.variantsAsString(): String {
    return getVariants()
        .toList()
        .joinToString(separator = "\n") {
            if (it.first.url == null) {
                "${it.first.name} - ${it.second}"
            } else {
                "[${it.first.name}](${it.first.url}) - ${it.second}"
            }

        }
}