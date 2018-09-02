package com.kirakishou.mvrxtest.app

import android.app.Application
import com.kirakishou.mvrxtest.data.ApiService
import com.kirakishou.mvrxtest.data.FakeApiService
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

class TestApp : Application() {

  private val networkModule: Module = applicationContext {
    bean { FakeApiService() as ApiService }
  }

  override fun onCreate() {
    super.onCreate()

    startKoin(this, listOf(networkModule))
  }

}