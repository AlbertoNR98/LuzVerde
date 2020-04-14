package luzVerdeVerticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

//Verticle principal.
public class MainVerticle extends AbstractVerticle{
	@Override
	public void start(Future<Void> startFuture) {
		//Despliegue de API REST
		vertx.createHttpServer().requestHandler(
				request ->{
					request.response().end("<h1>ZeUS</h1> ZeUS");;
				 
		}).listen(8081, result ->{
			if(result.succeeded()) {
				System.out.println("Todo OK");
			}else {
				System.out.println(result.cause());
			}
		});
		
		vertx.deployVerticle(DatabaseVerticle.class.getName());
	}
}
