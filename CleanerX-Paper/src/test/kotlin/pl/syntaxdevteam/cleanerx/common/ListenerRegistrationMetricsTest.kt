package pl.syntaxdevteam.cleanerx.common

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ListenerRegistrationMetricsTest {

    @AfterTest
    fun tearDown() {
        ListenerRegistrationMetrics.reset()
    }

    @Test
    fun `increment should increase listener registration count`() {
        assertEquals(1, ListenerRegistrationMetrics.increment())
        assertEquals(2, ListenerRegistrationMetrics.increment())
        assertEquals(2, ListenerRegistrationMetrics.current())
    }
}
