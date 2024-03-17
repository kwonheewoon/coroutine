package ch21

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// 플로우 빌더
fun makeFlow(): Flow<Int> = flow {
    repeat(3){
        num -> emit(num)
    }
}

suspend fun main() {
    makeFlow().collect{ println(it) }
}