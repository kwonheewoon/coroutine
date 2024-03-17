package ch19

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime

// 플로우 빌더는 중단 함수가 아니기 때문에
// CoroutineScope가 필요하지 않습니다
fun usersFlow(): Flow<String> = flow {
    repeat(3) {
        delay(1000)
        val ctx = currentCoroutineContext()
        val name = ctx[CoroutineName]?.name
        emit("Users$it in $name")
    }

}
// collect 최종연산 함수는 불로킹 함수가 아니기 때문에
// 다른 스코프도 같이 처리된다 그러므로 다른 스코프에서 현재 스코프 취소시
// 안전하게 잡 취소가 된다
suspend fun main() {
    val users = usersFlow()

    withContext(CoroutineName("Name")) {
        val job = launch {
            // collect는 중단 함수
            users.collect { println(it) }
        }
        launch {
            delay(2100)
            println("I got enough")
            job.cancel()
        }
    }
}

// 플로우 사용 예제 소스코드

// 주식 가격 정보를 나타내는 데이터 클래스
data class StockPrice(val symbol: String, val price: Double, val timestamp: LocalDateTime)

// 주식 가격을 실시간으로 생성하는 함수
fun getStockPriceStream(symbol: String): Flow<StockPrice> = flow {
    while (true) {
        emit(StockPrice(symbol, (100..200).random().toDouble(), LocalDateTime.now()))
        delay(1000) // 1초에 한 번씩 업데이트
    }
}

// 주식의 역사적 가격 데이터를 조회하는 중단함수
suspend fun getHistoricalPriceData(symbol: String): List<Double> {
    delay(500) // 네트워크 요청을 시뮬레이션
    return List(5) { (50..150).random().toDouble() } // 무작위 역사적 가격 데이터 생성
}

//fun main() = runBlocking {
//    val stockSymbol = "XYZ"
//
//    // 주식 가격 스트림을 생성하고 처리
//    getStockPriceStream(stockSymbol)
//        .onEach { println("Current Price of $stockSymbol: $it") } // 현재 가격 출력
//        .flatMapConcat { price ->
//            flow {
//                // 역사적 가격 데이터 조회
//                val historicalData = getHistoricalPriceData(price.symbol)
//                emit(price to historicalData) // 현재 가격과 역사적 데이터를 함께 방출
//            }
//        }
//        .collect { (price, historicalData) ->
//            println("Processed $price with historical data: $historicalData")
//        }
//}
