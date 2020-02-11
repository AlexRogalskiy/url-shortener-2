package service

import ShortenedUrlDTO
import ShortenedUrlMapper
import ShortenedUrlSingleton
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import domain.ShortenedUrl
import domain.ShortenedUrls
import org.apache.log4j.Logger
import toShortenedUrl
import util.AlphanumericHashGenerator
import util.FileOperations
import java.io.IOException
import java.nio.file.Paths

class UrlShortenerService {
  private val logger: Logger = Logger.getLogger(UrlShortenerService::class.java)
  private val jsonfileName: String = "shortened-urls.json"
  private var identifier: Int = 0

  // for now this is how i uniquely identify a shortened url
  fun makeNewIdentifier(): String {
    return AlphanumericHashGenerator().generateHash()
  }

  fun prepareShortenedUrl(shortenedUrl: ShortenedUrlDTO): ShortenedUrl =
      ShortenedUrlMapper(makeNewIdentifier(), shortenedUrl.url, shortenedUrl.shortenedUrl)
          .toShortenedUrl()

  private fun writeUrlsToStorageFile(shortenedUrls: ShortenedUrls): Unit =
      ObjectMapper()
          .writeValue(
              Paths.get(jsonfileName)
                  .toFile(),
              shortenedUrls.shortenedUrls
          )

  fun addnewShortenedUrl(shortenedUrlDTO: ShortenedUrlDTO) {
    FileOperations().readUrlsFromFile()
    val shortenedUrl = prepareShortenedUrl(shortenedUrlDTO)

    logger.info("======================")
    logger.info(FileOperations().readUrlsFromFile())
    logger.info(deserialiseJsonFile())
    val shortenedUrls: ShortenedUrls = ShortenedUrlSingleton.addToShortenedUrls(shortenedUrl)

    try {
      writeUrlsToStorageFile(shortenedUrls)
      logger.info("written to file")
    } catch (ex: IOException) {
      logger.error("could not write to file", ex)
    }
  }

  // the goal is to deserialise the json into the ShortenedUrl::class not a List.
  //  TODO: move this out of the this service because it has nothing to do with the actual shortening of url.
  fun deserialiseJsonFile(): List<ShortenedUrl> {
    val mapper = ObjectMapper()
    val jsonString: String = FileOperations().readUrlsFromFile()

    return mapper.readValue(jsonString, object : TypeReference<List<ShortenedUrl>>() {})
  }
}

