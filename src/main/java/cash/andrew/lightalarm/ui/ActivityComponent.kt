package cash.andrew.lightalarm.ui

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import cash.andrew.lightalarm.AlarmApp
import cash.andrew.lightalarm.ComponentContainer
import cash.andrew.lightalarm.RiseAndShine
import cash.andrew.lightalarm.RiseAndShineModule
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class ActivityScope

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: AlarmActivity)
    fun inject(alarmListItemView: AlarmListItemView)

    val riseAndShine: RiseAndShine

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: Activity): Builder
        fun build(): ActivityComponent
    }
}

fun <T> T.makeComponent():
        ActivityComponent where T : ComponentContainer<ActivityComponent>,
                                T : Activity = (application as AlarmApp)
    .component
    .activityComponentBuilder
    .activity(this)
    .build()
    .also {
        it.riseAndShine.riseAndShine()
    }

@Suppress("UNCHECKED_CAST")
val Context.activityComponent
    get() = (this as ComponentContainer<ActivityComponent>).component

@Module(includes = [RiseAndShineModule::class])
object ActivityModule
