package luzVerdeVerticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

//Verticle principal.
public class MainVerticle extends AbstractVerticle{
	@Override
	public void start(Future<Void> startFuture) {
		//Despliegue del bot de Telegram (necesario para introducir los datos)
		vertx.deployVerticle(TelegramVerticle.class.getName());

		//Despliegue de API REST
		vertx.deployVerticle(DatabaseVerticle.class.getName());
		
		//Despliegue de cliente y servidor MQTT -> El verticle MqttClient simula un cliente como ejemplo
		//vertx.deployVerticle(MqttServerVerticle.class.getName());
		//vertx.deployVerticle(MqttClientVerticle.class.getName());
	}
}
