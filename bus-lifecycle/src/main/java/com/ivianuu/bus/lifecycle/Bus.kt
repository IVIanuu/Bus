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

package com.ivianuu.bus.lifecycle

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ivianuu.bus.Bus
import kotlin.reflect.KClass

/**
 * Returns a [LiveData] which emits on events of [clazz]
 */
fun <T : Any> Bus.liveData(clazz: KClass<T>): LiveData<T> {
    lateinit var subscriber: (T) -> Unit

    val liveData = object : MutableLiveData<T>() {
        override fun onActive() {
            super.onActive()
            subscribe(clazz, subscriber)
        }

        override fun onInactive() {
            super.onInactive()
            unsubscribe(subscriber)
        }
    }

    subscriber = {
        when {
            Looper.myLooper() == Looper.getMainLooper() -> liveData.value = it
            else -> liveData.postValue(it)
        }
    }

    return liveData
}

/**
 * Returns a [LiveData] which emits on events of [T]
 */
inline fun <reified T : Any> Bus.liveData() = liveData(T::class)