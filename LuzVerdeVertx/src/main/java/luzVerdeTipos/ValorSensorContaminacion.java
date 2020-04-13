package luzVerdeTipos;

public class ValorSensorContaminacion {

	private int idValor_sensor_contaminacion;
	private float value;
	private float accuracy;
	private long timestamp;
	private int idSensor;
	
	public ValorSensorContaminacion(int idValor_sensor_contaminacion, int idSensor, float value, float accuracy, long timestamp) {
		super();
		this.idValor_sensor_contaminacion = idValor_sensor_contaminacion;
		this.idSensor = idSensor;
		this.value = value;
		this.accuracy = accuracy;
		this.timestamp = timestamp;
	}

	public ValorSensorContaminacion() {
		super();
	}

	public int getIdValor_sensor_contaminacion() {
		return idValor_sensor_contaminacion;
	}

	public void setIdValor_sensor_contaminacion(int idValor_sensor_contaminacion) {
		this.idValor_sensor_contaminacion = idValor_sensor_contaminacion;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
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
		result = prime * result + Float.floatToIntBits(accuracy);
		result = prime * result + idSensor;
		result = prime * result + idValor_sensor_contaminacion;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + Float.floatToIntBits(value);
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
		ValorSensorContaminacion other = (ValorSensorContaminacion) obj;
		if (Float.floatToIntBits(accuracy) != Float.floatToIntBits(other.accuracy))
			return false;
		if (idSensor != other.idSensor)
			return false;
		if (idValor_sensor_contaminacion != other.idValor_sensor_contaminacion)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}
	
	
}
