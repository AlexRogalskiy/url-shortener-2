package endpoints

import ShortenedUrlDTO
import domain.ShortenedUrl
import org.apache.log4j.Logger
import service.UrlShortenerService
import util.FileOperations
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

private val logger: Logger = Logger.getLogger(ShortenedUrlResource::class.java)

@Path("/api")
class ShortenedUrlResource {
  private val shortenUrlService = UrlShortenerService()
  private val fileOperations = FileOperations()

  @Path("/shorten-url")
  @POST
  @Consumes("application/json")
  @Produces(MediaType.APPLICATION_JSON)
  fun addShortenedUrl(shortenedUrlDTO: ShortenedUrlDTO): Response = try {
    logger.info(shortenedUrlDTO)
    shortenUrlService.addnewShortenedUrl(shortenedUrlDTO)
    Response.ok().build()
  } catch (ex: Exception) {
    logger.error("internal server error", ex)
    Response.status(500).build()
  }

  @Path("/url/{id}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  fun getUrlById(@PathParam("id") id: String): ShortenedUrl =
      shortenUrlService.getById(id)

  @Path("/urls")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  fun getUrls(): List<ShortenedUrl> =
      fileOperations.readFileContent()

  @Path("/url/{id}")
  @DELETE
  fun deleteUrl(@PathParam("id") id: String): Response = try {
    fileOperations.deleteById(id)
    logger.info(fileOperations.readFileContent())
    Response.status(200).build()
  } catch (ex: Exception) {
    logger.error("something wrong while deleting url", ex)
    Response.status(500).build()
  }

}