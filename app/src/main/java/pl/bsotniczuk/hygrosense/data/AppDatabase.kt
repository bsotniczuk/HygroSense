package pl.bsotniczuk.hygrosense.data

import android.content.Context
import androidx.room.*
import pl.bsotniczuk.hygrosense.data.dao.SettingsDao
import pl.bsotniczuk.hygrosense.data.dao.StatisticsDao
import pl.bsotniczuk.hygrosense.data.model.SettingsItem
import pl.bsotniczuk.hygrosense.data.model.StatisticsItem

@Database(
    entities = [SettingsItem::class, StatisticsItem::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun settingsDao() : SettingsDao
    abstract fun statisticsDao() : StatisticsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it, if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}