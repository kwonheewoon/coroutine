package ch23

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
suspend fun main(){
    val flow = flowOf(1,2,3,4)
        .onEach { delay(100) }
    val res = flow.scan(0) {acc, i -> acc + i}
    res.collect { println(it) }
}