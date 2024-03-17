package ch21

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// awaitClose {...} : 채널이 닫힐 때까지 중단되는 함수입니다 채널이 닫힌 다음에 인자로
// 들어온 함수가 실행됩니다. awaitClose는 callbackFlow에서 아주 중요합니다
// trySendBlocking : send와 비슷하지만 중단하는 대신 블로킹하여 중단 함수가 아닌 함수에서도 사용 가능
// close() : 채널을 닫는다
// cancel(throwable) 채널을 종료하고 플루어에 예외를 던진다

