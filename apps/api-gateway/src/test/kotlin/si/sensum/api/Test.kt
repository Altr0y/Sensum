package si.sensum.api

import kotlin.test.Test
import kotlin.test.assertEquals

class TestExample {

    @Test
    fun testApiName() {
        assertEquals("API Gateway", ApiInfo.NAME)
    }
}