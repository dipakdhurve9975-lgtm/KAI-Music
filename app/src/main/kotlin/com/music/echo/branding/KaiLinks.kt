package echo.music.iad1tya.branding

import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/** Public KAI destinations. Leave an entry empty until the owner supplies the real account. */
object KaiLinks {
  const val WEBSITE_URL = ""
  const val INSTAGRAM_USERNAME = "dipak.s.dhurve"
  const val INSTAGRAM_URL = "https://www.instagram.com/dipak.s.dhurve/"
  const val GITHUB_URL = "https://github.com/dipakdhurve9975-lgtm"
  const val DISCORD_URL = ""
  const val BUY_ME_A_COFFEE_URL = "https://buymeacoffee.com/deku2666"
  const val UPI_ID = "8999575447@apl"
  const val TELEGRAM_URL = ""

  val configuration =
    KaiLinkConfiguration(
      websiteUrl = WEBSITE_URL,
      instagramUsername = INSTAGRAM_USERNAME,
      instagramUrl = INSTAGRAM_URL,
      githubUrl = GITHUB_URL,
      discordUrl = DISCORD_URL,
      coffeeUrl = BUY_ME_A_COFFEE_URL,
      upiId = UPI_ID,
      telegramUrl = TELEGRAM_URL,
    )

  fun defaultPresenceDestination(): String? = configuration.defaultPresenceDestination()
}

enum class KaiLink {
  WEBSITE,
  INSTAGRAM,
  GITHUB,
  DISCORD,
  COFFEE,
  UPI,
  TELEGRAM,
}

/** Pure configuration/validation shared by every public account link and its unit tests. */
data class KaiLinkConfiguration(
  val websiteUrl: String? = null,
  val instagramUsername: String? = null,
  val instagramUrl: String? = null,
  val githubUrl: String? = null,
  val discordUrl: String? = null,
  val coffeeUrl: String? = null,
  val upiId: String? = null,
  val telegramUrl: String? = null,
) {
  fun defaultPresenceDestination(): String? =
    destination(KaiLink.WEBSITE) ?: destination(KaiLink.GITHUB)

  fun destination(link: KaiLink): String? =
    when (link) {
      KaiLink.WEBSITE -> secureWebLink(websiteUrl)
      KaiLink.INSTAGRAM -> secureWebLink(instagramUrl)
      KaiLink.GITHUB -> secureWebLink(githubUrl)
      KaiLink.DISCORD -> secureWebLink(discordUrl)
      KaiLink.COFFEE -> secureWebLink(coffeeUrl)
      KaiLink.TELEGRAM -> secureWebLink(telegramUrl)
      KaiLink.UPI ->
        upiId
          ?.trim()
          ?.takeIf { it.length <= 255 && UPI_PATTERN.matches(it) }
          ?.let {
            "upi://pay?pa=${URLEncoder.encode(it, StandardCharsets.UTF_8.name())}&pn=KAI"
          }
    }

  fun displayValue(link: KaiLink): String? {
    val destination = destination(link) ?: return null
    if (link == KaiLink.UPI) return upiId?.trim()
    if (link == KaiLink.INSTAGRAM) {
      instagramUsername
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?.let {
          val handle = it.removePrefix("@")
          if (INSTAGRAM_PATTERN.matches(handle)) return "@$handle"
        }
    }
    val uri = URI(destination)
    return uri.host.removePrefix("www.") + uri.path.orEmpty().trimEnd('/')
  }

  private fun secureWebLink(raw: String?): String? {
    val value = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    if (value.any { it.isWhitespace() || it.isISOControl() }) return null
    val uri = runCatching { URI(value) }.getOrNull() ?: return null
    return value.takeIf {
      uri.scheme.equals("https", ignoreCase = true) &&
        uri.isAbsolute &&
        uri.userInfo == null &&
        !uri.host.isNullOrBlank() &&
        uri.host.contains('.') &&
        (uri.port == -1 || uri.port in 1..65535)
    }
  }

  private companion object {
    val UPI_PATTERN =
      Regex("[A-Za-z0-9][A-Za-z0-9._-]{1,255}@[A-Za-z][A-Za-z0-9]*(?:[.-][A-Za-z0-9]+)*")
    val INSTAGRAM_PATTERN = Regex("[A-Za-z0-9._]{1,30}")
  }
}

/** Keep valid saved user web links, but never emit an empty, malformed or truncated RPC button. */
internal fun normalizedPresenceUrl(raw: String?): String? {
  val value = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
  if (value.any { it.isWhitespace() || it.isISOControl() }) return null
  val hasWebScheme = value.startsWith("https://", true) || value.startsWith("http://", true)
  if (!hasWebScheme && value.contains("://")) return null
  val candidate = if (hasWebScheme) value else "https://$value"
  if (candidate.length > 256) return null
  val uri = runCatching { URI(candidate) }.getOrNull() ?: return null
  return candidate.takeIf {
    (uri.scheme.equals("https", true) || uri.scheme.equals("http", true)) &&
      uri.userInfo == null &&
      !uri.host.isNullOrBlank() &&
      uri.host.contains('.') &&
      (uri.port == -1 || uri.port in 1..65535)
  }
}
