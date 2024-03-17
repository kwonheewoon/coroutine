package ch11

import kotlinx.coroutines.*

data class Details(val name: String, val followers: Int)
data class Tweet(val text: String)

fun getFollowersNumber(): Int = throw Error("Service exception")

suspend fun getUserName(): String {
    delay(500)
    return "marcinmoskala"
}

suspend fun getTweets(): List<Tweet> {
    return listOf(Tweet("Hello, world"))
}

//suspend fun CoroutineScope.getUserDetails(): Details {
//    val userName = async { getUserName() }
//    val followersNumber = async {
//        delay(1000)
//        getFollowersNumber() }
//
//    val deferredNumber = try{followersNumber.await()}catch (e:Error){1}
//
//    return Details(userName.await(), deferredNumber)
//}
//
//fun main() = runBlocking {
//    val details = getUserDetails()
//    val tweets = async { getTweets() }
//    println("User: $details")
//    println("Tweets: ${tweets.await()}")
//}
// coroutineScope는 예외 발생시 자식 스코프를 모두 취소하고 예외를 다시 던짐
// 그러므로 예외를 캐치할수 있다
suspend fun getUserDetails(): Details =  coroutineScope {
        val userName = async { getUserName() }
        val followersNumber = async {
            delay(1000)
            getFollowersNumber()
        }
        val deferredNumber = try {
            followersNumber.await()
        } catch (e: Error) {
            1
        }
        Details(userName.await(), deferredNumber)
    }


fun main() = runBlocking {
    val details = try{getUserDetails()}catch (e:Error){null}
    val tweets = async { getTweets() }
    println("User: $details")
    println("Tweets: ${tweets.await()}")
}