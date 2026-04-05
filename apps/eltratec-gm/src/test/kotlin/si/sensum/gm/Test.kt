package si.sensum.gm

import kotlin.test.Test
import kotlin.test.assertEquals

class TestExample {

    @Test
    fun testApiName() {
        assertEquals("Eltratec GM", ApiInfo.NAME)
    }
}