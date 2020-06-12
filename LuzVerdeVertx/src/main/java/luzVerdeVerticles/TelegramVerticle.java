package luzVerdeVerticles;

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
								.getFirstName() + " ¿En qué puedo ayudarte?")
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
					}else if (handler.getMessage().getText().toLowerCase().contains("registrarme")) {
						Integer i=0;
						String name ="";
						String surrname="";
						String dni="";
						Long birthday;
						bot.sendMessage(new SendMessage()
								.setText("Perfecto, dígame su nombre")
								.setChatId(handler.getMessage().getChatId()));
						bot.stop();
						if(!(handler.getMessage().getText().contains("registrarme"))) {
							name = handler.getMessage().getText();
							bot.start().sendMessage(new SendMessage()
									.setText("Perfecto "+ "Depuracion: "+ name)
									.setChatId(handler.getMessage().getChatId()));
						}

						
					}
				}));
		bot.start();
	}
	

}
