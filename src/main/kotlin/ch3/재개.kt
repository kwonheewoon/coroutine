import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//suspend fun main(){
//    println("before")
//
//    // 중단 함수
//    suspendCoroutine<Unit> {
//        // 컨티뉴에이션 객체를 사용해 함수 중단 이후 재개
//        continuation -> continuation.resume(Unit)
//    }
//
//    println("After")
//}

//suspend fun main(){
//    println("Before")
//
//    suspendCoroutine<Unit> { continuation ->
//        // 코드 실행이 중지된 뒤 재개되는 다른 스레드를 실행
//        thread {
//            println("Suspended")
//            Thread.sleep(1000)
//            // 밑 After 문자열이 먼저 출력
//            // resume으로 중단된 지점 재개가 이루어 지기 때문
//            continuation.resume(Unit)
//            println("Resumed")
//        }
//    }
//
//    println("After")
//}

//fun continueAfterSecond(continuation: Continuation<Unit>){
//    // 스레드의 생성 비용은 크다!
//    thread {
//        Thread.sleep(1000)
//        continuation.resume(Unit)
//    }
//}
//
//suspend fun main(){
//    println("Before")
//
//    // 1초후 재개되는 함수 실행
//    suspendCoroutine<Unit> {
//        continuation -> continueAfterSecond(continuation)
//    }
//
//    println("After")
//}

private val executor = Executors.newSingleThreadScheduledExecutor{
    Thread(it, "scheduler").apply { isDaemon = true }
}

suspend fun main(){
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        executor.schedule({
            continuation.resume(Unit)
        },1000, TimeUnit.MILLISECONDS)
    }

    println("After")
}

//suspend fun printUser(token: String) {
//    println("Before")
//    val userId = getUserId(token) // 중단 함수
//    println("Got userId: $userId")
//    val userName = getUserName(userId, token) // 중단 함수
//    println(User(userId, userName))
//    println("After")
//}

// 가상의 Continuation 인터페이스와 상태
interface Continuation<in T> {
    fun resume(value: T)
    fun resumeWithException(exception: Throwable)
}

class CoroutineState(val continuation: Continuation<*>?, var label: Int)

// getUserId와 getUserName은 이제 Continuation을 받아들이고 CoroutineState를 반환합니다.
fun getUserId(token: String, continuation: Continuation<String>): CoroutineState {
    // ... 중단점을 처리하는 로직 ...
    // 예시를 위해 더미 CoroutineState를 반환합니다.
    return CoroutineState(continuation, 1)
}

fun getUserName(userId: String, token: String, continuation: Continuation<String>): CoroutineState {
    // ... 중단점을 처리하는 로직 ...
    // 예시를 위해 더미 CoroutineState를 반환합니다.
    return CoroutineState(continuation, 2)
}

// printUser 함수는 CoroutineState를 반환합니다.
//fun printUser(token: String, continuation: Continuation<Unit>): CoroutineState {
//    val state = CoroutineState(continuation, 0)
//    var userId: String? = null
//    var userName: String? = null
//
//    // 상태 머신 시작
//    while (true) {
//        when (state.label) {
//            0 -> {
//                println("Before")
//                val newState = getUserId(token, object : Continuation<String> {
//                    override fun resume(value: String) {
//                        userId = value
//                        state.label = 1
//                        printUser(token, continuation) // 재귀 호출로 상태 머신을 재개합니다.
//                    }
//                    override fun resumeWithException(exception: Throwable) { continuation.resumeWithException(exception) }
//                })
//                if (newState.label != state.label) return newState // 중단하고 상태를 반환합니다.
//            }
//            1 -> {
//                println("Got userId: $userId")
//                val newState = getUserName(userId!!, token, object : Continuation<String> {
//                    override fun resume(value: String) {
//                        userName = value
//                        state.label = 2
//                        printUser(token, continuation) // 재귀 호출로 상태 머신을 재개합니다.
//                    }
//                    override fun resumeWithException(exception: Throwable) { continuation.resumeWithException(exception) }
//                })
//                if (newState.label != state.label) return newState
//            }
//            2 -> {
//                println(User(userId!!, userName!!))
//                println("After")
//                continuation.resume(Unit) // 완료되었음을 알립니다.
//                return state
//            }
//        }
//    }
//}
