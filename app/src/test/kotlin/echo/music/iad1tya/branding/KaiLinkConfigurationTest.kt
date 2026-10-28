/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.branding

import java.net.URI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KaiLinkConfigurationTest {
  @Test fun presenceBrandFallbackOnlyUsesConfiguredDestinations() {
    assertNull(KaiLinkConfiguration().defaultPresenceDestination())
    val github = "https://source.example.org/kai"
    val website = "https://music.example.org/kai"
    assertEquals(github, KaiLinkConfiguration(githubUrl = github).defaultPresenceDestination())
    assertEquals(website, KaiLinkConfiguration(websiteUrl = website, githubUrl = github).defaultPresenceDestination())
    assertEquals(github, KaiLinkConfiguration(websiteUrl = "not-configured", githubUrl = github).defaultPresenceDestination())
  }

  @Test fun presenceLinksPreserveValidSavedUserDestinations() {
    val https = "https://music.example.org/my-page?view=1"
    val http = "http://music.example.org/my-page"
    assertEquals(https, normalizedPresenceUrl(https))
    assertEquals(http, normalizedPresenceUrl(http))
    assertEquals("https://music.example.org/my-page", normalizedPresenceUrl("music.example.org/my-page"))
  }

  @Test fun invalidPresenceLinksAreOmittedInsteadOfSentOrTruncated() {
    listOf(null, "", " ", "not-a-url", "javascript:alert(1)", "file:///tmp/page", "https://",
      "https://user:password@music.example.org", "https://music.example.org/with space",
      "https://music.example.org/" + "a".repeat(256)).forEach { value ->
      assertNull(value, normalizedPresenceUrl(value))
    }
  }

  @Test
  fun unsetPublicDestinationsNeverResolveToAnAction() {
    val configuration = KaiLinkConfiguration()
    KaiLink.entries.forEach { link ->
      assertNull(configuration.destination(link))
      assertNull(configuration.displayValue(link))
    }
  }

  @Test
  fun webActionsRequireAnExplicitValidHttpsDestination() {
    val unsafe =
      listOf(
        null,
        "",
        " ",
        "not-a-url",
        "https://",
        "http://music.example.org",
        "javascript:alert(1)",
        "file:///tmp/page",
        "https://user:password@music.example.org",
        "https://music.example.org/with space",
        "https://music.example.org/\nlink",
        "https://localhost",
        "https://music.example.org:99999",
      )
    unsafe.forEach { candidate ->
      assertNull(
        candidate,
        KaiLinkConfiguration(websiteUrl = candidate).destination(KaiLink.WEBSITE),
      )
    }
    val valid = "https://music.example.org/kai?ref=app#about"
    assertEquals(valid, KaiLinkConfiguration(websiteUrl = " $valid ").destination(KaiLink.WEBSITE))
  }

  @Test
  fun configuringOneLinkDoesNotEnableAnotherLink() {
    val configuration = KaiLinkConfiguration(websiteUrl = "https://music.example.org")
    assertEquals("https://music.example.org", configuration.destination(KaiLink.WEBSITE))
    KaiLink.entries
      .filter { it != KaiLink.WEBSITE }
      .forEach { assertNull(configuration.destination(it)) }
  }

  @Test
  fun anInstagramNameAloneCannotInventAPublicDestination() {
    val usernameOnly = KaiLinkConfiguration(instagramUsername = "kai.music")
    assertNull(usernameOnly.destination(KaiLink.INSTAGRAM))
    assertNull(usernameOnly.displayValue(KaiLink.INSTAGRAM))
    val configured = usernameOnly.copy(instagramUrl = "https://www.instagram.com/kai.music")
    assertEquals("@kai.music", configured.displayValue(KaiLink.INSTAGRAM))
  }

  @Test
  fun malformedUpiIdsCannotLaunchPaymentApplications() {
    listOf(
        null,
        "",
        "kai",
        "@upi",
        "kai@",
        "kai @upi",
        "kai@upi&am=1000",
        "kai@upi?am=1000",
        "kai@bank..upi",
        "kai@bank-",
      )
      .forEach { id ->
        assertNull(id, KaiLinkConfiguration(upiId = id).destination(KaiLink.UPI))
      }
  }

  @Test
  fun configuredUpiActionEncodesOnlyThePayeeAndBrand() {
    val configuration = KaiLinkConfiguration(upiId = "kai.music@upi")
    val destination = configuration.destination(KaiLink.UPI)!!
    val uri = URI(destination)
    assertEquals("upi", uri.scheme)
    assertEquals("pay", uri.authority)
    assertEquals("pa=kai.music@upi&pn=KAI", uri.query)
    assertTrue(uri.rawQuery.contains("%40"))
    assertEquals("kai.music@upi", configuration.displayValue(KaiLink.UPI))
    assertEquals(setOf("pa", "pn"), uri.query.split('&').map { it.substringBefore('=') }.toSet())
  }
}
