package androidx.compose.foundation.layout

import androidx.compose.ui.Modifier

/**
 * Compatibility symbol for source files that explicitly import layout.weight.
 * Inside RowScope/ColumnScope, Compose's native member extension still takes precedence.
 */
fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier = this
