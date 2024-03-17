package ch15

import kotlinx.coroutines.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext

class runTest{
    // runTest는 TestScope 타입이고 코루틴 시작 즉시 유휴 상태가 될 때까지 시간을 흐르게 한다
    // runTest는 TestScope를 TestScope는 StandardTestDispatcher 를, StandardTestDispatcher 는 TestCoroutineScheduler 를 포함
    @Test
    fun test1() = runTest {
        assertEquals(0, currentTime)
        delay(1000)
        assertEquals(1000, currentTime)
    }

    @Test
    fun test2() = runTest {
        assertEquals(0, currentTime)
        coroutineScope {
            launch { delay(1000) }
            launch { delay(1500) }
            launch { delay(2000) }
        }
        assertEquals(2000, currentTime)
    }
    // backgroundScope는 테스트가 기다릴 필요 없는 모든 프로세스를 시작할 때 사용한다
    @Test
    fun `should increment counter`() = runTest {
        var i = 0
        backgroundScope.launch {
            while(true){
                delay(1000)
                i++
            }
        }
        delay(1001)
        assertEquals(1, i)
        delay(1000)
        assertEquals(2,i)
    }

    suspend fun <T, R> Iterable<T>.mapAsync(
        transformation: suspend (T) -> R
    ): List<R> = coroutineScope {
        this@mapAsync.map { async { transformation(it) } }
            .awaitAll()
    }

    @Test
    fun `should map async and keep elements order`() = runTest {
        val transforms = listOf(
            suspend { delay(3000); "A" },
            suspend { delay(2000); "B" },
            suspend { delay(4000); "C" },
            suspend { delay(1000); "D" }
        )
        val res = transforms.mapAsync { it() }
        assertEquals(listOf("A", "B", "C", "D"), res)
        assertEquals(4000, currentTime)
    }
    // 중단 함수에서 컨텍스트를 확인하려면
    // currentCoroutineContext 함수나 coroutineContext 프로퍼티를 사용하면 된다
    // 코루틴 빌더의 람다식이나 스코프 함수에서는 currentCoroutineContext 함수를 사용해야 하는데
    // CoroutineScope의 coroutineContext 프로퍼티가 현재 코루틴 컨텍스트를 제공하는 프로퍼티보다 우선하기 때문
    @Test
    fun `should support context propagation`() = runTest {
        var ctx: CoroutineContext? = null
        val name1 = CoroutineName("Name 1")
        withContext(name1) {
            listOf("A").mapAsync {
                ctx = currentCoroutineContext()
                it
            }
            assertEquals(name1, ctx?.get(CoroutineName))
        }
        val name2 = CoroutineName("Some name 2")
        withContext(name2){
            listOf(1,2,3).mapAsync {
                ctx = currentCoroutineContext()
                it
            }
            assertEquals(name2, ctx?.get(CoroutineName))
        }
    }

    // 잡 취소를 확인하는 가장 쉬운 방법은 내부 함수에서 잡을 참조하고
    // 외부에서 코루틴을 취소한 뒤, 참조된 잡이 취소된 것을 확인하는 것
    @Test
    fun `should support cancellation`() = runTest {
        var job: Job? = null
        val parentJob = launch {
            listOf("A").mapAsync {
                job = currentCoroutineContext().job
                delay(Long.MAX_VALUE)
            }
        }
        delay(1000)
        parentJob.cancel()
        assertEquals(true, job?.isCancelled)
    }


}