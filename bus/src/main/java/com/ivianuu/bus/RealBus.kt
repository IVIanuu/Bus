/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.bus

import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

internal class RealBus(private val executor: Executor = BusPlugins.defaultCallbackExecutor) : Bus {

    private val subscribers = mutableMapOf<Class<*>, MutableSet<(Any) -> Unit>>()

    private val lock = ReentrantLock()

    override fun send(event: Any): Unit = lock.withLock {
        val subs = subscribers
            .filter { event.javaClass.isAssignableFrom(it.key) }
            .flatMap { it.value }

        executor.execute { subs.forEach { it(event) } }
    }

    override fun <T : Any> subscribe(clazz: KClass<T>, subscriber: (T) -> Unit) = lock.withLock {
        subscribers.getOrPut(clazz.java) { mutableSetOf() }
            .add(subscriber as (Any) -> Unit)
        subscriber
    }

    override fun <T : Any> unsubscribe(subscriber: (T) -> Unit): Unit = lock.withLock {
        subscribers.entries
            .filter { it.value.contains(subscriber) }
            .onEach { it.value.remove(subscriber) }
            .filter { it.value.isEmpty() }
            .map { it.key }
            .forEach { subscribers.remove(it) }
    }

}