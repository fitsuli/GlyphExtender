package com.fitsuli.glyphextender.utils.prefs

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by Dmitry Danilyuk at 12.08.2024
 */
object NotificationPreferencesSerializer : Serializer<NotificationPreferences> {
    override val defaultValue: NotificationPreferences
        get() = NotificationPreferences()

    override suspend fun readFrom(input: InputStream): NotificationPreferences = try {
        Json.decodeFromString(
            deserializer = NotificationPreferences.serializer(),
            string = input.readBytes().decodeToString()
        )
    } catch (e: Exception) {
        e.printStackTrace()
        defaultValue
    }

    override suspend fun writeTo(t: NotificationPreferences, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    serializer = NotificationPreferences.serializer(),
                    value = t
                ).toByteArray()
            )
        }
    }
}
