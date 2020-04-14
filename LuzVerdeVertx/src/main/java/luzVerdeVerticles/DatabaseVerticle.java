package luzVerdeVerticles;


import luzVerdeTipos.Cruce;
import luzVerdeTipos.LuzSemaforo;
import luzVerdeTipos.Semaforo;
import luzVerdeTipos.Sensor;
import luzVerdeTipos.ValorSensorContaminacion;
import luzVerdeTipos.ValorSensorTempHum;
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
				System.out.println("Todo OK");
				startPromise.complete();
			}else {
				startPromise.fail(result.cause());
			}
		});
		
		//Peticiones
		//router.route("links").blockingHandler(BodyHandler.create());
		router.get("/api/valor_sensor_contaminacion/values/:idSensor").handler(this::getValueBySensor);
		router.get("/api/valor_sensor_temp_hum/values/:idSensor").handler(this::getValueBySensor2);
		router.get("/api/sensor/sensors/:idSemaforo").handler(this::getSensorBySemaforo);
		router.get("/api/semaforo/semaforos/:idCruce").handler(this::getSemaforoByCruce);
		router.get("/api/luz/luces/:idSemaforo").handler(this::getLuzBySemaforo);
		router.get("/api/cruce/cruces/:idUsuario").handler(this::getCruceByUsuario);
		
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
	private void getValueBySensor2 (RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.valor_sensor_temp_hum WHERE idSensor = "+ routingContext.request().getParam("idSensor"), 
				res -> {
					if(res.succeeded()) {
						RowSet<Row>  resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new ValorSensorTempHum(row.getInteger("idValor_sensor_temp_hum"), row.getFloat("valueTemp"), row.getFloat("accuracyTemp"), 
									row.getFloat("valueHum"), row.getFloat("accuracyHum"), row.getLong("timestamp"), row.getInteger("idSensor"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						System.out.println("error");
					}
				});
	}
	private void getSensorBySemaforo (RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.sensor WHERE idSemaforo = "+ routingContext.request().getParam("idSemaforo"), 
				res -> {
					if(res.succeeded()) {
						RowSet<Row>  resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Sensor(row.getInteger("idSensor"), row.getString("tipoSensor"), row.getString("nombreSensor"), 
									row.getInteger("idSemaforo"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						System.out.println("error");
					}
				});
	}
	private void getSemaforoByCruce (RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.semaforo WHERE idCruce = "+ routingContext.request().getParam("idCruce"), 
				res -> {
					if(res.succeeded()) {
						RowSet<Row>  resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Semaforo(row.getInteger("idSemaforo"), row.getInteger("idCruce"), row.getString("nombreSemaforo"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						System.out.println("error");
					}
				});
	}
	private void getLuzBySemaforo (RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.luz_semaforo WHERE idSemaforo = "+ routingContext.request().getParam("idSemaforo"), 
				res -> {
					if(res.succeeded()) {
						RowSet<Row>  resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new LuzSemaforo(row.getInteger("idLuz_Semaforo"), row.getString("color"), row.getLong("timestamp"), row.getInteger("idSemaforo"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						System.out.println("error");
					}
				});
	}
	private void getCruceByUsuario (RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.cruce WHERE idUsuario = "+ routingContext.request().getParam("idUsuario"), 
				res -> {
					if(res.succeeded()) {
						RowSet<Row>  resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Cruce(row.getInteger("idCruce"), row.getString("ipCruce"), row.getString("nombreCruce"),
									row.getLong("initialTimestamp"), row.getInteger("idUsuario"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						System.out.println("error");
					}
				});
	}

}
