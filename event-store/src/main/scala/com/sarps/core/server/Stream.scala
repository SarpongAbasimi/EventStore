import org.http4s.blaze.server.BlazeServerBuilder
import cats.effect.kernel.Async
import com.sarps.core.config.Config

object Stream {
  def stream[F[_]: Async](config: Config): fs2.Stream[F, Unit] = for {
    server <- BlazeServerBuilder[F]
      .bindHttp(config.serverConfig.port.port, config.serverConfig.host.host)
      .serve
  } yield ()
}
