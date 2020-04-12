package cash.andrew.lightalarm

import android.app.Activity
import cash.andrew.lightalarm.ui.ActivityScope
import dagger.Module
import dagger.Provides

@Module
object RiseAndShineModule {
    @Provides
    @ActivityScope
    fun provideRiseAndShine(activity: Activity): RiseAndShine = DefaultRiseAndShine(activity)
}