package endpoints

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import domain.ShortenedUrlDTO
import domain.ShortenedUrl
import domain.ShortenedUrls
import domain.UnshortenedUrlResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import service.UrlShortenerService
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/api")
class ShortenedUrlResource {
    private val logger: Logger = LoggerFactory.getLogger(ShortenedUrlResource::class.java)
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val shortenUrlService = UrlShortenerService()

    @Path("/shorten-url")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    fun addShortenedUrl(shortenedUrlDTO: ShortenedUrlDTO): Response {
        return try {
            logger.info("Shortening url: ${shortenedUrlDTO.url}")
            shortenUrlService.addnewShortenedUrl(shortenedUrlDTO)
            Response.status(
                    Response.Status.ACCEPTED
            ).build()
        } catch (ex: Exception) {
            Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR
            ).build()
        }
    }

    @Path("/unshorten-url/{id}")
    @GET
    fun unshortenUrl(@PathParam("id") id: String): Response {
        logger.info("Unshortening url with id: $id")
        val unshortenedUrlResponse = objectMapper.writeValueAsString(UnshortenedUrlResponse(shortenUrlService.getOriginalUrlById(id)))
        return Response.accepted(unshortenedUrlResponse).build()
    }

    @Path("/url/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getUrlById(@PathParam("id") id: String): ShortenedUrl {
        logger.info("Getting url with id: $id")
        return shortenUrlService.getByIdOrShortened(id)
    }

    @Path("/urls")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllUrls(): Response {
        logger.info("Looking up all urls")
        val allUrls = objectMapper.writeValueAsString(shortenUrlService.getAllUrls())
        return Response.accepted(allUrls).build()
    }

    @Path("/url/{id}")
    @DELETE
    fun deleteUrl(@PathParam("id") id: String): Response {
        return try {
            logger.info("Deleting url with id: $id")
            shortenUrlService.deleteUrlById(id)
            Response.status(
                    Response.Status.ACCEPTED
            ).build()
        } catch (ex: Exception) {
            Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR
            ).build()
        }
    }

    @Path("/url/{id}")
    @PUT
    @Consumes("application/json")
    fun changeUrlById(@PathParam("id") id: String, shortenedUrlDTO: ShortenedUrlDTO): Response {
        return try {
            shortenUrlService.changeById(id, shortenedUrlDTO)
            Response.status(
                    Response.Status.ACCEPTED
            ).build()
        } catch (ex: Exception) {
            Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR
            ).build()
        }
    }
}