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
import kotlin.reflect.KClass

/**
 * A [ReceiveBus] which is also a [SendBus]
 */
interface Bus : ReceiveBus, SendBus

/**
 * A bus which can send events
 */
interface SendBus {
    /**
     * Sends the [event] to subscribers
     */
    fun send(event: Any)
}

/**
 * A bus which can be subscribed to
 */
interface ReceiveBus {
    /**
     * Invokes the [subscriber] on events of type [clazz]
     * Returns the same [subscriber] for convenience
     */
    fun <T : Any> subscribe(clazz: KClass<T>, subscriber: (T) -> Unit): (T) -> Unit
    /**
     * Unsubscribe's the previously added [subscriber]
     */
    fun <T : Any> unsubscribe(subscriber: (T) -> Unit)
}

/**
 * Returns a new [Bus] which uses the [callbackExecutor]
 */
fun Bus(
    callbackExecutor: Executor = BusPlugins.defaultCallbackExecutor
): Bus = RealBus(callbackExecutor)

/**
 * A global [Bus] instance
 */
val GlobalBus by lazy { Bus() }

/**
 * Invokes the [subscriber] on events of type [T]
 */
inline fun <reified T : Any> ReceiveBus.subscribe(noinline subscriber: (T) -> Unit) =
    subscribe(T::class, subscriber)