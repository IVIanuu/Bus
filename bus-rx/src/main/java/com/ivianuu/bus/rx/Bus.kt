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

package com.ivianuu.bus.rx

import com.ivianuu.bus.Bus
import io.reactivex.Observable
import kotlin.reflect.KClass

/**
 * Returns a [Observable] which emits on events of [clazz]
 */
fun <T : Any> Bus.observable(clazz: KClass<T>): Observable<T> = Observable.create { e ->
    val subscriber: (T) -> Unit = {
        if (!e.isDisposed) {
            e.onNext(it)
        }
    }

    e.setCancellable { unsubscribe(subscriber) }

    if (!e.isDisposed) {
        subscribe(clazz, subscriber)
    }
}

/**
 * Returns a [Observable] which emits on events of [T]
 */
inline fun <reified T : Any> Bus.observable() =
        observable(T::class)