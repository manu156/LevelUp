package com.manu156.levelup.data.git

import com.manu156.levelup.data.model.SessionCategory
import com.manu156.levelup.data.model.WorkSession
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object GitSessionSerializer {

    fun serializeSession(session: WorkSession): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("id", session.id)

        val info = JSONObject()
        info.put("title", session.title)
        info.put("category", session.category.name)
        info.put("tag", session.tag)
        info.put("notes", session.notes)
        root.put("info", info)

        val timing = JSONObject()
        timing.put("startEpochMs", session.startTimeMillis)
        timing.put("endEpochMs", session.endTimeMillis)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        timing.put("startIso", sdf.format(Date(session.startTimeMillis)))
        timing.put("endIso", sdf.format(Date(session.endTimeMillis)))
        root.put("timing", timing)

        return root.toString(2)
    }

    fun deserializeSession(jsonString: String): WorkSession {
        val root = JSONObject(jsonString)
        val id = root.optString("id").ifBlank { UUID.randomUUID().toString() }

        val info = root.optJSONObject("info") ?: JSONObject()
        val title = info.optString("title", "Focus Session")
        val categoryName = info.optString("category", "DEEP_WORK")
        val category = try {
            SessionCategory.valueOf(categoryName)
        } catch (e: Exception) {
            SessionCategory.DEEP_WORK
        }
        val tag = info.optString("tag", "Work")
        val notes = info.optString("notes", "")

        val timing = root.optJSONObject("timing") ?: JSONObject()
        val startEpoch = timing.optLong("startEpochMs", System.currentTimeMillis())
        val endEpoch = timing.optLong("endEpochMs", startEpoch + 3 * 3600 * 1000)

        val durationMillis = (endEpoch - startEpoch).coerceAtLeast(0)
        val durationMinutes = durationMillis / 60_000
        val durationHours = durationMillis / 3_600_000f

        return WorkSession(
            id = id,
            title = title,
            category = category,
            startTimeMillis = startEpoch,
            endTimeMillis = endEpoch,
            tag = tag,
            notes = notes
        )
    }

    fun getRelativePathForSession(session: WorkSession): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US)
        val datePrefix = sdf.format(Date(session.startTimeMillis))
        val safeId = sanitizeForFilename(session.id)
        val zoneId = java.time.ZoneId.systemDefault()
        val zdt = java.time.Instant.ofEpochMilli(session.startTimeMillis).atZone(zoneId)
        val year = zdt.year.toString()
        val month = String.format("%02d", zdt.monthValue)
        return "sessions/$year/$month/${datePrefix}_$safeId.json"
    }

    fun sanitizeForFilename(input: String): String {
        return input.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
}
