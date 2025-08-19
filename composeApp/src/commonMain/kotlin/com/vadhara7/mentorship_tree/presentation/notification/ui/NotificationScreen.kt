package com.vadhara7.mentorship_tree.presentation.notification.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vadhara7.mentorship_tree.domain.model.dto.RequestStatus
import com.vadhara7.mentorship_tree.domain.model.ui.RequestUi
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationIntent
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.accept
import mentorshiptree.composeapp.generated.resources.decline
import mentorshiptree.composeapp.generated.resources.ic_add
import mentorshiptree.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    state: NotificationState,
    onIntent: (NotificationIntent) -> Unit
) {

    RequestsList(
        requests = state.requests,
        modifier = modifier,
        onAccept = { request ->
            onIntent(NotificationIntent.AcceptRequest(request.fromUser.uid))
        },
        onDecline = { request ->
            onIntent(NotificationIntent.DeclineRequest(request.fromUser.uid))
        }
    )

}

@Composable
private fun RequestsList(
    requests: List<RequestUi>,
    onAccept: (RequestUi) -> Unit,
    onDecline: (RequestUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (requests.isEmpty()) {
        EmptyState(modifier.fillMaxSize())
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        items(requests, key = { it.fromUser.uid + "_" + it.createdAt }) { req ->
            RequestCard(
                request = req,
                onAccept = { onAccept(req) },
                onDecline = { onDecline(req) },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun RequestCard(
    request: RequestUi,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row {
                Text(
                    text = request.fromUser.displayName ?: request.fromUser.email,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = request.createdAt.asTelegramStyle(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {

                if (!request.message.isNullOrBlank()) {
                    Text(
                        text = request.message,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {

                    if (request.status == RequestStatus.PENDING) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AssistChip(
                                onClick = { onDecline() },
                                label = {
                                    Text(
                                        text = stringResource(Res.string.decline),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_close),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            AssistChip(
                                onClick = { onAccept() },
                                label = {
                                    Text(
                                        text = stringResource(Res.string.accept),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_add),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    } else {
                        AssistChip(
                            onClick = { /* no-op */ },
                            label = {
                                Text(
                                    text = request.status.name.lowercase()
                                        .replaceFirstChar { it.titlecase() },
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (request.status) {
                                    RequestStatus.APPROVED -> MaterialTheme.colorScheme.primaryContainer
                                    RequestStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MessageBlock(message: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Message:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No requests yet",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "You'll see incoming mentorship requests here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Helpers ---
@OptIn(ExperimentalTime::class)
private fun Long.asTelegramStyle(): String {
    val tz = TimeZone.currentSystemDefault()
    val nowDt = Clock.System.now().toLocalDateTime(tz)
    val epochMillis = if (this < 1_000_000_000_000L) this * 1000 else this
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(tz)

    val today = nowDt.date
    val thatDay = dt.date

    // Same day -> HH:mm
    if (thatDay == today) {
        val h = dt.hour.toString().padStart(2, '0')
        val m = dt.minute.toString().padStart(2, '0')
        return "$h:$m"
    }

    // Yesterday
    val yesterday = today.plus(kotlinx.datetime.DatePeriod(days = -1))
    if (thatDay == yesterday) return "Yesterday"

    // Within last 7 days -> day of week short
    val daysDiff = thatDay.daysUntil(today) // positive if thatDay before today
    if (daysDiff in 2..7) {
        return when (thatDay.dayOfWeek) {
            kotlinx.datetime.DayOfWeek.MONDAY -> "Mon"
            kotlinx.datetime.DayOfWeek.TUESDAY -> "Tue"
            kotlinx.datetime.DayOfWeek.WEDNESDAY -> "Wed"
            kotlinx.datetime.DayOfWeek.THURSDAY -> "Thu"
            kotlinx.datetime.DayOfWeek.FRIDAY -> "Fri"
            kotlinx.datetime.DayOfWeek.SATURDAY -> "Sat"
            kotlinx.datetime.DayOfWeek.SUNDAY -> "Sun"
        }
    }

    // Same year -> d MMM
    if (thatDay.year == today.year) {
        val d = thatDay.day.toString()
        val mmm = when (dt.month.number) {
            1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
            7 -> "Jul"; 8 -> "Aug"; 9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; else -> "Dec"
        }
        return "$d $mmm"
    }

    // Else -> d MMM yyyy
    val d = thatDay.day.toString()
    val mmm = when (dt.month.number) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
        7 -> "Jul"; 8 -> "Aug"; 9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; else -> "Dec"
    }
    return "$d $mmm ${thatDay.year}"
}