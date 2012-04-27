package il.ac.idc.jdt;

/**
 * this class represents a simple circle. <br />
 * it is used by the Delaunay Triangulation class. <br />
 * <br />
 * note that this class is immutable.
 * 
 * @see DelaunayTriangulation
 */
public class Circle {

	private Point c;
	private double r;

	/**
	 * Constructor. <br />
	 * Constructs a new Circle_dt.
	 * 
	 * @param c
	 *            Center of the circle.
	 * @param r
	 *            Radius of the circle.
	 */
	public Circle(Point c, double r) {
		this.c = c;
		this.r = r;
	}

	/**
	 * Copy Constructor. <br />
	 * Creates a new Circle with same properties of <code>circ</code>.
	 * 
	 * @param circ
	 *            Circle to clone.
	 */
	public Circle(Circle circ) {
		this.c = circ.c;
		this.r = circ.r;
	}

	@Override
	public String toString() {
		return "Circle [c=" + c + ", r=" + r + "]";
	}

	/**
	 * Gets the center of the circle.
	 * 
	 * @return the center of the circle.
	 */
	public Point center() {
		return this.c;
	}

	/**
	 * Gets the radius of the circle.
	 * 
	 * @return the radius of the circle.
	 */
	public double radius() {
		return this.r;
	}
}
