package com.vadhara7.mentorship_tree.presentation.notification.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.accept
import mentorshiptree.composeapp.generated.resources.decline
import mentorshiptree.composeapp.generated.resources.ic_add
import mentorshiptree.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initials = remember(request.fromUser.displayName, request.fromUser.email) {
                (request.fromUser.displayName?.takeIf { it.isNotBlank() } ?: request.fromUser.email)
                    .trim()
                    .split(" ")
                    .mapNotNull { it.firstOrNull()?.uppercase() }
                    .take(2)
                    .joinToString("")
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(initials, style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = request.fromUser.displayName ?: request.fromUser.email,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = request.fromUser.email,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AssistChip(
                onClick = { /* no-op */ },
                label = {
                    Text(
                        text = request.status.name.lowercase().replaceFirstChar { it.titlecase() },
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = when (request.status) {
                        RequestStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
                        RequestStatus.APPROVED -> MaterialTheme.colorScheme.primaryContainer
                        RequestStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
                    }
                )
            )
        }

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            MetaRow(label = "Created", value = request.createdAt.asDateString())

            if (request.reviewedAt != null) {
                Spacer(Modifier.height(4.dp))
                MetaRow(label = "Reviewed", value = request.reviewedAt.asDateString())
            }

            if (!request.message.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                MessageBlock(message = request.message!!)
            }

            if (request.reviewedAt == null) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDecline, contentPadding = PaddingValues(8.dp)) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_close),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(Res.string.decline),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onAccept, contentPadding = PaddingValues(8.dp)) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(Res.string.accept),
                            modifier = Modifier.padding(horizontal = 16.dp)
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
private fun Long.asDateString(): String {
    // If value looks like epoch seconds, convert to millis for safety
    val epochMillis = if (this < 1_000_000_000_000L) this * 1000 else this
    val dt = Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val day = dt.day.toString().padStart(2, '0')
    val month = dt.month.number.toString().padStart(2, '0')
    val year = dt.year.toString()
    val hour = dt.hour.toString().padStart(2, '0')
    val minute = dt.minute.toString().padStart(2, '0')

    // Numeric, locale-friendly style: dd.MM.yyyy HH:mm
    return "$day.$month.$year $hour:$minute"
}