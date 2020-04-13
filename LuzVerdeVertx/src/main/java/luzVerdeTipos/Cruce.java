package luzVerdeTipos;

public class Cruce {
	private int idCruce;
	private String ipCruce;
	private String nombreCruce;
	private long initialTimestamp;
	private int idUsuario;
	
	public Cruce() {
		super();
	}
	
	public Cruce(int idCruce, String ipCruce, String nombreCruce, long initialTimestamp, int idUsuario) {
		super();
		this.idCruce = idCruce;
		this.ipCruce = ipCruce;
		this.nombreCruce = nombreCruce;
		this.initialTimestamp = initialTimestamp;
		this.idUsuario = idUsuario;
	}

	public int getIdCruce() {
		return idCruce;
	}

	public void setIdCruce(int idCruce) {
		this.idCruce = idCruce;
	}

	public String getIpCruce() {
		return ipCruce;
	}

	public void setIpCruce(String ipCruce) {
		this.ipCruce = ipCruce;
	}

	public String getNombreCruce() {
		return nombreCruce;
	}

	public void setNombreCruce(String nombreCruce) {
		this.nombreCruce = nombreCruce;
	}

	public long getInitialTimestamp() {
		return initialTimestamp;
	}

	public void setInitialTimestamp(long initialTimestamp) {
		this.initialTimestamp = initialTimestamp;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idCruce;
		result = prime * result + idUsuario;
		result = prime * result + (int) (initialTimestamp ^ (initialTimestamp >>> 32));
		result = prime * result + ((ipCruce == null) ? 0 : ipCruce.hashCode());
		result = prime * result + ((nombreCruce == null) ? 0 : nombreCruce.hashCode());
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
		Cruce other = (Cruce) obj;
		if (idCruce != other.idCruce)
			return false;
		if (idUsuario != other.idUsuario)
			return false;
		if (initialTimestamp != other.initialTimestamp)
			return false;
		if (ipCruce == null) {
			if (other.ipCruce != null)
				return false;
		} else if (!ipCruce.equals(other.ipCruce))
			return false;
		if (nombreCruce == null) {
			if (other.nombreCruce != null)
				return false;
		} else if (!nombreCruce.equals(other.nombreCruce))
			return false;
		return true;
	}
	
	
}
