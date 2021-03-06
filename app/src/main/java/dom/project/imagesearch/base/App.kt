package dom.project.imagesearch.base

import android.app.Application
import dom.project.imagesearch.di.networkModule
import dom.project.imagesearch.di.roomModule
import dom.project.imagesearch.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                networkModule,
                viewModelModule,
                roomModule
            )
        }
    }
}