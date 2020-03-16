package summer.example.presentation

import kotlinx.coroutines.launch
import org.kodein.di.erased.instance
import summer.example.domain.about.GetAbout
import summer.example.entity.About
import summer.example.presentation.base.LoadingView
import summer.example.presentation.base.ScreenPresenter
import summer.example.presentation.base.withLoading

interface AboutView : LoadingView {
    var about: About?
    val doSomething: (smth: String) -> Unit
}

interface AboutRouter

class AboutPresenter : ScreenPresenter<AboutView>() {

    private val getAbout: GetAbout by instance()

    override val viewProxy = object : AboutView {
        override var about by store({ it::about }, initial = null)
        override var isLoading by store({ it::isLoading }, initial = true)
        override val doSomething = event { it.doSomething }.doOnlyWhenAttached()
    }

    override fun onAppear() {
        super.onAppear()
        launch {
            withLoading {
                val about = getAbout()
                viewProxy.about = about
            }
        }
    }
}