package si.sensum.backend

import kotlin.test.Test
import kotlin.test.assertEquals

class TestExample {

    @Test
    fun testApiName() {
        assertEquals("Backend Core", ApiInfo.NAME)
    }
}