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
	
	public static final String TOPIC_LIGHTS = "lights";
	public static final String TOPIC_INFO = "info";
	public static final String TOPIC_DOMO = "domo";
	
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
			System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());
			if(endpoint.auth().getUsername().contentEquals("mqttbroker") && endpoint.auth().getPassword().contentEquals("mqttbrokerpass")) {
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
				System.out.println("MQTT server is listening on port "+ ar.result().actualPort());
			}else {
				System.out.println("Error on starting the server");
				ar.cause().printStackTrace();
			}
			
		});
	}
	
	private void handleSubscription(MqttEndpoint endpoint) {
		endpoint.subscribeHandler(subscribe ->{
			List <MqttQoS> grantedQoSLevels = new ArrayList();
			for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
				System.out.println("Subscription for " + s.topicName() + "with QoS " + s.qualityOfService());
				grantedQoSLevels.add(s.qualityOfService());
				clients.put(s.topicName(), endpoint);		//MIERDA DE GOOGLE
			}
			endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQoSLevels);
		});
	}
	
	private void handleUnSubscription (MqttEndpoint endpoint) {
		endpoint.unsubscribeHandler(unsubscribe ->{
			for (String t: unsubscribe.topics()) {
				System.out.println("Unsubscribe for " + t);
			}
			endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
		});
	}
	
	private void publishHandler(MqttEndpoint endpoint) {
		endpoint.publishHandler(message ->{
			
			
			if(message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
				String topicName = message.topicName();
				System.out.println("New Message published in " + topicName);
				for (MqttEndpoint subscribed : clients.get(topicName)) {
					subscribed.publish(message.topicName(), message.payload(), message.qosLevel() , message.isDup(), message.isRetain());
				}
				endpoint.publishAcknowledge(message.messageId());
			}else if(message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
				// FALSO?
				
				endpoint.publishRelease(message.messageId());
			}
				
			
		}).publishReleaseHandler(messageId ->{
			endpoint.publishComplete(messageId);
		});
	}
	
	private void handleClientDisconnect (MqttEndpoint endpoint) {
		endpoint.disconnectHandler(h ->{
			System.out.println("The remote client has closed the connection");
			
		});
	}
	
}
