package ch14

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlin.system.measureTimeMillis

val dispatcher = Dispatchers.IO
    .limitedParallelism(1)

var counter = 0

// 싱글스레드로 래핑
//suspend fun massiveRun(action: suspend () -> Unit) =
//    withContext(Dispatchers.Default) {
//        repeat(1000){
//            launch {
//                repeat(1000) {action()}
//            }
//        }
//    }
//
//fun main() = runBlocking {
//    massiveRun {
//        withContext(dispatcher) {
//            counter++
//        }
//    }
//    println(counter)
//}

// 코스 그레인드 스레드 한정
// 함수 본체는 스레드 블로킹
//class UserDownloader(
//    private val api: NetworkService
//){
//    private val users = mutableListOf<User>()
//    private val dispatcher = Dispatchers.IO.limitedParallelism(1)
//
//    suspend fun downloaded(): List<User> =
//        withContext(dispatcher) {
//            users.toList()
//        }
//
//    suspend fun fetchUser(id: Int) = withContext(dispatcher){
//        val newUser = api.fetchUser(id)
//        users += newUser
//    }
//}
// 파인 그레이드 스레드 한정
// 동시성 부분의 상태변경이 요구되는 부분에만 래핑
//class UserDownloader(
//    private val api: NetworkService
//){
//    private val users = mutableListOf<User>()
//    private val dispatcher = Dispatchers.IO.limitedParallelism(1)
//
//    suspend fun downloaded(): List<User> =
//        withContext(dispatcher) {
//            users.toList()
//        }
//
//    suspend fun fetchUser(id: Int) = withContext(dispatcher){
//        val newUser = api.fetchUser(id)
//        withContext(dispatcher){
//            users += newUser
//        }
//    }
//}

// 뮤텍스
// Lock 기반 동시성 제어
// 단 하나의 코루틴만 lock 과 unlock 사이에 있을 수 있다
// 스레드 블로킹되지 않고 중단이 된다
//suspend fun main() = coroutineScope {
//    repeat(5){
//        launch {
//            delayAndPrint()
//        }
//    }
//}
//
//val mutex = Mutex()
//
//// lock 과 unlock를 직접 제어하는 것은 예외 발생시 lock이 풀리지 않는 deadlock이 발생할수 있으므로
//// withLock를 사용하여 블록내에서 어떤 예외가 발생해도 안전하게 lock을 해제하는 메소드를 사용하자
////suspend fun delayAndPrint() {
////    mutex.lock()
////    delay(1000)
////    println("Done")
////    mutex.unlock()
////}
//suspend fun delayAndPrint() {
//    mutex.withLock {
//        delay(1000)
//        println("Done")
//    }
//}

class MessagesRepository {
    private val messages = mutableListOf<String>()
    private val mutex = Mutex()

    suspend fun add(message: String) = mutex.withLock {
        delay(1000)
        messages.add(message)
    }
}

suspend fun main(){
    val repo = MessagesRepository()

    val timeMillis = measureTimeMillis {
        coroutineScope {
            repeat(5) {
                launch {
                    repo.add("Message$it")
                }
            }
        }
    }
    println(timeMillis)
}

// 세마포어는 동시 접근의 스레드를 제한
// 동시성 문제를 해결할수는 없지만 처리율 제한 장치 구현에 도움됨

class LimitedNetworkUserRepository(
){
    private val semaphore = Semaphore(10)

    suspend fun requestUser(userId: String){
        semaphore.withPermit {
            // api 호출
        }
    }
}

