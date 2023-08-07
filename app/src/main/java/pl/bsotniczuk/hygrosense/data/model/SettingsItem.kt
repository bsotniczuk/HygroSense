package pl.bsotniczuk.hygrosense.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.bsotniczuk.hygrosense.ConnectionType
import pl.bsotniczuk.hygrosense.data.DbConstants

@Entity(tableName = DbConstants.SETTINGS_TABLE_NAME)
data class SettingsItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "esp32_ip_address_access_point")
    val esp32_ip_address_access_point: String,
    @ColumnInfo(name = "wifi_ssid")
    val wifi_ssid: String,
    @ColumnInfo(name = "wifi_password")
    val wifi_password: String,
    @ColumnInfo(name = "aws_iot_core_endpoint")
    val aws_iot_core_endpoint: String,
    @ColumnInfo(name = "wifi_access_point_ssid")
    val wifi_access_point_ssid: String,
    @ColumnInfo(name = "wifi_access_point_password")
    val wifi_access_point_password: String,
    @ColumnInfo(name = "connection_type")
    val connection_type: ConnectionType,
    @ColumnInfo(name = "device_setting")
    val device_setting: String

)
