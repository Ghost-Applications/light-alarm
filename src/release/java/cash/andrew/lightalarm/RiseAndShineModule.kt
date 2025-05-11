package cash.andrew.lightalarm

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@InstallIn(ActivityComponent::class)
@Module
object RiseAndShineModule {
    @ActivityRetainedScoped
    @Provides
    fun provideRiseAndShine(): RiseAndShine = object: RiseAndShine {
        override fun riseAndShine() { }
    }
}