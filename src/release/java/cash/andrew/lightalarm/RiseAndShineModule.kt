package cash.andrew.lightalarm

import cash.andrew.lightalarm.ui.ActivityScope
import dagger.Module
import dagger.Provides

@Module
object RiseAndShineModule {
    @Provides
    @ActivityScope
    fun provideRiseAndShine(): RiseAndShine = object: RiseAndShine {
        override fun riseAndShine() { }
    }
}