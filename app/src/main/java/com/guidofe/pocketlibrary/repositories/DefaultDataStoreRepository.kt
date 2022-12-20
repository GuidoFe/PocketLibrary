package com.guidofe.pocketlibrary.repositories

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.datastore.dataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.AppSettingsSerializer
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.data.local.library_db.converters.UriConverter
import com.guidofe.pocketlibrary.ui.theme.Theme
import java.io.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val Context.dataStore by dataStore(
    fileName = "app-settings.json",
    serializer = AppSettingsSerializer
)

class DefaultDataStoreRepository @Inject constructor(
    private val context: Context
) : DataStoreRepository {
    override val settingsLiveData: LiveData<AppSettings> = context.dataStore.data.asLiveData()
    override val COVER_DIR = "covers"
    override val BACKUP_FILE_ROOT = "pocket_library_backup"

    override fun getCoverDir(): File? {
        return settingsLiveData.value?.let { settings ->
            val isExternal = settings.saveInExternal
            if (isExternal)
                context.getExternalFilesDir(COVER_DIR)
            else
                context.getDir(COVER_DIR, Context.MODE_PRIVATE)
        }
    }

    override fun getCoverDir(isExternal: Boolean): File? {
        return getDir(COVER_DIR, isExternal)
    }

    override fun getInternalCoverFile(fileName: String): File? {
        return context.getDir(COVER_DIR, Context.MODE_PRIVATE)?.let { File(it, fileName) }
    }

    override fun getExternalCoverFile(fileName: String): File? {
        return context.getExternalFilesDir(COVER_DIR)?.let { File(it, fileName) }
    }

    override fun getCoverFile(fileName: String, external: Boolean): File? {
        return if (external)
            getExternalCoverFile(fileName)
        else
            getInternalCoverFile(fileName)
    }

    override fun getDir(dir: String, isExternal: Boolean): File? {
        return if (isExternal)
            context.getExternalFilesDir(dir)
        else
            context.getDir(dir, Context.MODE_PRIVATE)
    }

    override fun getFileInRootDir(name: String, isExternal: Boolean): File? {
        return if (isExternal)
            File(context.getExternalFilesDir(null), name)
        else
            File(context.filesDir, name)
    }

    override fun getCover(fileName: String): File? {
        return getCoverDir()?.let { File(it, fileName) }
    }

    override fun getCoverPath(fileName: String): String? {
        return getCover(fileName)?.path
    }

    override suspend fun setLanguage(language: Language) {
        context.dataStore.updateData { it.copy(language = language) }
    }

    private fun booleanToNightModeEnum(darkTheme: Boolean): Int {
        return if (darkTheme)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO
    }

    override suspend fun setDarkTheme(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(booleanToNightModeEnum(enabled))
        context.dataStore.updateData { it.copy(darkTheme = enabled) }
    }

    override suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.updateData {
            it.copy(
                dynamicColors = enabled
            )
        }
    }

    override suspend fun setTheme(theme: Theme) {
        context.dataStore.updateData {
            it.copy(
                theme = theme
            )
        }
    }

    override suspend fun setGenreTranslation(translate: Boolean) {
        context.dataStore.updateData {
            it.copy(
                allowGenreTranslation = translate
            )
        }
    }

    override suspend fun setMemory(isExternal: Boolean) {
        context.dataStore.updateData {
            it.copy(
                saveInExternal = isExternal
            )
        }
        getCoverDir(isExternal)?.toUri()?.let {
            UriConverter.baseUri = it
        }
    }

    override suspend fun setDefaultDaysBeforeDue(n: Int) {
        context.dataStore.updateData {
            it.copy(
                defaultShowNotificationNDaysBeforeDue = n
            )
        }
    }

    override suspend fun setDefaultNotificationEnabled(enabled: Boolean) {
        context.dataStore.updateData {
            it.copy(
                defaultEnableNotification = enabled
            )
        }
    }

    override suspend fun setDefaultNotificationTime(hours: Int, minutes: Int) {
        context.dataStore.updateData {
            it.copy(
                defaultNotificationTime = LocalTime.of(hours, minutes)
            )
        }
    }

    override fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    override suspend fun saveCover(bitmap: Bitmap, fullPath: String, callback: () -> Unit) {
        withContext(Dispatchers.IO) {
            val fOut = FileOutputStream(fullPath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut)
            fOut.flush()
            fOut.close()
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    override suspend fun saveMediaBackupLocally(external: Boolean): File? {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val formatted = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).format(formatter)
        val backupName = "${BACKUP_FILE_ROOT}_$formatted.zip"
        val coverFolder = getCoverDir(external) ?: return null
        val backupZip = getFileInRootDir(backupName, external) ?: return null
        zipFolder(coverFolder, backupZip)
        return backupZip
    }

    override suspend fun zipFolder(folder: File, outputZipFile: File) {
        withContext(Dispatchers.IO) {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(outputZipFile))).use { zos ->
                folder.walkTopDown().forEach { file ->
                    val zipFileName =
                        file.absolutePath.removePrefix(folder.absolutePath).removePrefix("/")
                    val entry = ZipEntry("$zipFileName${(if (file.isDirectory) "/" else "")}")
                    zos.putNextEntry(entry)
                    if (file.isFile) {
                        file.inputStream().copyTo(zos)
                    }
                }
            }
        }
    }

    /**
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun unzip(zipFilePath: File, destDirectory: String) {

        File(destDirectory).run {
            if (!exists()) {
                mkdirs()
            }
        }

        ZipFile(zipFilePath).use { zip ->

            zip.entries().asSequence().forEach { entry ->

                zip.getInputStream(entry).use { input ->

                    val filePath = destDirectory + File.separator + entry.name

                    if (!entry.isDirectory) {
                        // if the entry is a file, extracts it
                        extractFile(input, filePath)
                    } else {
                        // if the entry is a directory, make the directory
                        val dir = File(filePath)
                        dir.mkdir()
                    }
                }
            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     * @param inputStream
     * @param destFilePath
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }

    /**
     * Size of the buffer to read/write data
     */
    private val BUFFER_SIZE = 4096
}