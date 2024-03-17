package ch20

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

fun <T, K> Flow<T>.distinctBy(
    keySelector: (T) -> K
) = flow {
    val sentKeys = mutableSetOf<K>()
    collect { value ->
        val key = keySelector(value)
        if (key !in sentKeys) {
            sentKeys.add(key)
            emit(value)
        }
    }
}

fun kotlinx.coroutines.flow.Flow<*>.counter() = kotlinx.coroutines.flow.flow<Int> {
    var counter = 0
    collect {
        counter++
        List(100) { Random.nextLong() }.shuffled().sorted()
        emit(counter)
    }
}

fun kotlinx.coroutines.flow.Flow<*>.counter2(): kotlinx.coroutines.flow.Flow<Int> {
    var counter = 0
    return this.map {
        counter++
        List(100) { Random.nextLong() }.shuffled().sorted()
        counter
    }
}

//suspend fun main(): Unit = coroutineScope {
//    val f1 = List(1000) { "$it" }.asFlow()
//    val f2 = List(1000) { "$it" }.asFlow().counter()
//
//    launch { println(f1.counter().last()) }
//    launch { println(f1.counter().last()) }
//    launch { println(f2.last()) }
//    launch { println(f2.last()) }
//
//}

suspend fun main(): Unit = coroutineScope {
    val f1 = List(1000) { "$it" }.asFlow()
    val f2 = List(1000) { "$it" }.asFlow().counter2()

    launch {
        println("f1-1 "+f1.counter2().last()) }
    launch {
        println("f1-2 "+f1.counter2().last()) }
    launch {
        println("f2-1 "+f2.last()) }
    launch {
        println("f2-2 "+f2.last()) }

}