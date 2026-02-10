package pl.syntaxdevteam.cleanerx.common

import java.util.concurrent.atomic.AtomicInteger

object ListenerRegistrationMetrics {
    private val counter = AtomicInteger(0)

    fun increment(): Int = counter.incrementAndGet()

    fun current(): Int = counter.get()

    fun reset() {
        counter.set(0)
    }
}
