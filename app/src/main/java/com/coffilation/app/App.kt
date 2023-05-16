package com.coffilation.app

import android.app.Application
import com.coffilation.app.util.authModule
import com.coffilation.app.util.collectionsModule
import com.coffilation.app.util.mapModule
import com.coffilation.app.util.prefModule
import com.coffilation.app.util.searchModule
import com.coffilation.app.util.signInModule
import com.coffilation.app.util.usersModule
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/**
 * @author pvl-zolotov on 15.10.2022
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("debac8f5-12c6-425e-b34c-d22bd460f19f")
        startKoin {
            androidContext(this@App)
            val list = arrayListOf(
                usersModule,
                signInModule,
                authModule,
                prefModule,
                collectionsModule,
                searchModule,
                mapModule,
            )
            modules(list)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}
