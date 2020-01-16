package server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures
import org.glassfish.jersey.jetty.JettyHttpContainerFactory
import org.glassfish.jersey.server.ResourceConfig
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.ext.ContextResolver
import org.eclipse.jetty.server.Server
import resource.FirstResource
import javax.ws.rs.ext.Provider

class JettyServer {
  fun startServer() {
    println("booting the server")

    val baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build()
    val serverConfig = ResourceConfig()
        .register(JacksonFeatures::class.java)
        .register(ObjectMapperProvider::class.java)
        .register(FirstResource())

    JettyHttpContainerFactory.createServer(baseUri, serverConfig).use { it.join() }
  }
}

@Provider
class ObjectMapperProvider : ContextResolver<ObjectMapper> {
  val objectMapper = ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

  override fun getContext(p0: Class<*>?): ObjectMapper? = objectMapper
}


inline fun Server.use(block: (Server) -> Unit) {
  try {
    block(this)
  } finally {
    this.destroy()
  }
}