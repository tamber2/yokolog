package at.tamber.yokolog

import android.content.Context
import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.Calendar
import java.util.Date


/**
 * https://stackoverflow.com/questions/7953725/how-to-convert-milliseconds-to-date-format-in-android
 * Return date in specified format.
 * @param milliSeconds Date in milliseconds
 * @return String representing date in specified format
 */
fun getDate(milliSeconds: Long): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar: Calendar = Calendar.getInstance()
    calendar.setTimeInMillis(milliSeconds)
    return formatter.format(calendar.time)
}

fun writeToDisk(text: String, context: Context): File {
    val appSpecificExternalStorageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val file = File(appSpecificExternalStorageDirectory, "export_yokolog_" + Date().toString() + ".csv")
    file.createNewFile()
    file.writeText(text)
    val fos = FileOutputStream(file, false)
    fos.close()
    return file
}

fun getIconByName(name: String): ImageVector {
    try {
        val cl = Class.forName("androidx.compose.material.icons.filled.${name}Kt")
        val method = cl.declaredMethods.first()
        return method.invoke(null, Icons.Filled) as ImageVector
    } catch (_: Throwable) {
        return Icons.AutoMirrored.Filled.ArrowForward
    }
}


fun getColorFromString(colorString: String): Color {
    return Color(colorString.removePrefix("#").toLong(16) or 0x00000000FF000000)
}

val String.color
    get() = Color(android.graphics.Color.parseColor(this))


fun Color.toHexCode(): String {
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255
    return String.format("#%02x%02x%02x", red.toInt(), green.toInt(), blue.toInt())
}