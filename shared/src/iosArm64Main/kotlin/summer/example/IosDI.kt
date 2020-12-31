package summer.example

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.engine.ios.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import platform.Foundation.NSUserDefaults
import summer.example.presentation.base.mainDispatcher

fun bind(
    userDefaults: NSUserDefaults
) {
    mainDispatcher = Dispatchers.Unconfined

    mainDI = DI {
        import(sharedModule)

        bind<Settings>() with singleton { AppleSettings(userDefaults) }
        bind<HttpClient>() with singleton { HttpClient(Ios) }
        bind<Json>() with singleton { Json { isLenient = true } }
    }
}