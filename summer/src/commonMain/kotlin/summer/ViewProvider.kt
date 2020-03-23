package summer

import summer.state.SummerStateDelegate
import summer.events.SummerEvent

/**
 * Provider of view to mirror [SummerStateDelegate] state changes and [SummerEvent] action calls to it.
 */
interface ViewProvider<out TView> {
    val getView: () -> TView?
}