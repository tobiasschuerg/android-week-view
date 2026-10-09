package de.tobiasschuerg.weekview.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tobiasschuerg.weekview.compose.EventContentScope

/** Example of a custom entry design: a light card with a colored bar on the left, title and room. */
@Composable
fun CustomEventContent(scope: EventContentScope) {
    val accent = Color(scope.event.backgroundColor)
    val shape = RoundedCornerShape(6.dp)
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(shape)
                // Opaque base, so grid lines don't show through the translucent tint.
                .background(MaterialTheme.colorScheme.surface)
                .background(accent.copy(alpha = 0.15f))
                .border(1.dp, accent.copy(alpha = 0.4f), shape),
    ) {
        Box(
            Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(accent),
        )
        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
            Text(
                text = scope.event.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            // Only show the room when the entry is tall enough for a second line.
            val room = scope.event.subTitle
            if (room != null && scope.height > 32.dp) {
                Text(
                    text = room,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
