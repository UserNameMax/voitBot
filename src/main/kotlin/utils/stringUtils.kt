package utils

fun String.takeIfNotEmpty() = takeIf { it.isNotEmpty() }