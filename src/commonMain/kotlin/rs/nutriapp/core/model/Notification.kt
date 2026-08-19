package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AppNotification(
    val id: NotificationId,
    val type: NotificationType,
    val title: String,
    val message: String,
    val time: String,
    val read: Boolean = false,
    val icon: String,
    val actionLabel: String = "",
    val actionRoute: String = "",
)

fun List<AppNotification>.unreadCount(): Int = count { !it.read }

fun List<AppNotification>.groupedByType(): Map<NotificationType, List<AppNotification>> =
    groupBy { it.type }
