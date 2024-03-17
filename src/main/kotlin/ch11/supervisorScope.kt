package ch11

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

// supervisorScope는 기본적으로 coroutineScope와 같지만
// Job을 SupervisorJob으로 오버라이딩하므로 자식의 예외가 전파되지 않는다
fun main() = runBlocking {
    println("Before")

    supervisorScope {
        launch {
            delay(1000)
            throw Error()
        }

        launch {
            delay(2000)
            println("Done")
        }
    }

    println("After")
}