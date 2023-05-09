package com.coffilation.app.util.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

/**
 * @author pvl-zolotov on 09.05.2023
 */
class StateDispatcher private constructor() : ExecutorCoroutineDispatcher() {

    private val workerName = DISPATCHER_PREFIX + dispatcherCount.incrementAndGet()

    private val executorService = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, workerName).apply { isDaemon = true }
    }

    override val executor: Executor
        get() = executorService

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        try {
            executorService.execute(block)
        } catch (e: RejectedExecutionException) {
            Dispatchers.IO.dispatch(context, block)
        }
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return Thread.currentThread().name != workerName
    }

    override fun close() {
        executorService.shutdown()
    }

    override fun toString(): String {
        return executor.toString()
    }

    override fun equals(other: Any?): Boolean {
        return other is StateDispatcher && other.executor === executor
    }

    override fun hashCode(): Int {
        return System.identityHashCode(executor)
    }

    companion object {

        private val dispatcherCount = AtomicInteger(0)
        private const val DISPATCHER_PREFIX = "StateDispatcher-worker-"

        val default: StateDispatcher = StateDispatcher()
    }
}
