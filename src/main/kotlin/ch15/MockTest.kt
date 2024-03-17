package ch15

import io.mockk.coEvery
import kotlinx.coroutines.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class MockTest {

    @Test
    fun `should load data concurrently`() = runTest {
        coEvery {

        } coAnswers {

        }
    }
}