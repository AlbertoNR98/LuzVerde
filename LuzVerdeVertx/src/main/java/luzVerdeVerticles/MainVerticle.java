package luzVerdeVerticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;


public class MainVerticle extends AbstractVerticle{
	@SuppressWarnings("deprecation")
	@Override
	public void start(Future<Void> startFuture) {
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest request) {
				request.response().end("<h1>LUZ VERDE</h1>Semáforos");
			}
		}).listen(8080, result -> {	//Puerto
			if(result.succeeded()) {	//Saca el mensaje por consola si se ha iniciado correctamente
				System.out.println("Todo correcto");
			}else {
				System.out.println(result.cause());
			}
		});
	}
}
