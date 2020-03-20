package cash.andrew.lightalarm.data

import dagger.Binds
import dagger.Module

@Module
abstract class DataModule {
    @Binds abstract fun bindAlarmKeeper(defaultAlarmKeeper: DefaultAlarmKeeper): AlarmKeeper
}
