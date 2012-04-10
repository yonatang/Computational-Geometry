package idc.gc;

public class Squere {

	private Point topLeft;
	
	private double edge;

	public Squere(Point topLeft, double edge) {
		super();
		this.topLeft = topLeft;
		this.edge = edge;
	}

	public Point getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(Point topLeft) {
		this.topLeft = topLeft;
	}

	public double getEdge() {
		return edge;
	}

	public void setEdge(double edge) {
		this.edge = edge;
	}

	@Override
	public String toString() {
		return "Squere [topLeft=" + topLeft + ", edge=" + edge + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(edge);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((topLeft == null) ? 0 : topLeft.hashCode());
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
		Squere other = (Squere) obj;
		if (Double.doubleToLongBits(edge) != Double.doubleToLongBits(other.edge))
			return false;
		if (topLeft == null) {
			if (other.topLeft != null)
				return false;
		} else if (!topLeft.equals(other.topLeft))
			return false;
		return true;
	}
	
	
}
