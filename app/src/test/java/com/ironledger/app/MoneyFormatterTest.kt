package com.ironledger.app

import com.ironledger.app.core.money.MoneyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyFormatterTest {
    @Test
    fun parseToPaise_convertsDecimalRupees() {
        assertEquals(12_345L, MoneyFormatter.parseToPaise("123.45"))
        assertEquals(1_200_00L, MoneyFormatter.parseToPaise("INR 1,200"))
    }

    @Test
    fun parseToPaise_rejectsInvalidAmounts() {
        assertNull(MoneyFormatter.parseToPaise(""))
        assertNull(MoneyFormatter.parseToPaise("-10"))
        assertNull(MoneyFormatter.parseToPaise("premium"))
    }

    @Test
    fun formatINR_masksWhenHidden() {
        assertEquals("INR ****", MoneyFormatter.formatINR(1_000_00, hidden = true))
    }
}
