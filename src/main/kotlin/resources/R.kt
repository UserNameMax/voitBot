package resources

import java.nio.file.Paths
import java.util.*
import kotlin.io.path.Path

object R {
    private fun getStringFromEnv(key: String) = System.getenv()[key]
    private fun getStringFromFile(path: String): String? = runCatching {
        val baseUrl = Paths.get("").toAbsolutePath().toString() + "/src/main/resources"
        val scanner = Scanner(Path("$baseUrl/$path").toFile())
        return buildString {
            while (scanner.hasNextLine()) {
                append(scanner.nextLine())
                append('\n')
            }
        }.dropLast(1)
    }.getOrNull()

    val botToken = getStringFromEnv("BOT_TOKEN")
        ?: getStringFromFile("botToken")
        ?: error("bot token not found")

    val connectionString = getStringFromEnv("CONNECTION_STRING")
        ?: getStringFromFile("connectionString")
        ?: error("connection string not found")

    val authKeyJson = getStringFromEnv("AUTH_KEY_JSON")
        ?: getStringFromFile("authorizedKeyJson")
        ?: error("authorized key json not found")

    val webHookUrl = getStringFromEnv("WEBHOOK_URL")
}