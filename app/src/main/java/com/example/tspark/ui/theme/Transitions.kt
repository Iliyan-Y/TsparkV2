import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

private const val TRANSITION_DURATION = 300

fun defaultEnterTransition(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = TRANSITION_DURATION)
    ) + fadeIn(animationSpec = tween(durationMillis = TRANSITION_DURATION))

fun defaultExitTransition(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = TRANSITION_DURATION)
    ) + fadeOut(animationSpec = tween(durationMillis = TRANSITION_DURATION))

fun defaultPopEnterTransition(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = TRANSITION_DURATION)
    ) + fadeIn(animationSpec = tween(durationMillis = TRANSITION_DURATION))

fun defaultPopExitTransition(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = TRANSITION_DURATION)
    ) + fadeOut(animationSpec = tween(durationMillis = TRANSITION_DURATION))
