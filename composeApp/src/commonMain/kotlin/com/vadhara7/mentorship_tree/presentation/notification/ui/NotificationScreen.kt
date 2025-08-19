package com.vadhara7.mentorship_tree.presentation.notification.ui

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
import mentorshiptree.composeapp.generated.resources.apr
import mentorshiptree.composeapp.generated.resources.aug
import mentorshiptree.composeapp.generated.resources.dec
import mentorshiptree.composeapp.generated.resources.decline
import mentorshiptree.composeapp.generated.resources.feb
import mentorshiptree.composeapp.generated.resources.fri
import mentorshiptree.composeapp.generated.resources.ic_add
import mentorshiptree.composeapp.generated.resources.ic_close
import mentorshiptree.composeapp.generated.resources.incoming_requests_hint
import mentorshiptree.composeapp.generated.resources.jan
import mentorshiptree.composeapp.generated.resources.jul
import mentorshiptree.composeapp.generated.resources.jun
import mentorshiptree.composeapp.generated.resources.mar
import mentorshiptree.composeapp.generated.resources.may
import mentorshiptree.composeapp.generated.resources.mon
import mentorshiptree.composeapp.generated.resources.no_requests_yet
import mentorshiptree.composeapp.generated.resources.nov
import mentorshiptree.composeapp.generated.resources.oct
import mentorshiptree.composeapp.generated.resources.sat
import mentorshiptree.composeapp.generated.resources.sep
import mentorshiptree.composeapp.generated.resources.sun
import mentorshiptree.composeapp.generated.resources.thu
import mentorshiptree.composeapp.generated.resources.tue
import mentorshiptree.composeapp.generated.resources.wed
import mentorshiptree.composeapp.generated.resources.yesterday
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

                Text(
                    text = request.message ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )


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
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.no_requests_yet),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(Res.string.incoming_requests_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Helpers ---
@OptIn(ExperimentalTime::class)
@Composable
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
    if (thatDay == yesterday) return stringResource(Res.string.yesterday)

    // Within last 7 days -> day of week short
    val daysDiff = thatDay.daysUntil(today) // positive if thatDay before today
    if (daysDiff in 2..7) {
        return when (thatDay.dayOfWeek) {
            kotlinx.datetime.DayOfWeek.MONDAY -> stringResource(Res.string.mon)
            kotlinx.datetime.DayOfWeek.TUESDAY -> stringResource(Res.string.tue)
            kotlinx.datetime.DayOfWeek.WEDNESDAY -> stringResource(Res.string.wed)
            kotlinx.datetime.DayOfWeek.THURSDAY -> stringResource(Res.string.thu)
            kotlinx.datetime.DayOfWeek.FRIDAY -> stringResource(Res.string.fri)
            kotlinx.datetime.DayOfWeek.SATURDAY -> stringResource(Res.string.sat)
            kotlinx.datetime.DayOfWeek.SUNDAY -> stringResource(Res.string.sun)
        }
    }

    // Same year -> d MMM
    if (thatDay.year == today.year) {
        val d = thatDay.day.toString()
        val mmm = when (dt.month.number) {
            1 -> stringResource(Res.string.jan)
            2 -> stringResource(Res.string.feb)
            3 -> stringResource(Res.string.mar)
            4 -> stringResource(Res.string.apr)
            5 -> stringResource(Res.string.may)
            6 -> stringResource(Res.string.jun)
            7 -> stringResource(Res.string.jul)
            8 -> stringResource(Res.string.aug)
            9 -> stringResource(Res.string.sep)
            10 -> stringResource(Res.string.oct)
            11 -> stringResource(Res.string.nov)
            else -> stringResource(Res.string.dec)
        }
        return "$d $mmm"
    }

    // Else -> d MMM yyyy
    val d = thatDay.day.toString()
    val mmm = when (dt.month.number) {
        1 -> stringResource(Res.string.jan)
        2 -> stringResource(Res.string.feb)
        3 -> stringResource(Res.string.mar)
        4 -> stringResource(Res.string.apr)
        5 -> stringResource(Res.string.may)
        6 -> stringResource(Res.string.jun)
        7 -> stringResource(Res.string.jul)
        8 -> stringResource(Res.string.aug)
        9 -> stringResource(Res.string.sep)
        10 -> stringResource(Res.string.oct)
        11 -> stringResource(Res.string.nov)
        else -> stringResource(Res.string.dec)
    }
    return "$d $mmm ${thatDay.year}"
}