package com.twapps.serverstatuschecker

import com.twapps.serverstatuschecker.MainPage.mainPage
import io.kvision.Application
import io.kvision.BootstrapCssModule
import io.kvision.BootstrapModule
import io.kvision.ChartModule
import io.kvision.CoreModule
import io.kvision.FontAwesomeModule
import io.kvision.ToastifyModule
import io.kvision.module
import io.kvision.panel.root
import io.kvision.startApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val AppScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

class App : Application() {
    override fun start(state: Map<String, Any>) {
        val root = root("kvapp") {
            mainPage()
        }
    }
}

fun main() {
    startApplication(
        ::App,
        module.hot,
        BootstrapModule,
        BootstrapCssModule,
        FontAwesomeModule,
        ToastifyModule,
        ChartModule,
        CoreModule
    )
}
