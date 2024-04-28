package at.tamber.yokolog

import AppContainer
import AppDataContainer
import android.app.Application


class Application : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}