package luzVerdeVerticles;

import java.util.Calendar;

import org.schors.vertx.telegram.bot.LongPollingReceiver;
import org.schors.vertx.telegram.bot.TelegramBot;
import org.schors.vertx.telegram.bot.TelegramOptions;
import org.schors.vertx.telegram.bot.api.methods.SendMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class TelegramVerticle extends AbstractVerticle {

	private TelegramBot bot;
	private static boolean token;
	private static boolean tokenRegUser;
	private static boolean tokenRegCruce;
	private static boolean tokenRegId;
	private static String dniReg="";
	private static String idReg="";
	
	@Override
	public void start(Promise<Void> future) {
		TelegramOptions telegramOptions = new TelegramOptions()
				.setBotName("LuzVerdeBot")
				.setBotToken("1244751453:AAEKNll_h_QgRnlD4fwFpTB-vX3oqFiK__s");
		bot = TelegramBot.create(vertx, telegramOptions)
				.receiver(new LongPollingReceiver().onUpdate(handler ->{
					if(handler.getMessage().getText().toLowerCase().contains("/start")) {
						bot.sendMessage(new SendMessage().setText("Hola "+ handler
								.getMessage()
								.getFrom()
								.getFirstName() + " Entre mis funciones se encuentran las siguientes: \n"
										+ "Si escribe 'usuarios' le diré los nombres de los usuarios existentes. \n"
										+ "Si escribe 'cruces' le diré el nombre de los cruces en los que se encuentran nuestros semáforos. \n"
										+ "Si escribe 'semaforos' le diré el nombre identificativo de nuestros semáforos. \n"
										+ "Si escribe 'sensores de contaminacion' podrá elegir un semáforo y ver los valores de contaminación que ha recogido \n"
										+ "Para más información del proyecto, pulse en: /info \n"
										+ "Para 'logearse' pulse en el siguiente comando: /regid \n"
										+ "Si quiere volver a ver éste mensaje, escriba de nuevo: '/start'")
								.setChatId(handler.getMessage().getChatId()));
					}else if (handler.getMessage().getText().toLowerCase().contains("usuarios")) {
						WebClient client = WebClient.create(vertx);
						client.get(8082,"localhost","/api/usuarios")
						.send(ar ->{
							if(ar.succeeded()) {
								Integer i = 0;
								String names = "";
								HttpResponse<Buffer> response = ar.result();
								JsonArray list = response.bodyAsJsonArray();
								while(list.size() > i) {
									JsonObject User = list.getJsonObject(i);
									if(i == list.size()-1 ) {
										names = names + User.getString("nombre");
									}else {
										names = names + User.getString("nombre") + ", ";
									}
									i++;
								}
								
								bot.sendMessage(new SendMessage()
										.setText("Los nombres son: " + names)
										.setChatId(handler.getMessage().getChatId()));
								
							}else {
								bot.sendMessage(new SendMessage().setText("Algo salió mal").setChatId(handler.getMessage().getChatId()));
							}
						});;
					}else if(handler.getMessage().getText().toLowerCase().contains("cruces")) {
						WebClient client = WebClient.create(vertx);
						client.get(8082,"localhost","/api/cruces")
						.send(ar ->{
							if(ar.succeeded()) {
								Integer i = 0;
								String names = "";
								HttpResponse<Buffer> response = ar.result();
								JsonArray list = response.bodyAsJsonArray();
								while(list.size() > i) {
									JsonObject User = list.getJsonObject(i);
									if(i == list.size()-1 ) {
										names = names + User.getString("nombreCruce");
									}else {
										names = names + User.getString("nombreCruce") + ", ";
									}
									i++;
								}
								
								bot.sendMessage(new SendMessage()
										.setText("Los nombres de los cruces son: " + names)
										.setChatId(handler.getMessage().getChatId()));
								
							}else {
								bot.sendMessage(new SendMessage().setText("Algo salió mal").setChatId(handler.getMessage().getChatId()));
							}
						});;
					}else if(handler.getMessage().getText().toLowerCase().contains("semaforos") || handler.getMessage().getText().toLowerCase().contains("semáforos")) {
						WebClient client = WebClient.create(vertx);
						client.get(8082,"localhost","/api/semaforos")
						.send(ar ->{
							if(ar.succeeded()) {
								Integer i = 0;
								String names = "";
								HttpResponse<Buffer> response = ar.result();
								JsonArray list = response.bodyAsJsonArray();
								while(list.size() > i) {
									JsonObject User = list.getJsonObject(i);
									if(i == list.size()-1 ) {
										names = names + User.getString("nombreSemaforo");
									}else {
										names = names + User.getString("nombreSemaforo") + ", ";
									}
									i++;
								}
								
								bot.sendMessage(new SendMessage()
										.setText("Los nombres identificadores de los semáforos son: " + names)
										.setChatId(handler.getMessage().getChatId()));
								
							}else {
								bot.sendMessage(new SendMessage().setText("Algo salió mal").setChatId(handler.getMessage().getChatId()));
							}
						});;
					}else if(handler.getMessage().getText().toLowerCase().contains("sensores") && (handler.getMessage().getText().toLowerCase().contains("contaminacion") 
							|| handler.getMessage().getText().toLowerCase().contains("contaminación")) && token == false) {
						WebClient client = WebClient.create(vertx);
						client.get(8082,"localhost","/api/semaforos")
						.send(ar ->{
							if(ar.succeeded()) {
								Integer i = 0;
								String names = "";
								HttpResponse<Buffer> response = ar.result();
								JsonArray list = response.bodyAsJsonArray();
								while(list.size() > i) {
									JsonObject User = list.getJsonObject(i);
									if(i == list.size()-1 ) {
										names = names +"/"+ User.getString("nombreSemaforo");
									}else {
										names = names +"/"+ User.getString("nombreSemaforo") + " \n";
									}
									i++;
								}
								
								bot.sendMessage(new SendMessage()
										.setText("Elija el nombre del semáforo del que desee ver sus valores recogidos: \n" + names)
										.setChatId(handler.getMessage().getChatId()));
								
							}else {
								bot.sendMessage(new SendMessage().setText("Algo salió mal").setChatId(handler.getMessage().getChatId()));
							}
						});;
						token = true;
						
					}else if(handler.getMessage().getText().toLowerCase().startsWith("/") && token == true) {
						String nombreSem= handler.getMessage().getText().substring(1);
						WebClient client = WebClient.create(vertx);
						client.get(8082, "localhost", "/api/valores_sensor_contaminacion/"+nombreSem+"/CO2")
						.send(ar->{
							if(ar.succeeded()) {
								Integer i = 0;
								String values= "";
								HttpResponse<Buffer> response = ar.result();
								JsonArray list = response.bodyAsJsonArray();
								while(list.size()>i) {
									JsonObject valores = list.getJsonObject(i);
									if(i==list.size()-1) {
										values = values + valores.getFloat("value").toString();
									}else {
										values = values + valores.getFloat("value").toString() + ", ";
									}
									i++;
								}
								bot.sendMessage(new SendMessage().
										setText("Los valores son los siguientes: \n"
												+ values)
										.setChatId(handler.getMessage().getChatId()));
							}else {
								bot.sendMessage(new SendMessage().
										setText("Error al consultar los datos")
										.setChatId(handler.getMessage().getChatId()));
							}
						});
						token = false;
					}else if (tokenRegUser== false && handler.getMessage().getText().toLowerCase().contains("registrarme")) {
						bot.sendMessage(new SendMessage()
								.setText("Para registrarse deberá decirme su nombre, apellidos, DNI y su fecha de nacimiento en el siguiente formato: DDMMAAAA, donde D es el día, M el mes y A el año."
										+ " Cada dato tiene que estar separado por un salto de línea, un ejemplo: \n"
										+ "José Joaquín\n"
										+ "Comitre Palacios\n"
										+ "11111111J\n"
										+ "18101999")
								.setChatId(handler.getMessage().getChatId()));
						tokenRegUser = true;
					}else if(tokenRegUser== true && (handler.getMessage().getText().endsWith("1") ||handler.getMessage().getText().endsWith("2") ||handler.getMessage().getText().endsWith("3") ||
							handler.getMessage().getText().endsWith("4") ||handler.getMessage().getText().endsWith("5") ||handler.getMessage().getText().endsWith("6") ||handler.getMessage().getText().endsWith("7") ||
							handler.getMessage().getText().endsWith("8") ||handler.getMessage().getText().endsWith("9") || handler.getMessage().getText().endsWith("0"))) {
						String []regUser= handler.getMessage().getText().split("\\n");
						if(regUser.length != 4) {
							bot.sendMessage(new SendMessage().setText("No tiene el formato adecuado, empiece de nuevo la operación escribiendo 'registrarme'"));
						}else {
							bot.sendMessage(new SendMessage().setText("Procesando datos...").setChatId(handler.getMessage().getChatId()));
							WebClient client = WebClient.create(vertx);
							client.post(8082, "localhost", "/api/usuarios")
							.sendJsonObject(new JsonObject().put("nombre", regUser[0])
									.put("apellidos", regUser[1])
									.put("dni",regUser[2])
									.put("fnacimiento",Integer.parseInt(regUser[3]))
									,ar ->{
									if(ar.succeeded()) {
										bot.sendMessage(new SendMessage().setText("Procesado exitoso, su registro se ha llevado a cabo. \n"
												+"Para conocer su ID pulse en el siguiente comando: /id").setChatId(handler.getMessage().getChatId()));
										dniReg= regUser[2];
									}else{
										bot.sendMessage(new SendMessage().setText("Error al procesar los datos, repita el registro de nuevo.").setChatId(handler.getMessage().getChatId()));
									}
							});
						}
					}else if(handler.getMessage().getText().toLowerCase().contains("/id") && tokenRegUser == true) {
						WebClient client = WebClient.create(vertx);
						client.get(8082,"localhost", "/api/usuario/"+dniReg)
						.send(ar->{
							if(ar.succeeded()) {
							Integer i = 0;
							String id= "";
							HttpResponse<Buffer> response = ar.result();
							JsonArray list = response.bodyAsJsonArray();
							while(list.size()>i) {
								JsonObject user = list.getJsonObject(i);
								id = user.getInteger("idUsuario").toString();
								i++;
							}
							bot.sendMessage(new SendMessage().
									setText("Su ID es el siguiente: \n"
											+ id+ "\n"
											+"No lo comparta con nadie y apúntelo para poder añadir cruces al sistema")
									.setChatId(handler.getMessage().getChatId()));
							idReg=id;
							tokenRegUser=false;
							}else {
								bot.sendMessage(new SendMessage().
										setText("Error al consultar los datos")
										.setChatId(handler.getMessage().getChatId()));
							}
						});
					}else if(tokenRegCruce == false && handler.getMessage().getText().toLowerCase().contains("registrar") && handler.getMessage().getText().toLowerCase().contains("cruce") && idReg != "") {
						bot.sendMessage(new SendMessage()
								.setText("Para registrar el cruce deberá decirme la Ip del cruce y el nombre del cruce \n"
										+ "Un ejemplo de registro de cruce: \n"
										+ "100.100.100.101\n"
										+ "Avda. Palmera - Cardenal Ilundain")
								.setChatId(handler.getMessage().getChatId()));
						tokenRegCruce = true;
					}else if(tokenRegCruce==true && (handler.getMessage().getText().startsWith("0") ||handler.getMessage().getText().startsWith("1") ||handler.getMessage().getText().startsWith("2") )) {
						String []regCruce= handler.getMessage().getText().split("\\n");
						if(regCruce.length != 2) {
							bot.sendMessage(new SendMessage().setText("No tiene el formato adecuado, empiece de nuevo la operación escribiendo 'registrar cruce'"));
						}else {
							bot.sendMessage(new SendMessage().setText("Procesando datos...").setChatId(handler.getMessage().getChatId()));
							WebClient client = WebClient.create(vertx);
							client.post(8082, "localhost", "/api/cruces")
							.sendJsonObject(new JsonObject().put("ipCruce", regCruce[0])
									.put("nombreCruce", regCruce[1])
									.put("initialTimestamp", Calendar.getInstance().getTimeInMillis())
									.put("idUsuario",Integer.parseInt(idReg))
									,ar ->{
									if(ar.succeeded()) {
										bot.sendMessage(new SendMessage().setText("Procesado exitoso, su registro se ha llevado a cabo.").setChatId(handler.getMessage().getChatId()));
									}else{
										bot.sendMessage(new SendMessage().setText("Error al procesar los datos, repita el registro de nuevo.").setChatId(handler.getMessage().getChatId()));
									}
							});
						}
						
						tokenRegCruce = false;
					}if(handler.getMessage().getText().toLowerCase().substring(1).contains("info")) {
						bot.sendMessage(new SendMessage()
								.setText("Este es un bot diseñado por José Joaquín Comitre Palacios y Alberto Naranjo Rodríguez, en su proyecto de semáforos inteligentes, Luz Verde. \n"
										+"Diseñado para la asignatura de Desarrollo de Aplicaciones Distribuidas, en la ETSII, Sevilla. \n"
										+ "Toda la información que muestra éste bot está dentro de la base de datos del ordenador de uno de los diseñadores, en la que se ejecute este programa.")
								.setChatId(handler.getMessage().getChatId()));
					}if(handler.getMessage().getText().toLowerCase().contains("/regid") && tokenRegId==false) {
						bot.sendMessage(new SendMessage().setText("Dígame su ID").setChatId(handler.getMessage().getChatId()));
						tokenRegId=true;
					}else if(tokenRegId==true && (handler.getMessage().getText().endsWith("1") ||handler.getMessage().getText().endsWith("2") ||handler.getMessage().getText().endsWith("3") ||
							handler.getMessage().getText().endsWith("4") ||handler.getMessage().getText().endsWith("5") ||handler.getMessage().getText().endsWith("6") ||handler.getMessage().getText().endsWith("7") ||
							handler.getMessage().getText().endsWith("8") ||handler.getMessage().getText().endsWith("9") || handler.getMessage().getText().endsWith("0"))) {
						WebClient client = WebClient.create(vertx);
						client.get(8082,"localhost","/api/usuarios/"+handler.getMessage().getText())
						.send(ar->{
							if(ar.succeeded()) {
								Integer i = 0;
								String nombre= "";
								HttpResponse<Buffer> response = ar.result();
								JsonArray list = response.bodyAsJsonArray();
								while(list.size()>i) {
									JsonObject user = list.getJsonObject(i);
									if(user.getInteger("idUsuario") == Integer.parseInt(handler.getMessage().getText())){
									nombre = user.getString("nombre");
									}
									i++;
								}
								if(nombre != "") {
								bot.sendMessage(new SendMessage().setText("Muy buenas, "+nombre+"\n"
										+ "Recuerde que si quiere añadir cruces, debe 'logearse' siempre").setChatId(handler.getMessage().getChatId()));
								}else {
									bot.sendMessage(new SendMessage().setText("No se encuentra registrado en nuestro sistema.").setChatId(handler.getMessage().getChatId()));
								}
								idReg=handler.getMessage().getText();
								tokenRegId=false;
							}
						});
						
					}
					
				}));
		bot.start();
	}
	

}
