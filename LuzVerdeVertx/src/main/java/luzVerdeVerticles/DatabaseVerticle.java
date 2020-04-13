package luzVerdeVerticles;

import luzVerdeTipos.ValorSensorContaminacion;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

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
		router.get("/api/valor_sensor_contaminacion/values/:idSensor").handler(this::getValueBySensor);
	}
	
	private void getValueBySensor (RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.valor_sensor_contaminacion WHERE idSensor = "+ routingContext.request().getParam("idSensor"), 
				res -> {
					if(res.succeeded()) {
						RowSet<Row>  resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new ValorSensorContaminacion(row.getInteger("idValor_sensor_contaminacion"), row.getInteger("idSensor"),
									row.getFloat("value"), row.getFloat("accuracy"), row.getLong("timestamp"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						System.out.println("error");
					}
				});
	}
}
