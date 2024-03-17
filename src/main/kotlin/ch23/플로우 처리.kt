package ch23

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

// map
//suspend fun main() {
//    flowOf(1,2,3)
//        .map { it * it }
//        .collect { println(it) }
//}

// 컬렉션 처리 구현

fun <T,R> Flow<T>.map(
    transform: suspend (value: T) -> R
): Flow<R> = flow {
    collect { // 여기서 collect는 수신객체(Flow 인스턴스) 안의 collect 메소드를 호출하는것 그러니 수신객체(Flow 인스턴스)의 스트림을 순회한다
        value -> emit(transform(value)) // emit은 this(FlowCollector) 메소드로 데이터를 방출
    }
}

fun <T> flowOf(vararg elements: T): Flow<T> = flow {
    for (element in elements) {
        this.emit(element) // emit은 this(FlowCollector) 메소드로 데이터를 방출
    }
}

//suspend fun main() {
//    ch23.flowOf("A","B")
//        .map { it.lowercase() }
//        .collect { print(it) }
//}

// meerge
// 한 플로우의 원소가 다른 플로우를 기다리지 않는다
//suspend fun main(){
//    val ints: Flow<Int> = ch23.flowOf(1,2,3)
//    val doubles: Flow<Double> = ch23.flowOf(0.1, 0.2, 0.3)
//
//    val together: Flow<Number> = merge(ints, doubles) // Int, Double 의 부모타입 Number 타입의 Flow로 merge
//    println(together.toList())
//}

// 다른 플로우 원소 생성이 되지 않아도 merge 대상의 다른 플로우 원소 생성이 중단되지 않고 별개로 생성
//suspend fun main(){
//    val ints: Flow<Int> = ch23.flowOf(1,2,3).onEach { delay(1000) }
//    val doubles: Flow<Double> = ch23.flowOf(0.1, 0.2, 0.3)
//
//    val together: Flow<Number> = merge(ints, doubles) // Int, Double 의 부모타입 Number 타입의 Flow로 merge
//    together.collect { println(it) }
//}

// zip
// 두 플로우로부터 쌍을 만드는 zip
// 각 원소는 한 쌍의 일부가 되므로 쌍이 될 원소를 기다려야 한다
// 쌍을 이루지 못하고 남은 원소는 유실된다
//suspend fun main(){
//    val flow1 = flowOf("A", "B", "C","D","E")
//        .onEach { delay(400) }
//    val flow2 = flowOf(1,2,3,4)
//        .onEach { delay(1000) }
//
//    flow1.zip(flow2) { f1, f2 -> "${f1}_${f2}" }
//        .collect { println(it) }
//}


// combine
// 두 데이터 소스의 변화를 능동적으로 감지할 때 주로 사용
// 변화가 발생할 때마다 원소가 내보내지길 원한다면(첫 쌍을 가지도록) 합쳐질 각 플로우에 초기 값을 더하면 됨

//suspend fun main(){
//    val flow1 = flowOf("A", "B", "C")
//        .onEach { delay(400) }
//    val flow2 = flowOf(1,2,3,4)
//        .onEach { delay(1000) }
//
//    flow1.combine(flow2) { f1, f2 -> "${f1}_${f2}" }
//        .collect { println(it) }
//    //(1초 후)
////    B_1
////    (0.2초 후)
////    C_1
////    (0.8초 후)
////    C_2
////    (1초 후)
////    C_3
////    (1초 후)
////    C_4
//
//}

// fold(List 컬렉션에서도 사용 가능)
// 누적 연산이고 최종 연산이다
// 초기 값부터 시작해 주어진 원소 각각에 대해 두 개의 값을 하나로 합치는 연산을 적용해 컬렉션의 모든 값을 하나로 합침
//suspend fun main(){
//    val flow = flowOf(1,2,3,4)
//        .onEach { delay(1000) }
//    val res = flow.fold(0) {acc, i -> acc + i}
//    println(res)
//}

// scan(List 컬렉션에서도 사용 가능)
// fold와 비슷하지만 누적되는 값을 모두 방출하는 오퍼레이터이며 중간연산 이다
// scan은 변경해야 할 사항을 플로우로 가지고 있으며, 변경 내역에 대한 객체가 필요할 때 주로 사용
//suspend fun main(){
//    val flow = flowOf(1,2,3,4)
//        .onEach { delay(100) }
//    val res = flow.scan(0) {acc, i -> acc + i}
//    res.collect { println(it) }
//}

// flow 빌더와 collect를 사용해 scan을 쉽게 구현할 수 있다.
// 초기 값을 먼저 내보낸 뒤, 새로운 원소가 나올 때마다 다음 값이 누적된 결과를 내보내면 된다
//fun <T,R> Flow<T>.scan(
//    initial: R,
//    operation: suspend (accmulator: R, value: T) -> R
//): Flow<R> = flow {
//    var accmulator: R = initial
//    emit(accmulator) // 초기 값을 방출
//    collect { value ->
//        accmulator = operation(accmulator, value)
//        emit(accmulator) // operation 함수형 인터페이스 호출후 계산된 값 방출
//    }
//}
//suspend fun main(){
//    val flow = flowOf(1,2,3,4)
//        .onEach { delay(100) }
//    val res = flow.scan(0) {acc, i -> acc + i}
//    res.collect { println(it) }
//}

// flatMap
// 컬렉션의 경우, flatMap은 맵과 비슷하지만 변환 함수가 평탄화된 컬렉션을 반환해야 한다는 점이 다르다
// 예를 들어 부서 목록을 가지고 있고 각 부서가 사원 목록을 가지고 있다면 flatMap을 사용해 전체 부서의 사원 목록을 만들수 있다.
// val allEmployees: List<Employee> = departments
//     .flatMap { department -> department.employees }
// 맵을 사용하면 리스트의 리스트를 대신 얻게 된다
// val listOfListOfEmployee: List<List<Employee>> = departments
//     .map { department -> department.employees }
// 플로우에서의 flatMap은 변환 함수가 평탄화된 플로우를 반환한다고 생각하는 것이 직관적이다
// 문제는 플로우 원소가 나오는 시간이 다르다는 것이다.
// 두 번째 원소에서 만들어진 플로우가 첫 번째 플로우에서 만들어진 원소를 기다리는가, 동시에 처리하는가?
// 이런 이유 때문에 Flow에는 flatMap 함수가 없으며, flatMapConcat, flatMapMerge, flatMapLatest와 같은 다양한 함수가 있다.

// flatMapConcat 함수는 생성된 플로우를 하나씩 처리한다.
// 그래서 두 번째 플로우는 첫 번째 플로우가 완료되었을 때 시작할 수 있다.
fun flowFrom(elem: String) = flowOf(1, 2, 3)
    .onEach { delay(1000) }
    .map { "${it}_${elem}" }

//suspend fun main() {
//    flowOf("A", "B", "C")
//        .flatMapConcat { flowFrom(it) }
//        .collect{ println(it) }
//
//    // (1초 후)
//    // 1_A
//    // (1초 후)
//    // 2_A
//    // (1초 후)
//    // 3_A
//    // ...
//}

// flatMapMerge
// 만들어진 플로우를 동시에 처리
// concurrency 인자를 사용해 동시에 처리할 수 있는 플로우의 수를 설정 가능
//suspend fun main() {
//    flowOf("A", "B", "C")
//        .flatMapMerge { flowFrom(it) }
//        //.flatMapMerge(concurrency = 2) { flowFrom(it) }
//        .collect { println(it) }
//    // (1초 후)
//    // 1_A
//    // 1_C
//    // 1_B
//    // (1초 후)
//    // 2_B
//    // ...
//}

// async 대신 플로우와 함께 flatMapMerge를 사용하면 두 가지 이점
// 1. 동시성 인자를 제어하고(같은 시간에 수백 개의 요청을 보내는 걸 피하기 위해) 같은 시간에 얼마만큼의 종류를 처리할지 결정할 수 있다.
// 2. Flow를 반환해 데이터가 생성될 때마다 다음 원소를 보낼 수 있다 (함수를 사용하는 측면에서 보면 데이터를 즉시 처리할 수 있다.)

//suspend fun getOffers(
//    categories: List<Category>
//): List<Offer> = coroutineScope {
//    categories
//        .map { async { api.requestOffers(it) } } // List<Deferred>
//        .flatMap { it.await() } // List<Offer>
//}

//suspend fun getOffers(
//    categories: List<Category>
//): List<Offer> = categories
//    .asFlow()
//    .flatMapMerge(concurrency = 20) {
//
//        suspend { api.requestOffers(it) }.asFlow()
//        // 또는 flow { emit(api.requestOffers(it)) }
//    }
//}

// flatMapLatest
// 이 메소드는 새로운 플로우가 나타나면 이전에 처리하던 플로우를 잊는다.
// 새로운 값이 나올 때마다 이전 플로우 처리는 사라져 버린다.
// "A", "B", "C" 사이에 지연이 없다면 "1_C", "2_C", "3_C"

//suspend fun main() {
//    flowOf("A", "B", "C")
//        .onEach { delay(1200) }
//        .flatMapLatest { flowFrom(it) }
//        .collect { println(it) }
//}