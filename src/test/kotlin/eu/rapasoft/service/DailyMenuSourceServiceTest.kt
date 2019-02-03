package eu.rapasoft.service

import org.junit.Assert.assertEquals
import org.junit.Test

class DailyMenuSourceServiceTest {

    @Test
    fun getSources() {
        assertEquals(16, DailyMenuSourceService().sources.size)
    }
}