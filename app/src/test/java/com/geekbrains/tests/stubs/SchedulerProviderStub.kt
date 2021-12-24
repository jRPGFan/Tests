package com.geekbrains.tests.stubs

import com.geekbrains.tests.presenter.rx.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class SchedulerProviderStub : SchedulerProvider {
    override fun ui(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }
}