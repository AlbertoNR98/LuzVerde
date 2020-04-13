package luzVerdeTipos;

public class Sensor {
	private int idSensor;
	private String tipoSensor;
	private String nombreSensor;
	private int idSemaforo;
	
	public Sensor() {
		super();
	}
	
	public Sensor(int idSensor, String tipoSensor, String nombreSensor, int idSemaforo) {
		super();
		this.idSensor = idSensor;
		this.tipoSensor = tipoSensor;
		this.nombreSensor = nombreSensor;
		this.idSemaforo = idSemaforo;
	}

	public int getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}

	public String getTipoSensor() {
		return tipoSensor;
	}

	public void setTipoSensor(String tipoSensor) {
		this.tipoSensor = tipoSensor;
	}

	public String getNombreSensor() {
		return nombreSensor;
	}

	public void setNombreSensor(String nombreSensor) {
		this.nombreSensor = nombreSensor;
	}

	public int getIdSemaforo() {
		return idSemaforo;
	}

	public void setIdSemaforo(int idSemaforo) {
		this.idSemaforo = idSemaforo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idSemaforo;
		result = prime * result + idSensor;
		result = prime * result + ((nombreSensor == null) ? 0 : nombreSensor.hashCode());
		result = prime * result + ((tipoSensor == null) ? 0 : tipoSensor.hashCode());
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
		Sensor other = (Sensor) obj;
		if (idSemaforo != other.idSemaforo)
			return false;
		if (idSensor != other.idSensor)
			return false;
		if (nombreSensor == null) {
			if (other.nombreSensor != null)
				return false;
		} else if (!nombreSensor.equals(other.nombreSensor))
			return false;
		if (tipoSensor == null) {
			if (other.tipoSensor != null)
				return false;
		} else if (!tipoSensor.equals(other.tipoSensor))
			return false;
		return true;
	}
	
}
