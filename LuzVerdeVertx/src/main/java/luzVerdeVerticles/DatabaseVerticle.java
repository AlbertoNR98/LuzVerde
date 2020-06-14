package luzVerdeVerticles;


import luzVerdeTipos.Cruce;
import luzVerdeTipos.LuzSemaforo;
import luzVerdeTipos.Semaforo;
import luzVerdeTipos.Sensor;
import luzVerdeTipos.Usuario;
import luzVerdeTipos.ValorSensorContaminacion;
import luzVerdeTipos.ValorSensorTempHum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;


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
		
		
		Router router = Router.router(vertx);
	
		
		//HTTP Server
		vertx.createHttpServer().requestHandler(router::handle).listen(8082, result -> {
			if(result.succeeded()) {
				System.out.println("Todo OK. Sistema desplegado");
				startPromise.complete();
			}else {
				System.out.println("Error al desplegar el sistema");
				startPromise.fail(result.cause());
			}
		});
		
		//Peticiones
			//Usuario
		router.route("/api/usuarios").handler(BodyHandler.create());
		router.route("/api/usuarios*").handler(BodyHandler.create());
		
		router.get("/api/usuarios/:idUsuario").handler(this::getUserByID);
		router.get("/api/usuario/:dni").handler(this::getIdByDni);
		router.get("/api/usuarios").handler(this::getAllUsers);
		router.post("/api/usuarios").handler(this::postUser);
		router.put("/api/usuarios/:idUsuario").handler(this::updateUserByID);
		router.delete("/api/usuarios/:idUsuario").handler(this::deleteUserByID);
		
			//Cruce
		router.route("/api/cruces").handler(BodyHandler.create());
		router.route("/api/cruces*").handler(BodyHandler.create());
		
		router.get("/api/cruces/:idUsuario").handler(this::getCruceByUsuario);
		router.get("/api/cruces").handler(this::getAllCruces);
		router.post("/api/cruces").handler(this::postCruce);
		router.put("/api/cruces/:idCruce").handler(this::updateCruceByID);
		router.delete("/api/cruces/:idCruce").handler(this::deleteCruceByID);
		
			//Semáforo
		router.route("/api/semaforos").handler(BodyHandler.create());
		router.route("/api/semaforos*").handler(BodyHandler.create());
		
		router.get("/api/semaforos/:idCruce").handler(this::getSemaforoByCruce);
		router.get("/api/semaforos").handler(this::getAllSemaforos);
		router.post("/api/semaforos").handler(this::postSemaforo);
		router.put("/api/semaforos/:idSemaforo").handler(this::updateNombreSemaforoByID);
		
			//Luz Semáforo
		router.route("/api/luces").handler(BodyHandler.create());
		router.route("/api/luces*").handler(BodyHandler.create());
		
		router.get("/api/luces/:idSemaforo/:timestamp").handler(this::getLuzBySemaforoAndTimestamp);
		router.get("/api/luces").handler(this::getAllLuces);
		router.put("/api/luces").handler(this::putLuz);
		
			//Sensores
		router.route("/api/sensores").handler(BodyHandler.create());
		router.route("/api/sensores*").handler(BodyHandler.create());
		
		router.get("/api/sensores/:idSemaforo").handler(this::getSensorBySemaforo);
		router.post("/api/sensores").handler(this::postSensor);
		router.put("/api/sensores/:idSensor").handler(this::updateSensorByID);
		router.delete("/api/sensores/:idSensor").handler(this::deleteSensorByID);
		
			//Sensor Contaminación
		router.route("/api/valores_sensor_contaminacion").handler(BodyHandler.create());
		router.route("/api/valores_sensor_contaminacion*").handler(BodyHandler.create());
		
		router.get("/api/valores_sensor_contaminacion/:idSensor").handler(this::getValueBySensorCont);
		router.get("/api/valores_sensor_contaminacion/:nombreSemaforo/:C02").handler(this::getValueBySemaforo);
		router.put("/api/valores_sensor_contaminacion").handler(this::putValorContaminacion);
		
			//Sensor Temperatura y humedad
		router.route("/api/valores_sensor_temp_hum").handler(BodyHandler.create());
		router.route("/api/valores_sensor_temp_hum*").handler(BodyHandler.create());

		router.get("/api/valores_sensor_temp_hum/:idSensor").handler(this::getValueBySensorTempHum);
		router.put("/api/valores_sensor_temp_hum").handler(this::putValorTempHum);
						
	}
	
	//Operaciones
		//Usuario
	private void getAllUsers(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.usuario", res ->{
			if(res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size()+" elementos obtenidos");
				JsonArray result = new JsonArray();
				for(Row row : resultSet) {
					result.add(JsonObject.mapFrom(new Usuario(row.getInteger("idUsuario"), row.getString("nombre"), row.getString("apellidos"), row.getString("dni"), row.getInteger("fnacimiento"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						

			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				System.out.println("Error al hacer la operación");
			}
		});
	}
	
	private void getUserByID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.usuario WHERE idUsuario = "+ routingContext.request().getParam("idUsuario"), res ->{
			if(res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size()+" elementos obtenidos");
				JsonArray result = new JsonArray();
				for(Row row : resultSet) {
					result.add(JsonObject.mapFrom(new Usuario(row.getInteger("idUsuario"), row.getString("nombre"), row.getString("apellidos"), row.getString("dni"), row.getInteger("fnacimiento"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						

			}else {
				System.out.println("Error al hacer la operación");
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
			}
		});
	}
	
	private void getIdByDni(RoutingContext routingContext) {
		mySQLPool.query("Select * from luzverde.usuario where dni= (\""+ routingContext.request().getParam("dni")+"\")" , res->{
			if(res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size() + " elemento obtenido");
				JsonArray result = new JsonArray();
				for(Row row : resultSet) {
					result.add(JsonObject.mapFrom(new Usuario(row.getInteger("idUsuario"), row.getString("nombre"), row.getString("apellidos"), row.getString("dni"), row.getInteger("fnacimiento"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());
			}else {
				System.out.println("Error al hacer la operación");
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
			}
		});
	}
	
	private void postUser(RoutingContext routingContext) {
		Usuario user = Json.decodeValue(routingContext.getBodyAsString(), Usuario.class);
		mySQLPool.preparedQuery(
				"INSERT INTO luzverde.usuario (nombre, apellidos, dni, fnacimiento) VALUES (?,?,?,?)",
				Tuple.of(user.getNombre(), user.getApellidos(), user.getDni(), user.getFnacimiento()),
				res -> {
					if (res.succeeded()) {
						System.out.println(res.result().rowCount()+" filas insertadas");
						
						long id = res.result().property(MySQLClient.LAST_INSERTED_ID);
						user.setIdUsuario((int) id);
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(user).encodePrettily());
					} else {
						System.out.println("Error al hacer la operación");
						System.out.println(res.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	private void updateUserByID(RoutingContext routingContext) {
		JsonObject body = routingContext.getBodyAsJson();
		mySQLPool.query("UPDATE luzverde.usuario SET nombre = '" + body.getString("nombre") + 
				"', apellidos = '" + body.getString("apellidos") + "', dni = '" + body.getString("dni") + "', fnacimiento = " + body.getInteger("fnacimiento")+" WHERE idUsuario = " +routingContext.request().getParam("idUsuario"),
				res -> {		
					if(res.succeeded()) {
						System.out.println("Usuario actualizado");
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(body.encodePrettily());
					}else {
						System.out.println("Error al hacer la operación");
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end(JsonObject.mapFrom(res.cause()).encodePrettily());
					}
				});
	}
	
	private void deleteUserByID(RoutingContext routingContext) {
		mySQLPool.query("DELETE FROM luzverde.usuario WHERE idUsuario = "+routingContext.request().getParam("idUsuario"), 
				res -> {
					if(res.succeeded()) {
						System.out.println("Usuario borrado");
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end();
					}else {
						System.out.println("Error al hacer la operación");
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end(JsonObject.mapFrom(res.cause()).encodePrettily());
					}
		});
	}
	
		//Cruce
	private void getCruceByUsuario (RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.cruce WHERE idUsuario = "+ routingContext.request().getParam("idUsuario"), 
				res -> {
					if(res.succeeded()) {
						RowSet<Row>  resultSet = res.result();
						System.out.println("El número de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Cruce(row.getInteger("idCruce"), row.getString("ipCruce"), row.getString("nombreCruce"),
									row.getLong("initialTimestamp"), row.getInteger("idUsuario"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						System.out.println("Error al hacer la operación");
					}
				});
	}
	
	private void getAllCruces(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.cruce", res ->{
			if(res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size()+" elementos obtenidos");
				JsonArray result = new JsonArray();
				for(Row row : resultSet) {
					result.add(JsonObject.mapFrom(new Cruce(row.getInteger("idCruce"), row.getString("ipCruce"), row.getString("nombreCruce"), row.getInteger("initialTimestamp"), row.getInteger("idUsuario"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						

			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				System.out.println("Error al hacer la operación");
			}
		});
	}
	
	private void postCruce(RoutingContext routingContext) {
		Cruce cruce = Json.decodeValue(routingContext.getBodyAsString(), Cruce.class);
		mySQLPool.preparedQuery(
				"INSERT INTO luzverde.cruce (idCruce, ipCruce, nombreCruce, initialTimestamp, idUsuario) VALUES (?,?,?,?,?)",
				Tuple.of(cruce.getIdCruce(), cruce.getIpCruce(), cruce.getNombreCruce(), cruce.getInitialTimestamp(), cruce.getIdUsuario()),
				res -> {
					if (res.succeeded()) {
						System.out.println(res.result().rowCount()+" filas insertadas");
						
						long id = res.result().property(MySQLClient.LAST_INSERTED_ID);
						cruce.setIdUsuario((int) id);
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(cruce).encodePrettily());
					} else {
						System.out.println("Error al hacer la operación");
						System.out.println(res.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	private void updateCruceByID(RoutingContext routingContext) {
		JsonObject body = routingContext.getBodyAsJson();
		mySQLPool.query("UPDATE luzverde.cruce SET ipCruce = '" + body.getString("ipCruce") + 
				"', nombreCruce = '" + body.getString("nombreCruce") + "', initialTimestamp = " + body.getLong("initialTimestamp") + ", idUsuario = " + body.getInteger("idUsuario")+" WHERE idCruce = " +routingContext.request().getParam("idCruce"),
				res -> {		
					if(res.succeeded()) {
						System.out.println("Cruce actualizado");
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(body.encodePrettily());
					}else {
						System.out.println("Error al hacer la operación");
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end(JsonObject.mapFrom(res.cause()).encodePrettily());
					}	
		});
	}
	
	private void deleteCruceByID(RoutingContext routingContext) {
		mySQLPool.query("DELETE FROM luzverde.cruce WHERE idCruce = "+routingContext.request().getParam("idCruce"), 
				res -> {
					if(res.succeeded()) {
						System.out.println("Cruce borrado");
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end();
					}else {
						System.out.println("Error al hacer la operación");
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end(JsonObject.mapFrom(res.cause()).encodePrettily());
					}
		});
	}
	
		//Semáforos
	private void getSemaforoByCruce (RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.semaforo WHERE idCruce = "+ routingContext.request().getParam("idCruce"), 
				res -> {
					if(res.succeeded()) {
						RowSet<Row>  resultSet = res.result();
						System.out.println("El número de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Semaforo(row.getInteger("idSemaforo"), row.getInteger("idCruce"), row.getString("nombreSemaforo"))));
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						System.out.println("Error al hacer la operación");
					}
				});
	}
	
	private void getAllSemaforos(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.semaforo", res ->{
			if(res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size()+" elementos obtenidos");
				JsonArray result = new JsonArray();
				for(Row row : resultSet) {
					result.add(JsonObject.mapFrom(new Semaforo(row.getInteger("idSemaforo"), row.getInteger("idCruce"), row.getString("nombreSemaforo"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						

			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				System.out.println("Error al hacer la operación");
			}
		});
	}
	
	private void postSemaforo(RoutingContext routingContext) {
		Semaforo sem = Json.decodeValue(routingContext.getBodyAsString(), Semaforo.class);
		mySQLPool.preparedQuery(
				"INSERT INTO luzverde.semaforo (idCruce, nombreSemaforo) VALUES (?,?)",
				Tuple.of(sem.getIdCruce(), sem.getNombreSemaforo()),
				res -> {
					if (res.succeeded()) {
						System.out.println(res.result().rowCount()+" filas insertadas");
						long id = res.result().property(MySQLClient.LAST_INSERTED_ID);
						sem.setIdSemaforo((int) id);
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(sem).encodePrettily());
					} else {
						System.out.println("Error al hacer la operación");
						System.out.println(res.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	private void updateNombreSemaforoByID(RoutingContext routingContext) {
		JsonObject body = routingContext.getBodyAsJson();
		mySQLPool.query("UPDATE luzverde.semaforo SET nombreSemaforo = '" + body.getString("nombreSemaforo") + 
				"' WHERE idSemaforo = " +routingContext.request().getParam("idSemaforo"),
				res -> {		
					if(res.succeeded()) {
						System.out.println("Semáforo actualizado");
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(body.encodePrettily());
					}else {
						System.out.println("Error al hacer la operación");
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end(JsonObject.mapFrom(res.cause()).encodePrettily());
					}	
		});
	}
	
		//Luz Semáforo
	private void getLuzBySemaforoAndTimestamp (RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.luz_semaforo WHERE timestamp > "
				+ routingContext.request().getParam("timestamp") + " AND idSemaforo = "
				+ routingContext.request().getParam("idSemaforo"), res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El número de elementos obtenidos es " + resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new LuzSemaforo(row.getInteger("idLuz_Semaforo"),
									row.getString("color"), row.getLong("timestamp"), row.getInteger("idSemaforo"))));
						}

						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(result.encodePrettily());
					} else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	private void getAllLuces(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM luzverde.luz_semaforo", res ->{
			if(res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				System.out.println(resultSet.size()+" elementos obtenidos");
				JsonArray result = new JsonArray();
				for(Row row : resultSet) {
					result.add(JsonObject.mapFrom(new LuzSemaforo(row.getInteger("idLuz_Semaforo"), row.getString("color"), row.getLong("timestamp"), row.getInteger("idSemaforo"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());						

			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				System.out.println("Error al hacer la operación");
			}
		});
	}
	
	private void putLuz(RoutingContext routingContext) {
		LuzSemaforo luz = Json.decodeValue(routingContext.getBodyAsString(), LuzSemaforo.class);
		mySQLPool.preparedQuery(
				"INSERT INTO luzverde.luz_semaforo (color, timestamp, idSemaforo) VALUES (?,?,?)",
				Tuple.of(luz.getColor(), luz.getTimestamp(), luz.getIdSemaforo()),
				res -> {
					if (res.succeeded()) {
						System.out.println(res.result().rowCount()+" filas insertadas");
						long id = res.result().property(MySQLClient.LAST_INSERTED_ID);
						luz.setIdLuz_Semaforo((int) id);
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(luz).encodePrettily());
					} else {
						System.out.println("Error al hacer la operación");
						System.out.println(res.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
		//Sensores
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
							System.out.println("Error al hacer la operación");
						}
					});
		}
		
		private void postSensor(RoutingContext routingContext) {
			Sensor sensor = Json.decodeValue(routingContext.getBodyAsString(), Sensor.class);
			mySQLPool.preparedQuery(
					"INSERT INTO luzverde.sensor (tipoSensor, nombreSensor, idSemaforo) VALUES (?,?,?)",
					Tuple.of(sensor.getTipoSensor(), sensor.getNombreSensor(), sensor.getIdSemaforo()),
					res -> {
						if (res.succeeded()) {
							System.out.println(res.result().rowCount()+" filas insertadas");
							
							long id = res.result().property(MySQLClient.LAST_INSERTED_ID);
							sensor.setIdSensor((int) id);
							
							routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
									.end(JsonObject.mapFrom(sensor).encodePrettily());
						} else {
							System.out.println("Error al hacer la operación");
							System.out.println(res.cause().toString());
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
									.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						}
					});
		}
		
		private void updateSensorByID(RoutingContext routingContext) {
			JsonObject body = routingContext.getBodyAsJson();
			mySQLPool.query("UPDATE luzverde.sensor SET tipoSensor = '" + body.getString("tipoSensor") + 
					"', nombreSensor = '" + body.getString("nombreSensor") + "', idSemaforo = " + body.getInteger("idSemaforo") +" WHERE idSensor = " 
					+routingContext.request().getParam("idSensor"),
		
					res -> {
						
				if(res.succeeded()) {
					System.out.println("Sensor actualizado\n");
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(body.encodePrettily());
				}else {
					System.out.println("Error al hacer la operación");
					routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end(JsonObject.mapFrom(res.cause()).encodePrettily());
				}
				
			});
		}
		
		private void deleteSensorByID(RoutingContext routingContext) {
			mySQLPool.query("DELETE FROM luzverde.sensor WHERE idSensor = "+routingContext.request().getParam("idSensor"), 
					res -> {
						if(res.succeeded()) {
							System.out.println("Usuario borrado");
							routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end();
						}else {
							System.out.println("Error al hacer la operación");
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end(JsonObject.mapFrom(res.cause()).encodePrettily());
						}
			});
		}
		
			//Sensores de Contaminación
	private void getValueBySensorCont (RoutingContext routingContext) {
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
						System.out.println("Error al hacer la operación");
					}
				});
	}
	private void getValueBySemaforo(RoutingContext routingContext) {
		mySQLPool.query("SELECT * from luzverde.valor_sensor_contaminacion where idSensor = (SELECT idSensor from sensor where idSemaforo = (SELECT luzverde.semaforo.idSemaforo from semaforo where nombreSemaforo = \"" +routingContext.request().getParam("nombreSemaforo")+"\") and tipoSensor = \"CO2\")", res ->{
					if(res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El numero de valores obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for(Row row: resultSet) {
							result.add(JsonObject.mapFrom(new  ValorSensorContaminacion(row.getInteger("idValor_sensor_contaminacion"),row.getInteger("idSensor"),
									row.getFloat("value"), row.getInteger("accuracy"), row.getLong("timestamp"))));
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json").end(JsonObject.mapFrom(res.cause()).encodePrettily());
						//System.out.println("Error al hacer la operación");
						System.out.println("SELECT luzverde.valor_sensor_contaminacion.value from luzverde.valor_sensor_contaminacion where idSensor =");
					}
				});
	}
	 
	private void putValorContaminacion(RoutingContext routingContext) {
		ValorSensorContaminacion sensor = Json.decodeValue(routingContext.getBodyAsString(), ValorSensorContaminacion.class);
		mySQLPool.preparedQuery(
				"INSERT INTO luzverde.valor_sensor_contaminacion (value, accuracy, timestamp, idSensor) VALUES (?,?,?,?)",
				Tuple.of(sensor.getValue(),sensor.getAccuracy(),sensor.getTimestamp(),sensor.getIdSensor()),
				res -> {
					if (res.succeeded()) {
						System.out.println(res.result().rowCount()+" filas insertadas");
						
						long id = res.result().property(MySQLClient.LAST_INSERTED_ID);
						sensor.setIdValor_sensor_contaminacion((int) id);
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(sensor).encodePrettily());
					} else {
						System.out.println("Error al hacer la operación");
						System.out.println(res.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}

		//Sensores de temperatura y humedad
	private void getValueBySensorTempHum (RoutingContext routingContext) {
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
						System.out.println("Error al hacer la operación");
					}
				});
	}
	
	private void putValorTempHum(RoutingContext routingContext) {
		ValorSensorTempHum sensor = Json.decodeValue(routingContext.getBodyAsString(), ValorSensorTempHum.class);
		mySQLPool.preparedQuery(
				"INSERT INTO luzverde.valor_sensor_temp_hum (valueTemp, accuracyTemp, valueHum, accuracyHum, timestamp, idSensor) VALUES (?,?,?,?,?,?)",
				Tuple.of(sensor.getValueTemp(),sensor.getAccuracyTemp(),sensor.getValueHum(),sensor.getAccuracyHum(),sensor.getTimestamp(),sensor.getIdSensor()),
				res -> {
					if (res.succeeded()) {
						System.out.println(res.result().rowCount()+" filas insertadas");
						
						long id = res.result().property(MySQLClient.LAST_INSERTED_ID);
						sensor.setIdValor_sensor_temp_hum((int) id);
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(sensor).encodePrettily());
					} else {
						System.out.println("Error al hacer la operación");
						System.out.println(res.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
}
