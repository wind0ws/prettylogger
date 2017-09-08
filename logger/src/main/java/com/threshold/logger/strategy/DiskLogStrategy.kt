package com.threshold.logger.strategy

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.RuntimeException

class DiskLogStrategy(private val handler: Handler) : LogStrategy {

    override fun log(priority: Int, tag: String?, message: String?) {
        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(priority, message))
    }

    class WriteHandler(looper: Looper, private val folder: String, private val maxFileSize: Int) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            val content = msg.obj as String
            var fileWriter: FileWriter? = null
            try {
                val logFile = getLogFile(folder, "logs")
                fileWriter = FileWriter(logFile, true)

                writeLog(fileWriter, content)

                fileWriter.flush()
                fileWriter.close()
            } catch (ex: IOException) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush()
                        fileWriter.close()
                    } catch (io: IOException) {
                        /* fail silently */
                    }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        @Throws(IOException::class)
        private fun writeLog(fileWriter: FileWriter, content: String) {
            fileWriter.append(content)
        }

        private fun getLogFile(folderName: String, fileName: String): File {
            val folder = File(folderName)
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw RuntimeException("Can't create the log folder: $folder \n Did you add <uses-permission android:name=\"android.permission.WRITE_EXTERNAL_STORAGE\"/> permission?")
                }
            }

            var newFileCount = 0
            var newFile: File
            var existingFile: File? = null

            newFile = File(folder, "${fileName}_$newFileCount.csv")
            while (newFile.exists()) {
                existingFile = newFile
                newFileCount++
                newFile = File(folder, "${fileName}_$newFileCount.csv")
            }

            return if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    newFile
                } else existingFile
            } else newFile
        }
    }
}