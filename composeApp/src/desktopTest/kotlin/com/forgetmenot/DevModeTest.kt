package com.forgetmenot

import kotlin.test.Test
import kotlin.test.assertEquals

class DevModeTest {

    @Test
    fun theBarIsOffWhenTheVariableIsUnsetOrEmpty() {
        assertEquals(false, devToolsEnabled(null))
        assertEquals(false, devToolsEnabled(""))
        assertEquals(false, devToolsEnabled("  "))
    }

    @Test
    fun theUsualWaysOfSayingYesAllWork() {
        for (yes in listOf("1", "true", "TRUE", "yes", "on", " 1 ")) {
            assertEquals(true, devToolsEnabled(yes), """expected "$yes" to enable dev tools""")
        }
    }

    @Test
    fun anythingElseLeavesItOff() {
        // Notably "0" and "false", which a shell script might set explicitly.
        for (no in listOf("0", "false", "no", "off", "maybe")) {
            assertEquals(false, devToolsEnabled(no), """expected "$no" to leave dev tools off""")
        }
    }
}
