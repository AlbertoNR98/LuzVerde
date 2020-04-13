package luzVerdeTipos;

public class Semaforo {
	private int idSemaforo;
	private int idCruce;
	private String nombreSemaforo;
	
	public Semaforo() {
		super();
	}
	
	public Semaforo(int idSemaforo, int idCruce, String nombreSemaforo) {
		super();
		this.idSemaforo = idSemaforo;
		this.idCruce = idCruce;
		this.nombreSemaforo = nombreSemaforo;
	}

	public int getIdSemaforo() {
		return idSemaforo;
	}

	public void setIdSemaforo(int idSemaforo) {
		this.idSemaforo = idSemaforo;
	}

	public int getIdCruce() {
		return idCruce;
	}

	public void setIdCruce(int idCruce) {
		this.idCruce = idCruce;
	}

	public String getNombreSemaforo() {
		return nombreSemaforo;
	}

	public void setNombreSemaforo(String nombreSemaforo) {
		this.nombreSemaforo = nombreSemaforo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idCruce;
		result = prime * result + idSemaforo;
		result = prime * result + ((nombreSemaforo == null) ? 0 : nombreSemaforo.hashCode());
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
		Semaforo other = (Semaforo) obj;
		if (idCruce != other.idCruce)
			return false;
		if (idSemaforo != other.idSemaforo)
			return false;
		if (nombreSemaforo == null) {
			if (other.nombreSemaforo != null)
				return false;
		} else if (!nombreSemaforo.equals(other.nombreSemaforo))
			return false;
		return true;
	}
	
}
