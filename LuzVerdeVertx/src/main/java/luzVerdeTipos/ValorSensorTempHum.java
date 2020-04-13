package luzVerdeTipos;

public class ValorSensorTempHum {
	private int idValor_sensor_temp_hum;
	private float valueTemp;
	private float accuracyTemp;
	private float valueHum;
	private float accuracyHum;
	private long timestamp;
	private int idSensor;
	
	public ValorSensorTempHum() {
		super();
	}

	public ValorSensorTempHum(int idValor_sensor_temp_hum, float valueTemp, float accuracyTemp, float valueHum, float accuracyHum, long timestamp, int idSensor) {
		super();
		this.idValor_sensor_temp_hum = idValor_sensor_temp_hum;
		this.valueTemp = valueTemp;
		this.accuracyTemp = accuracyTemp;
		this.valueHum = valueHum;
		this.accuracyHum = accuracyHum;
		this.timestamp = timestamp;
		this.idSensor = idSensor;
	}

	public int getIdValor_sensor_temp_hum() {
		return idValor_sensor_temp_hum;
	}

	public void setIdValor_sensor_temp_hum(int idValor_sensor_temp_hum) {
		this.idValor_sensor_temp_hum = idValor_sensor_temp_hum;
	}

	public float getValueTemp() {
		return valueTemp;
	}

	public void setValueTemp(float valueTemp) {
		this.valueTemp = valueTemp;
	}

	public float getAccuracyTemp() {
		return accuracyTemp;
	}

	public void setAccuracyTemp(float accuracyTemp) {
		this.accuracyTemp = accuracyTemp;
	}

	public float getValueHum() {
		return valueHum;
	}

	public void setValueHum(float valueHum) {
		this.valueHum = valueHum;
	}

	public float getAccuracyHum() {
		return accuracyHum;
	}

	public void setAccuracyHum(float accuracyHum) {
		this.accuracyHum = accuracyHum;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(accuracyHum);
		result = prime * result + Float.floatToIntBits(accuracyTemp);
		result = prime * result + idSensor;
		result = prime * result + idValor_sensor_temp_hum;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + Float.floatToIntBits(valueHum);
		result = prime * result + Float.floatToIntBits(valueTemp);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValorSensorTempHum other = (ValorSensorTempHum) obj;
		if (Float.floatToIntBits(accuracyHum) != Float.floatToIntBits(other.accuracyHum))
			return false;
		if (Float.floatToIntBits(accuracyTemp) != Float.floatToIntBits(other.accuracyTemp))
			return false;
		if (idSensor != other.idSensor)
			return false;
		if (idValor_sensor_temp_hum != other.idValor_sensor_temp_hum)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (Float.floatToIntBits(valueHum) != Float.floatToIntBits(other.valueHum))
			return false;
		if (Float.floatToIntBits(valueTemp) != Float.floatToIntBits(other.valueTemp))
			return false;
		return true;
	}

}
