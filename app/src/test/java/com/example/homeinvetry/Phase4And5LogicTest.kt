package com.example.homeinvetry

import com.example.homeinvetry.core.common.VoiceCommandParser
import org.junit.Assert.assertEquals
import org.junit.Test

class Phase4And5LogicTest {

    @Test
    fun testVoiceCommandParser() {
        val spoken = "add 5 kg of basmati rice to kitchen"
        val parsed = VoiceCommandParser.parseAddCommand(spoken)

        assertEquals(5.0, parsed.quantity, 0.001)
        assertEquals("kg", parsed.unit)
        assertEquals("Kitchen", parsed.location)
        assertEquals("Basmati Rice", parsed.name)
    }

    @Test
    fun testVoiceCommandParserLiquid() {
        val spoken = "add 2 litres of cooking oil to pantry"
        val parsed = VoiceCommandParser.parseAddCommand(spoken)

        assertEquals(2.0, parsed.quantity, 0.001)
        assertEquals("litre", parsed.unit)
        assertEquals("Pantry", parsed.location)
        assertEquals("Cooking Oil", parsed.name)
    }
}
