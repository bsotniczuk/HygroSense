package pl.bsotniczuk.hygrosense.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.bsotniczuk.hygrosense.data.DbConstants

@Entity(tableName = DbConstants.SETTINGS_TABLE_NAME)
data class SettingsItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "esp32_ip_address")
    val esp32_ip_address: String,
    @ColumnInfo(name = "wifi_ssid")
    val wifi_ssid: String,
    @ColumnInfo(name = "wifi_password")
    val wifi_password: String,
    @ColumnInfo(name = "device_setting")
    val device_setting: String

)
