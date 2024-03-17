package ch15

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class mosin{
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testRunCurrentExample() = runTest {
        // TestCoroutineScope를 사용하여 테스트 환경 설정

        var result = 0

        // 코루틴 시작
        launch {
            delay(1000) // 1초 지연
            result = 10  // result 값을 변경
        }

        assertEquals(0, result) // delay 전에는 result 값이 변경되지 않았음을 확인

        // 현재 대기 중인 코루틴 작업 실행
        runCurrent()

        // runCurrent 이후에도 지연 시간이 경과하지 않았으므로, result는 여전히 변경되지 않았어야 함
        assertEquals(0, result)

        // 1초를 진행시켜 지연을 완료
        //advanceTimeBy(1000)
        advanceUntilIdle()

        // 이제 result 값이 변경되었음을 확인
        assertEquals(10, result)
    }
}