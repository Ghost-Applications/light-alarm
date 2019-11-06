package cash.andrew.lightalarm.ui

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import cash.andrew.lightalarm.AlarmApp
import cash.andrew.lightalarm.ComponentContainer
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class ActivityScope

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)
    fun inject(alarmListItemView: AlarmListItemView)

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

@Suppress("UNCHECKED_CAST")
val Context.activityComponent
    get() = (this as ComponentContainer<ActivityComponent>).component

@Module
object ActivityModule {
}

