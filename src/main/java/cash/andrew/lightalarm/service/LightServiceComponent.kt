package cash.andrew.lightalarm.service

import cash.andrew.lightalarm.data.LightController
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class LightServiceScope

@LightServiceScope
@Subcomponent
interface LightServiceComponent {
    fun inject(lightService: LightService)

    val lightController: LightController
}
