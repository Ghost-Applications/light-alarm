package cash.andrew.lightalarm.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds abstract fun bindAlarmKeeper(defaultAlarmKeeper: DefaultAlarmKeeper): AlarmKeeper
}
