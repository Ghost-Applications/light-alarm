package cash.andrew.lightalarm

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import androidx.core.content.getSystemService
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import io.paperdb.Book
import io.paperdb.Paper
import javax.inject.Singleton

val componentBuilder: AlarmAppComponent.Builder = DaggerAlarmAppComponent.builder()

@Singleton
@Component(modules = [AlarmAppModule::class])
interface AlarmAppComponent {

    val activityComponentBuilder: ActivityComponent.Builder
    val notificationManager: NotificationManager

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AlarmAppComponent
    }
}

@Module
object AlarmAppModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideNotificationManager(application: Application): NotificationManager = requireNotNull(application.getSystemService())

    @JvmStatic
    @Provides
    @Singleton
    fun provideAlarmManager(application: Application): AlarmManager = requireNotNull(application.getSystemService())

    @JvmStatic
    @Provides
    @Singleton
    fun provideBook(): Book = Paper.book()
}
