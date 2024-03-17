package ch7

import ch7.MyCustomContext.Key
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

//fun main() {
//    val ctx: CoroutineContext = CoroutineName("A name")
//
//    val coroutineName: CoroutineName? = ctx[CoroutineName]
//    println(coroutineName?.name)
//    val job: Job? = ctx[Job]
//    println(job)
//
//}

// 원소 더하기
//fun main(){
//    val ctx1: CoroutineContext = CoroutineName("Name1")
//    println(ctx1[CoroutineName]?.name) // Name1
//    println(ctx1[Job]?.isActive) // null
//
//    val ctx2: CoroutineContext = Job()
//    println(ctx2[CoroutineName]?.name) // null
//    println(ctx2[Job]?.isActive) // 'Active'
//
//    val ctx3 = ctx1 + ctx2
//    println(ctx3[CoroutineName]?.name) // Name1
//    println(ctx3[Job]?.isActive) // true
//}

// 원소 제거
//fun main(){
//    var ctx = CoroutineName("Name1") + Job()
//    println(ctx)
//    ctx = ctx.minusKey(CoroutineName)
//    println(ctx[CoroutineName]?.name)
//}

// 컨텍스트 폴딩
//fun main(){
//    val ctx = CoroutineName("Name1") + Job()
//    val ctx2 = CoroutineName("Name222") + Job()
//
//    ctx.fold("") {
//        acc, element -> "$acc$element"
//    }.also(::println)
//
//    val empty = emptyList<CoroutineContext>()
//    val list = listOf<CoroutineContext>(ctx2)
//    ctx.fold(list) {acc, element -> acc + element}
//        .joinToString()
//        .also(::println)
//}

// 중단함수에서 컨텍스트에 접근
//suspend fun printName(){
//    println(coroutineContext[CoroutineName]?.name)
//}
//
//suspend fun main() = withContext(CoroutineName("Outer")){
//    printName() // Outer
//    launch(CoroutineName("Inner")) {
//        printName()
//    }
//    delay(10)
//    printName()
//}

// 컨텍스트를 개별적으로 생성
class MyCustomContext : CoroutineContext.Element{
    override val key: CoroutineContext.Key<*> = Key

    companion object Key :
            CoroutineContext.Key<MyCustomContext>
}

// CounterContext 클래스도 결국 CoroutineName 와 똑같은 기능을 하는 커스텀 CoroutineName 이다
class CounterContext(
    private val name: String
) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key
    private var nextNumber = 0

    fun printNext() {
        println("$name: $nextNumber")
        nextNumber++
    }
    companion object Key:CoroutineContext.Key<CounterContext>
}

suspend fun printNext(){
    coroutineContext[CounterContext]?.printNext()
}

suspend fun main(): Unit =
    withContext(CounterContext("Outer")){
        printNext()
        launch {
            printNext()
            launch {
                printNext()
            }
            launch(CounterContext("Inner")) {
                printNext()
                printNext()
                launch {
                    printNext()
                }
            }
        }
        printNext()
    }