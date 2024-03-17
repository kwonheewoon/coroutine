import ch23.flowOf
import ch23.map
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

// 재시도
// 예외는 플로우를 따라 흐르면서 각 단계를 하나씩 종료합니다.
// 종료된 단계는 비활성화 되기 때문에, 예외가 발생한 뒤 메시지를 보내는 건 불가능하지만, 각 단계가 이전 단계에 대한 참조를 가지고 있으며,
// 플로우를 다시 시작하기 위해 참조를 사용할 수 있습니다. 이 원리에 기반해 코틀린은 retry와 retryWhen 함수를 제공

// retryWhen 구현
fun <T> Flow<T>.retryWhen(
    predicate: suspend FlowCollector<T>.(
            cause: Throwable,
            attempt: Long
            ) -> Boolean
): Flow<T> = flow {
    var attempt = 0L
    do {
        val shallRetry = try {
            collect { emit(it) }
            false
        } catch (e: Throwable) {
            predicate(e, attempt++)
                .also { if(!it) throw e }
        }
    } while (shallRetry)
}

// retryWhen은 플로우의 이전 단계에서 예외가 발생할때마다 조건자(predicate)를 확인
// 여기서 조건자는 예외가 무시되고 이전 단계가 다시 시작되어야 하는지, 또는 플로우를 계속해서 종료해야하는지를 결정 대부분 몇 번까지 재시도할지,
// 특정 예외 클래스가 발생했을 때만 처리할지를 명시
// 이럴 때 내부적으로 retryWhen을 사용하는 retry 함수를 사용

// retry 구현
fun <T> Flow<T>.retry(
    retries: Long = Long.MAX_VALUE,
    predicate: suspend (cause: Throwable) -> Boolean = {true}
): Flow<T> {
    // false 일때 아래 구문 실행 IllegalArgumentException 발생
    require(retries > 0) {
        "Expected positive amount of retries, but had $retries"
    }

    return retryWhen {cause, attempt ->
        attempt < retries && predicate(cause)
    }
}

//suspend fun main(){
//    flow {
//        emit(1)
//        emit(2)
//        error("E")
//        emit(3)
//    }.retry(3) {
//        print(it.message)
//        true
//    }.collect { print(it) }
//}

// retry를 사용하는 몇 가지 예시
// 로그를 남기고 새로운 연결을 맺는 걸 시도할 때 시간 간격을 준다
//fun makeConnection(config: ConnectionConfig) = api
//    .startConnection(config)
//    .retry { e ->
//        delay(1000)
//        log.error(e) {"Error for $config"}
//        true
//    }

// 예외가 특정 타입일 때 또는 특정 타입이 아닌 경우에만 재시도하는 조건자 구현
//fun makeConnection(config: ConnectionConfig) = api
//    .startConnection(config)
//    .retryWhen { e, attempt ->
//        delay(100 * attempt)
//        log.error(e) { "Error for $config" }
//        e is ApiException && e.code ! in 400..499
//    }

// 중복제거 함수 distinctUntilChanged
// 바로 이전의 원소와 동일한 원소만 emit 하지 않는다.
private val NOT_SET = Any()
fun <T> Flow<T>.distinctUntilChanged(): Flow<T> = flow {
    var previous: Any? = NOT_SET
    collect {
        if(previous == NOT_SET || previous != it) {
            emit(it)
            previous = it
        }
    }
}

//suspend fun main() {
//    flowOf(1, 2, 2, 3, 2, 1, 1, 3)
//        .distinctUntilChanged()
//        .collect { print(it) }
//}

data class User(val id: Int, val name: String) {
    override fun toString(): String = "[$id] $name"
}

// 중복제거 함수 distinctUntilChangedBy
// 중복제거의 기준이 될 키를 인자로 넘겨 중복제거 기본적으로 distinctUntilChanged 와 작동 방식은 동일
//suspend fun main() {
//    val users = flowOf(
//        User(1, "Alex"),
//        User(1, "Bob"),
//        User(2, "Bob"),
//        User(2, "Celine")
//    )
//
//    println(users.distinctUntilChangedBy { it.id }.toList())
//    println(users.distinctUntilChangedBy { it.name }.toList())
//}

// 최종 연산
// 최종연산에는 collect, 컬렉션과 Sequence가 제공하는 것과 비슷한 연산인 count, first, firstOrNull, fold, reduce(원소들을 누적 계산하여 하나의 객체를 만드는)
// 최종연산은 중단 가능하며 플로우가 완료되었을 때 (또는 최종 연산 자체가 플로우를 완료시켰을 때) 값을 반환

suspend fun main(){
    val flow = flowOf(1, 2, 3, 4)
        .map { it * it }

    println(flow.first()) // 1
    println(flow.count()) // 4

    println(flow.reduce { acc, value -> acc * value }) // 576
    println(flow.fold(0) {acc, value -> acc + value})


}

// 최종연산 커스텀
// Int 플로우의 sum 구현
suspend fun Flow<Int>.sum(): Int {
    var sum = 0
    collect { value ->
        sum += value
    }
    return sum
}