package ch8

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

// 모든 코루틴 빌더는 부모 잡을 기초로 자신만의 잡을 생성한다

//suspend fun main() = coroutineScope {
//    // 빌더로 생성된 잡은
//    val job = Job()
//    println(job)
//    // 메서드로 완료시킬 때까지 Active 상태입니다.
//    job.complete()
//    println(job)
//
//    // launch는 기본적으로 활성화되어 있습니다.
//    val activeJob = launch {
//        // launch 확장함수를 변수에 대입할때는 바로 실행되지 않는다
//        delay(1000)
//    }
//    println(activeJob)
//    // 여기서 잡이 완료될 때까지 기다립니다.
//    activeJob.join()
//    println(activeJob)
//
//    // launch는 New 상태로 지연 시작됩니다.
//    val lazyJob = launch( start = CoroutineStart.LAZY ){
//        delay(1000)
//    }
//    println(lazyJob)
//    // Active 상태가 되려면 시작하는 함수를 호출해야 합니다.
//    lazyJob.start()
//    println(lazyJob)
//    lazyJob.join()
//    println(lazyJob)
//}

// async 함수에 의해 반환되는 타입은 Deferred<T>이며 Deferred 또한 Job 인터페이스를 구현하고 있다
//fun main(): Unit = runBlocking {
//    val deferred: Deferred<String> = async {
//        delay(1000)
//        "Test"
//    }
//    val job: Job = deferred
//    println(job)
//    job.join()
//    println(job)
//}

//Job 확장 프로퍼티
//val CoroutineContext.job: Job
//    get() = get(Job) ?: error("Current context dosen't...")
//
//fun main(): Unit = runBlocking {
//    println(coroutineContext.job)
//}

//Job은 코루틴이 상속하지 않는 유일한 코루틴 컨텍스트이다
//fun main(): Unit = runBlocking {
//    val name = CoroutineName("Some name")
//    val job = Job()
//
//    launch (name + job){
//        val childName = coroutineContext[CoroutineName]
//        println(childName == name)
//        val childJob = coroutineContext[Job]
//        println(childJob == job) //childJob은 현재 launch내의 새로운 잡이다
//        println(childJob == job.children.first())
//    }
//}

//fun main(): Unit = runBlocking {
//    val parentJob = this.coroutineContext.job
//    launch(Job()) {// 자식 코루틴이 독자적인 Job을 생성하므로 위의 parentJob과 연관성이 없다 동시성을 잃게됨
//        // parentJob.children 시퀀스는 비어있다 자식 잡과 부모 잡의 연관이 없기 때문
//        println(parentJob.children.first() == this.coroutineContext.job)
//        delay(1000)
//        println("Will not be printed")
//    }
//    delay(1000)
//}

// 자식 잡 기다리기
//fun main(): Unit = runBlocking {
//    val job1 = launch {
//        delay(1000)
//        println("Test1")
//    }
//
//    val job2 = launch {
//        delay(2000)
//        println("Test2")
//    }
//
//    job1.join()
//    job2.join()
//    println("All tests are done")
//}
// 부모잡에 연관된 자식잡을 모두 기다리기
//fun main(): Unit = runBlocking {
//    launch {
//        delay(1000)
//        println("Test1")
//    }
//
//    launch {
//        delay(2000)
//        println("Test2")
//    }
//
//    val children = coroutineContext[Job]?.children
//
//    val childrenNum = children?.count()
//    println("Number of children: $childrenNum")
//    children?.forEach { it.join() }
//    println("All tests are done")
//}
// 잡 팩토리 함수(영원히 대기)
//suspend fun main(): Unit = coroutineScope {
//    val job = Job()
//    launch(job) {// 새로운 잡이 부모로부터 상속받은 잡을 대체
//        delay(1000)
//        println("Text 1")
//    }
//    launch(job) {
//        delay(2000)
//        println("Text 2")
//    }
//    job.join()
//    println("Will not be printed")
//}
// 모든 자식 잡을 기다리므로 안전하게 부모 잡 종료
//suspend fun main(): Unit = coroutineScope {
//    val job = Job()
//    launch(job) {// 새로운 잡이 부모로부터 상속받은 잡을 대체
//        delay(1000)
//        println("Text 1")
//    }
//    launch(job) {
//        delay(2000)
//        println("Text 2")
//    }
//    job.children.forEach { it.join() }
//    println("Will not be printed")
//}
// complete 함수 호출
//suspend fun main(): Unit = runBlocking {
//    val job = Job()
//
//    launch(job) {
//        repeat(5){
//            num -> delay(200)
//            println("Rep$num")
//        }
//    }
//
//    launch {
//        delay(500)
//        job.complete()
//    }
//
//    job.join()
//
//    launch(job) {
//        println("Will not be printed")
//    }
//
//    println("Done")
//}

// completeExceptionally job 상태 Cancelled로 변경
//suspend fun main(): Unit = runBlocking {
//    val job = Job()
//
//    launch(job) {
//        repeat(5) {
//            num -> delay(200)
//            println("Rep$num")
//        }
//    }
//
//    launch {
//        delay(500)
//        job.completeExceptionally(Error("Some error"))
//    }
//
//    job.join()
//
//    launch(job) {
//        println("Will nt be printed")
//    }
//
//    println("Done state=${job}")
//}

//부모 잡 취소(자식 잡까지 실행되다 전부 취소)
suspend fun main(): Unit = coroutineScope {
    val parentJob = Job()
    val job = Job(parentJob)
    launch(job) {
        delay(1000)
        println("Text 1")
    }
    launch(job) {
        delay(2000)
        println("Text 2")
    }
    delay(1100)
    parentJob.cancel()
    //job.children.forEach { it.join() }
}