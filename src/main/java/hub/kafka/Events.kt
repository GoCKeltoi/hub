package de.mobile.commercial.listing.service.kafka

import de.mobile.commercial.listing.service.Status
import java.net.URI
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class Event(
    val id: String,
    val type: String,
    val origin: Origin,
    val timestamp: String = ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT),
    val functionalities: Functionalities? = null,
    val profile: DealerProfile? = null,
    val orders: List<Order>? = null,
    val user: User? = null,
    val status: Status? = null
)

enum class EventType {
    ACCOUNT_CHANGE,
    MOBILE_DB_INSERT,
    MOBILE_DB_UPDATE,
    MOBILE_DB_DELETE,
    MOBILE_DB_SYNC,
    PROFILE_CHANGE,
    ORDER,
}

enum class Origin {
    DATS,
    MOBILE_DB,
    DSPS,
    LISTING_SERVICE,
}

data class Functionalities(
    val vipAdvertisingFree: Map<String,String>?,
    val videoSlideshow: Map<String,String>?,
    val vipShowroomGallery: Map<String,String>?,
    val vipCompanyLogo: Map<String,String>?,
    val vipHeroImage: Map<String,String>?,
    val vipCompanyServices: Map<String,String>?,
    val vipWelcomeText: Map<String,String>?,
    val videoYouTube: Map<String,String>?,
    val similarAds: Map<String,String>?,
    val highlights: Map<String,String>?,
    val pictures: Map<String,String>?
)

data class DealerProfile(
    val vipHeroImage: Image?,
    val vipShowroomGallery: List<Image>,
    val welcomeText: String?,
    val welcomeTextWiki: String?,
    val selectedServices: List<String>,
    val vipCompanyLogo: Image?
)


data class Image(val url: URI)

data class FeatureChangesIn(
    val id: String?,
    val orders: List<Order>?,
    val user: User?
)

class Order(
    val name: String,
    val state: String
)

class User(
    val name: String,
    val type: String,
    val id: String,
    val ipAddress: String
)

