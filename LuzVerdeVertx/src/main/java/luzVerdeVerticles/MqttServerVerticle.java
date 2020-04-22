package luzVerdeVerticles;


import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

import io.vertx.mqtt.MqttEndpoint;


import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.ClientAuth;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.mqtt.MqttTopicSubscription;

public class MqttServerVerticle extends AbstractVerticle{
	
	public static final String TOPIC_CONT = "contaminacion";
	public static final String TOPIC_TEMP_HUM = "temp_hum";
	public static final String TOPIC_LUZ = "luces";
	
	private static final SetMultimap<String ,MqttEndpoint> clients = LinkedHashMultimap.create();
	
	public void start (Promise <Void> promise) {
		MqttServerOptions options = new MqttServerOptions();
		options.setPort(1885);
		options.setClientAuth(ClientAuth.REQUIRED);
		MqttServer mqttServer = MqttServer.create(vertx, options);
		init(mqttServer);
	}
	
	public void init(MqttServer mqttServer) {
		mqttServer.endpointHandler(endpoint ->{
			System.out.println("Cliente MQTT [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());
			if(endpoint.auth().getUsername().contentEquals("luzverde") && endpoint.auth().getPassword().contentEquals("ZeUS")) {
				endpoint.accept();
				handleSubscription(endpoint);
				handleUnSubscription(endpoint);
				publishHandler(endpoint);
				handleClientDisconnect(endpoint);
			}else {
				endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
				
			}
		}).listen(ar ->{
			if(ar.succeeded()) {
				System.out.println("Servidor MQTT en el puerto: "+ ar.result().actualPort());
			}else {
				System.out.println("Error desplegando servidor");
				ar.cause().printStackTrace();
			}
			
		});
	}
	
	private void handleSubscription(MqttEndpoint endpoint) {
		endpoint.subscribeHandler(subscribe ->{
			List <MqttQoS> grantedQoSLevels = new ArrayList<>();
			for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
				System.out.println("Suscripcion a " + s.topicName() + "con QoS " + s.qualityOfService());
				grantedQoSLevels.add(s.qualityOfService());
				clients.put(s.topicName(), endpoint);
			}
			endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQoSLevels);
		});
	}
	
	private void handleUnSubscription (MqttEndpoint endpoint) {
		endpoint.unsubscribeHandler(unsubscribe ->{
			for (String t: unsubscribe.topics()) {
				System.out.println("Desuscripcion de " + t);
			}
			endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
		});
	}
	
	private void publishHandler(MqttEndpoint endpoint) {
		endpoint.publishHandler(message ->{
			if(message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
				String topicName = message.topicName();
				System.out.println("Nuevo mensaje en: " + topicName);
				for (MqttEndpoint subscribed : clients.get(topicName)) {
					subscribed.publish(message.topicName(), message.payload(), message.qosLevel() , message.isDup(), message.isRetain());
				}
				endpoint.publishAcknowledge(message.messageId());
			}else if(message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
				endpoint.publishRelease(message.messageId());
			}
				
			
		}).publishReleaseHandler(messageId ->{
			endpoint.publishComplete(messageId);
		});
	}
	
	private void handleClientDisconnect (MqttEndpoint endpoint) {
		endpoint.disconnectHandler(h ->{
			System.out.println("El cliente remoto se ha desconectado");
			
		});
	}
	
}
