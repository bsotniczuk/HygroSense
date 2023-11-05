package pl.bsotniczuk.hygrosense.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.bsotniczuk.hygrosense.ConnectionType
import pl.bsotniczuk.hygrosense.data.DbConstants
import java.util.*

@Entity(tableName = DbConstants.STATISTICS_TABLE_NAME)
data class StatisticsItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "sensor_id")
    val sensor_id: String,
    @ColumnInfo(name = "temperature")
    val temperature: String,
    @ColumnInfo(name = "humidity")
    val humidity: String,
    @ColumnInfo(name = "date")
    val date: Date

)
