package si.sensum.demo

import kotlin.test.Test
import kotlin.test.assertEquals

class TestExample {

    @Test
    fun testApiName() {
            assertEquals("Desktop Demo", ApiInfo.NAME)
    }
}