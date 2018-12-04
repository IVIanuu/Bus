package com.ivianuu.kbus.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ivianuu.bus.BusPlugins
import com.ivianuu.bus.GlobalBus
import com.ivianuu.bus.android.MAIN_THREAD_EXECUTOR
import com.ivianuu.bus.rx.observable
import com.ivianuu.bus.subscribe

data class MyEvent(val value: Int)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BusPlugins.defaultCallbackExecutor = MAIN_THREAD_EXECUTOR

        val sub = GlobalBus.subscribe<MyEvent> {
            Log.d("testt", "on event $it")
        }

        GlobalBus.observable<MyEvent>()
            .subscribe { Log.d("testt", "rx -> $it") }

        GlobalBus.send(MyEvent(1))

        GlobalBus.unsubscribe(sub)

        GlobalBus.send(MyEvent(2))

        GlobalBus.subscribe(sub)

        GlobalBus.send(MyEvent(3))

        GlobalBus.unsubscribe(sub)

        GlobalBus.send(MyEvent(4))


    }
}
