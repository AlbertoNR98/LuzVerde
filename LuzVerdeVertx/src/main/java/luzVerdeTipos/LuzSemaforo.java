package luzVerdeTipos;

public class LuzSemaforo {
	private int idLuz_Semaforo;
	private String color;
	private long timestamp;
	private int idSemaforo;
	
	public LuzSemaforo() {
		super();
	}
	
	public LuzSemaforo(int idLuz_Semaforo, String color, long timestamp, int idSemaforo) {
		super();
		this.idLuz_Semaforo = idLuz_Semaforo;
		this.color = color;
		this.timestamp = timestamp;
		this.idSemaforo = idSemaforo;
	}
	
	public LuzSemaforo(String color, long timestamp, int idSemaforo) {
		super();
		this.color = color;
		this.timestamp = timestamp;
		this.idSemaforo = idSemaforo;
	}

	public int getIdLuz_Semaforo() {
		return idLuz_Semaforo;
	}

	public void setIdLuz_Semaforo(int idLuz_Semaforo) {
		this.idLuz_Semaforo = idLuz_Semaforo;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + idLuz_Semaforo;
		result = prime * result + idSemaforo;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		LuzSemaforo other = (LuzSemaforo) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (idLuz_Semaforo != other.idLuz_Semaforo)
			return false;
		if (idSemaforo != other.idSemaforo)
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}
	
	
}
