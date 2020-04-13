package luzVerdeVerticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

//Verticle para gestionar la BBDD a través de Vert.x
public class DatabaseVerticle extends AbstractVerticle{

	private MySQLPool mySQLPool;
	
	@Override
	public void start(Promise<Void> startPromise) {
		//Conexión BBDD
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("luzverde").setUser("root").setPassword("zeus");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(15);
		
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);
		
		//HTTP Server
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(8082, result -> {
			if(result.succeeded()) {
				startPromise.complete();
			}else {
				startPromise.fail(result.cause());
			}
		});
		
		//Peticiones
		router.route("links").blockingHandler(BodyHandler.create());
		
	}
}
