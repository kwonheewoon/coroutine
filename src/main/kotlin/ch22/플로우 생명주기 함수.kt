package ch22

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.coroutineContext

// onEach 람다식은 중단함수이며 원소는 순서대로 처리
// 따라서 onEach에 delay를 넣으면 각각의 값이 흐를 때마다 지연
//suspend fun main(){
//    flowOf(1,2,3,4)
//        .onEach { print(it) }
//        .collect()
//}

// onStart 함수는 최종 연산이 호출될 때와 같이 플로우가 시작되는 경우에 호출되는 리스너를 설정
// onStart는 첫번째 원소가 생성되는 걸 기다렸다 호출되는게 아니라는 것이 중요
//suspend fun main(){
//    flowOf(1,2)
//        .onEach { delay(1000) }
//        .onStart { println("Before") }
//        .collect { println(it) }
//}

//onStart에서도 원소를 내보낼 수 있다
//원소들은 onStart부터 아래로 흐르게 된다
//suspend fun main(){
//    flowOf(1,2)
//        .onEach { delay(1000) }
//        .onStart {
//            emit(0)
//            emit(4)
//            emit(6)
//        }
//        .collect { println(it) }
//}

//onCompletion 메서드를 사용해 플로우가 완료되었을 때 호출되는 리스너를 추가할 수 있다
//suspend fun main() = coroutineScope {
//    flowOf(1,2)
//        .onEach { delay(1000) }
//        .onCompletion { println("Completed") }
//        .collect { println(it) }
//}

// job이 cancel 되어도 onCompletion 리스너는 호출된다
//suspend fun main() = coroutineScope {
//    val job = launch {
//        flowOf(1,2)
//            .onEach { delay(1000) }
//            .onCompletion { println("Completed") }
//            .collect { println(it) }
//    }
//    delay(1100)
//    job.cancel()
//}

// onEmpty
// 플로우는 예기치 않은 이벤트가 발생하면 값을 내보내기 전에 완료될 수 있다
// onEmpty 함수는 원소를 내보내기 전에 플로우가 완료되면 실행 기본값 내보내기 위한 목적으로 사용 가능
//suspend fun main() = coroutineScope {
//    flow<List<Int>> { delay(1000) }
//        .onEmpty { emit(emptyList()) }
//        .collect { println(it) }
//}

// catch
// 예외는 아래로 흐르면서 처리하는 단계를 하나씩 닫는다
// 예외를 잡고 관리하려면 catch 메서드 사용. 이 리스너는 예외를 인자로 받고 정리를 위한 연산 수행 가능

class MyError : Throwable("My error")

//val flow = flow {
//    emit(1)
//    emit(2)
//    throw MyError()
//}

//suspend fun main(): Unit {
//    flow.onEach { println("Got $it") }
//        .catch { println("caught $it") }
//        .onCompletion { println("과연 성공인가") }
//        .collect { println("Collected $it") }
//}
// onEach는 예외에 반응하지 않는다 map, filter와 같은 다른 함수에서도 마찬가지
// 오직 Completion 핸들러만 예외가 발생했을때 호출

// onEach는 예외에 반응 X, catch 함수는 데이터 방출도 가능
suspend fun main(): Unit {
    flow{
      emit(1)
        emit(2)
        throw MyError()
    }.onEach { println("Got $it") }
        .catch { emit(78) }
        .onCompletion { println("과연 성공인가") }
        .collect { println("Collected $it") }
}

// 잡히지 않은 예외
// 플로우에서 잡히지 않은 예외는 플로우를 즉시 취소하며, collect는 예외를 다시 던진다
// 중단 함수가 예외를 처리하는 방식과 같으며, coroutineScope 또한 같은 방식으로 예외 처리
// 플로우 바깥에서 전통적인 try-catch 블록을 사용해서 예외를 잡을 수도 있다
val flow = flow {
    emit("Message1")
    throw MyError()
}
// catch 는 위에서 발생한 예외를 처리하기 때문에 최종연산에서 발생한 예외처리에는 도움이 되지 않는다
// 만약 이 소스에서 try catch 문을 없애면 예외는 함수 바깥으로 전파된다
// 그러므로 collect 연산을 onEach로 옮겨 예외를 catch 메소드가 받을 수 있게 패턴을 구성 추천
//suspend fun main(): Unit {
//    try {
//        flow.collect { println("Collected $it") } // 예외 다시 던저버려~
//    } catch (e: MyError) {
//        println("Caught")
//    }
//}

// flowOn
// (onEach, onStart, onCompletion과 같은)플로우 연산과 (flow나 channelFlow와 같은)플로우 빌더의
// 인자로 사용되는 람다식은 모두 중단 함수이다 중단함수는 컨텍스트가 필요하며 부모와의 관례를 유지
// collect가 호출되는 곳이 컨텍스트
fun usersFlow(): Flow<String> = flow {
    repeat(2) {
        val ctx = currentCoroutineContext()
        val name = ctx[CoroutineName]?.name
        emit("User$it in $name")
    }
}

//suspend fun main(){
//    val users = usersFlow()
//    withContext(CoroutineName("Name1")) {
//        users.collect { println(it) }
//    }
//    withContext(CoroutineName("Name2")) {
//        users.collect { println(it) }
//    }
//}

// flowOn 코루틴 컨텍스트를 변경
suspend fun present(place: String, message: String){
    val ctx = coroutineContext
    val name = ctx[CoroutineName]?.name
    println("[$name] $message on $place")
}

fun messagesFlow(): Flow<String> = flow {
    present("flow Builder", "Message")
    emit("Message")
}

//suspend fun main() {
//    val users = messagesFlow()
//    withContext(CoroutineName("Name1")) {
//        users
//            .flowOn(CoroutineName("Name3")) // messageFlow 시작은 Name3
//            .onEach { present("onEach", it) }
//            .flowOn(CoroutineName("Name2")) // 위 onEach 메시지는 Name2
//            .collect { present("collect", it) } // flowOn 함수가 없으니 withContext 스코프에서 지정된 Name1
//    }
//}
// launchIn
// collect는 플로우가 완료될 때까지 코루틴을 중단하는 중단 연산
// launch 빌더로 collect를 래핑하면 플로우를 다른 코루틴에서 처리할 수 있다
// 플로우의 확장 함수인 launchIn을 사용하면 유일한 인자로 스코프를 받아 collect를 새로운 코루틴에서 시작가능

fun <T> Flow<T>.launchIn(scope: CoroutineScope): Job =
    scope.launch { collect() }

// 별도의 코루틴에서 플로우를 시작하기 위해 launchIn을 주로 사용
//suspend fun main(): Unit = coroutineScope {
//    flowOf("User1", "User2")
//        .onStart { println("Users:") }
//        .onEach { println(it) }
//        .launchIn(this)
//}