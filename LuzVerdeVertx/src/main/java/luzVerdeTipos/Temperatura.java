package luzVerdeTipos;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Temperatura {

	
	private static final AtomicInteger COUNTER = new AtomicInteger() ;
	
	private int id;
	private float value;
	private long timestamp;  //ms desde una fecha determinada (nosecual, 1 de enero de XXXX), para trabajar con fechas
	private String location;
	private int accurancy;


	@JsonCreator
	
	public Temperatura(
	@JsonProperty("valor")float value, 
	@JsonProperty("timestamp")long timestamp,
	@JsonProperty("localizacion") String location,
	@JsonProperty("accurancy")int accurancy) {
		super();
		this.id = COUNTER.getAndIncrement();
		this.value = value;
		this.timestamp = timestamp;
		this.location = location;
		this.accurancy = accurancy;
	}

	
	public Temperatura() {
		super();
		this.id = COUNTER.getAndIncrement();
		this.value = 0;
		this.timestamp = Calendar.getInstance().getTimeInMillis(); //Fecha actual en ms
		/*
		Si se quiere hacer respecto a una fecha concreta se hace lo siguiente:
		 
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 25);
		calendar.set(Calendar.MONTH,JANUARY);
		calendar.set(Calendar.HOUR,11);
		calendar.set(Calendar.MINUTE,15);
		this.timestamp = calendar.getTimeInMillis();
		
		*/
		this.location = "";
		this.accurancy = 0;
	}
	
	public int getID() {
		return id;
	}
	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getAccurancy() {
		return accurancy;
	}

	public void setAccurancy(int accurancy) {
		this.accurancy = accurancy;
	}
	
}
