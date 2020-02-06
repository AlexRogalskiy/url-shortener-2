package util

import java.io.File

class FileOperations {
  private val jsonfileName: String = "shortened-urls.json"

  fun readUrlsFromFile(): String =
      File(jsonfileName).readText(Charsets.UTF_8)

}