package luzVerdeTipos;

public class valorSensorContaminacionAcustica {

	private int idValorSensor;
	private int sensorID;
	private float value;
	private float accuracy;
	private long timeStamp;
	
	public valorSensorContaminacionAcustica(int idValorSensor, int sensorID, float value, float accuracy,
			long timeStamp) {
		super();
		this.idValorSensor = idValorSensor;
		this.sensorID = sensorID;
		this.value = value;
		this.accuracy = accuracy;
		this.timeStamp = timeStamp;
	}

	public valorSensorContaminacionAcustica() {
		super();
	}

	public int getIdValorSensor() {
		return idValorSensor;
	}

	public void setIdValorSensor(int idValorSensor) {
		this.idValorSensor = idValorSensor;
	}

	public int getSensorID() {
		return sensorID;
	}

	public void setSensorID(int sensorID) {
		this.sensorID = sensorID;
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

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(accuracy);
		result = prime * result + idValorSensor;
		result = prime * result + sensorID;
		result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
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
		valorSensorContaminacionAcustica other = (valorSensorContaminacionAcustica) obj;
		if (Float.floatToIntBits(accuracy) != Float.floatToIntBits(other.accuracy))
			return false;
		if (idValorSensor != other.idValorSensor)
			return false;
		if (sensorID != other.sensorID)
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "valorSensorContaminacionAcustica [idValorSensor=" + idValorSensor + ", sensorID=" + sensorID
				+ ", value=" + value + ", accuracy=" + accuracy + ", timeStamp=" + timeStamp + "]";
	}
	
	
	
	
}
