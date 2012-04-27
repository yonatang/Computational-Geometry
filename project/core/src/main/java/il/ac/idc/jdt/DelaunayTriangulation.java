package il.ac.idc.jdt;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * 
 * This class represents a Delaunay Triangulation. The class was written for a
 * large scale triangulation (1000 - 200,000 vertices). The application main use
 * is 3D surface (terrain) presentation. <br>
 * The class main properties are the following:<br>
 * - fast point location. (O(n^0.5)), practical runtime is often very fast. <br>
 * - handles degenerate cases and none general position input (ignores duplicate
 * points). <br>
 * - save & load from\to text file in TSIN format. <br>
 * - 3D support: including z value approximation. <br>
 * - standard java (1.5 generic) iterators for the vertices and triangles. <br>
 * - smart iterator to only the updated triangles - for terrain simplification <br>
 * <br>
 * 
 * Testing (done in early 2005): Platform java 1.5.02 windows XP (SP2), AMD
 * laptop 1.6G sempron CPU 512MB RAM. Constructing a triangulation of 100,000
 * vertices takes ~ 10 seconds. point location of 100,000 points on a
 * triangulation of 100,000 vertices takes ~ 5 seconds.
 * 
 * Note: constructing a triangulation with 200,000 vertices and more requires
 * extending java heap size (otherwise an exception will be thrown).<br>
 * 
 * Bugs: if U find a bug or U have an idea as for how to improve the code,
 * please send me an email to: benmo@ariel.ac.il
 * 
 * @author Boaz Ben Moshe 5/11/05 <br>
 *         The project uses some ideas presented in the VoroGuide project,
 *         written by Klasse f?r Kreise (1996-1997), For the original applet
 *         see: http://www.pi6.fernuni-hagen.de/GeomLab/VoroGlide/ . <br>
 */

public class DelaunayTriangulation {

	// the first and last points (used only for first step construction)
	private Point firstP;
	private Point lastP;

	// for degenerate case!
	private boolean allCollinear;

	// the first and last triangles (used only for first step construction)
	private Triangle firstT, lastT, currT;

	// the triangle the fond (search start from
	private Triangle startTriangle;

	// the triangle the convex hull starts from
	private Triangle startTriangleHull;

	private int nPoints = 0; // number of points
	// additional data 4/8/05 used by the iterators
	private Set<Point> vertices;
	private Vector<Triangle> triangles;

	// The triangles that were deleted in the last deletePoint iteration.
	private Vector<Triangle> deletedTriangles;
	// The triangles that were added in the last deletePoint iteration.
	private Vector<Triangle> addedTriangles;

	private int modCount = 0, modCount2 = 0;

	// the Bounding Box, {{x0,y0,z0} , {x1,y1,z1}}
	private Point bbMin, bbMax;

	/**
	 * Index for faster point location searches
	 */
	private GridIndex gridIndex = null;

	/**
	 * creates an empty Delaunay Triangulation.
	 */
	public DelaunayTriangulation() {
		this(new Point[] {});
	}

	public DelaunayTriangulation(InputStream stream) throws IOException, UnsupportedFormatException {
		this(IOParsers.read(stream));
	}

	/**
	 * creates a Delaunay Triangulation from all the points. Note: duplicated
	 * points are ignored.
	 */
	public DelaunayTriangulation(Point[] ps) {
		modCount = 0;
		modCount2 = 0;
		bbMin = null;
		bbMax = null;
		this.vertices = new TreeSet<Point>(Point.getComparator());
		triangles = new Vector<Triangle>();
		deletedTriangles = null;
		addedTriangles = new Vector<Triangle>();
		allCollinear = true;
		for (int i = 0; ps != null && i < ps.length && ps[i] != null; i++) {
			this.insertPoint(ps[i]);
		}
	}

	/**
	 * creates a Delaunay Triangulation from all the points in the suggested
	 * tsin file or from a smf file (off like). if the file name is .smf - read
	 * it as an smf file as try to read it as .tsin <br>
	 * Note: duplicated points are ignored! <br>
	 * SMF file has an OFF like format (a face (f) is presented by the indexes
	 * of its points - starting from 1 - not from 0): <br>
	 * begin <br>
	 * v x1 y1 z1 <br>
	 * ... <br>
	 * v xn yn zn <br>
	 * f i11 i12 i13 <br>
	 * ... <br>
	 * f im1 im2 im3 <br>
	 * end <br>
	 * <br>
	 * The tsin text file has the following (very simple) format <br>
	 * vertices# (n) <br>
	 * x1 y1 z1 <br>
	 * ... <br>
	 * xn yn zn <br>
	 * 
	 * 
	 */
	public DelaunayTriangulation(String file) throws IOException, UnsupportedFormatException {
		this(IOParsers.read(file));
	}

	/**
	 * the number of (different) vertices in this triangulation.
	 * 
	 * @return the number of vertices in the triangulation (duplicates are
	 *         ignore - set size).
	 */
	public int size() {
		if (vertices == null) {
			return 0;
		}
		return vertices.size();
	}

	/**
	 * @return the number of triangles in the triangulation. <br />
	 *         Note: includes infinife faces!!.
	 */
	public int trianglesSize() {
		this.initTriangles();
		return triangles.size();
	}

	/**
	 * returns the changes counter for this triangulation
	 */
	public int getModeCounter() {
		return this.modCount;
	}

	/**
	 * insert the point to this Delaunay Triangulation. Note: if p is null or
	 * already exist in this triangulation p is ignored.
	 * 
	 * @param p
	 *            new vertex to be inserted the triangulation.
	 */
	public void insertPoint(Point p) {
		if (this.vertices.contains(p))
			return;
		modCount++;
		updateBoundingBox(p);
		this.vertices.add(p);
		Triangle t = insertPointSimple(p);
		if (t == null) //
			return;
		Triangle tt = t;
		currT = t; // recall the last point for - fast (last) update iterator.
		do {
			flip(tt, modCount);
			tt = tt.getCanext();
		} while (tt != t && !tt.isHalfplane());

		// Update index with changed triangles
		if (gridIndex != null)
			gridIndex.updateIndex(getLastUpdatedTriangles());
	}

	/**
	 * Deletes the given point from this.
	 * 
	 * @param pointToDelete
	 *            The given point to delete.
	 * 
	 *            Implementation of the Mostafavia, Gold & Dakowicz algorithm
	 *            (2002).
	 * 
	 *            By Eyal Roth & Doron Ganel (2009).
	 */
	public void deletePoint(Point pointToDelete) {

		// Finding the triangles to delete.
		Vector<Point> pointsVec = findConnectedVertices(pointToDelete, true);
		if (pointsVec == null) {
			return;
		}

		while (pointsVec.size() >= 3) {
			// Getting a triangle to add, and saving it.
			Triangle triangle = findTriangle(pointsVec, pointToDelete);
			addedTriangles.add(triangle);

			// Finding the point on the diagonal (pointToDelete,p)
			Point p = findDiagonal(triangle, pointToDelete);

			for (Point tmpP : pointsVec) {
				if (tmpP.equals(p)) {
					pointsVec.removeElement(tmpP);
					break;
				}
			}
		}
		// updating the trangulation
		deleteUpdate(pointToDelete);
		for (Triangle t : deletedTriangles) {
			if (t == startTriangle) {
				startTriangle = addedTriangles.elementAt(0);
				break;
			}
		}
		triangles.removeAll(deletedTriangles);
		triangles.addAll(addedTriangles);
		vertices.remove(pointToDelete);
		nPoints = nPoints + addedTriangles.size() - deletedTriangles.size();
		addedTriangles.removeAllElements();
		deletedTriangles.removeAllElements();
	}

	/**
	 * return a point from the trangulation that is close to pointToDelete
	 * 
	 * @param pointToDelete
	 *            the point that the user wants to delete
	 * @return a point from the trangulation that is close to pointToDelete By
	 *         Eyal Roth & Doron Ganel (2009).
	 */
	public Point findClosePoint(Point pointToDelete) {
		Triangle triangle = find(pointToDelete);
		Point p1 = triangle.p1();
		Point p2 = triangle.p2();
		double d1 = p1.distance(pointToDelete);
		double d2 = p2.distance(pointToDelete);
		if (triangle.isHalfplane()) {
			if (d1 <= d2) {
				return p1;
			} else {
				return p2;
			}
		} else {
			Point p3 = triangle.p3();

			double d3 = p3.distance(pointToDelete);
			if (d1 <= d2 && d1 <= d3) {
				return p1;
			} else if (d2 <= d1 && d2 <= d3) {
				return p2;
			} else {
				return p3;
			}
		}
	}

	// updates the trangulation after the triangles to be deleted and
	// the triangles to be added were found
	// by Doron Ganel & Eyal Roth(2009)
	private void deleteUpdate(Point pointToDelete) {
		for (Triangle addedTriangle1 : addedTriangles) {
			// update between addedd triangles and deleted triangles
			for (Triangle deletedTriangle : deletedTriangles) {
				if (shareSegment(addedTriangle1, deletedTriangle)) {
					updateNeighbor(addedTriangle1, deletedTriangle, pointToDelete);
				}
			}
		}
		for (Triangle addedTriangle1 : addedTriangles) {
			// update between added triangles
			for (Triangle addedTriangle2 : addedTriangles) {
				if ((addedTriangle1 != addedTriangle2) && (shareSegment(addedTriangle1, addedTriangle2))) {
					updateNeighbor(addedTriangle1, addedTriangle2);
				}
			}
		}

		// Update index with changed triangles
		if (gridIndex != null)
			gridIndex.updateIndex(addedTriangles.iterator());

	}

	// checks if the 2 triangles shares a segment
	// by Doron Ganel & Eyal Roth(2009)
	private boolean shareSegment(Triangle t1, Triangle t2) {
		int counter = 0;
		Point t1P1 = t1.p1();
		Point t1P2 = t1.p2();
		Point t1P3 = t1.p3();
		Point t2P1 = t2.p1();
		Point t2P2 = t2.p2();
		Point t2P3 = t2.p3();

		if (t1P1.equals(t2P1)) {
			counter++;
		}
		if (t1P1.equals(t2P2)) {
			counter++;
		}
		if (t1P1.equals(t2P3)) {
			counter++;
		}
		if (t1P2.equals(t2P1)) {
			counter++;
		}
		if (t1P2.equals(t2P2)) {
			counter++;
		}
		if (t1P2.equals(t2P3)) {
			counter++;
		}
		if (t1P3.equals(t2P1)) {
			counter++;
		}
		if (t1P3.equals(t2P2)) {
			counter++;
		}
		if (t1P3.equals(t2P3)) {
			counter++;
		}
		if (counter >= 2)
			return true;
		else
			return false;
	}

	// update the neighbors of the addedTriangle and deletedTriangle
	// we assume the 2 triangles share a segment
	// by Doron Ganel & Eyal Roth(2009)
	private void updateNeighbor(Triangle addedTriangle, Triangle deletedTriangle, Point pointToDelete) {
		Point delA = deletedTriangle.p1();
		Point delB = deletedTriangle.p2();
		Point delC = deletedTriangle.p3();
		Point addA = addedTriangle.p1();
		Point addB = addedTriangle.p2();
		Point addC = addedTriangle.p3();

		// updates the neighbor of the deleted triangle to point to the added
		// triangle
		// setting the neighbor of the added triangle
		if (pointToDelete.equals(delA)) {
			deletedTriangle.getBcnext().switchneighbors(deletedTriangle, addedTriangle);
			// AB-BC || BA-BC
			if ((addA.equals(delB) && addB.equals(delC)) || (addB.equals(delB) && addA.equals(delC))) {
				addedTriangle.setAbnext(deletedTriangle.getBcnext());
			}
			// AC-BC || CA-BC
			else if ((addA.equals(delB) && addC.equals(delC)) || (addC.equals(delB) && addA.equals(delC))) {
				addedTriangle.setCanext(deletedTriangle.getBcnext());
			}
			// BC-BC || CB-BC
			else {
				addedTriangle.setBcnext(deletedTriangle.getBcnext());
			}
		} else if (pointToDelete.equals(delB)) {
			deletedTriangle.getCanext().switchneighbors(deletedTriangle, addedTriangle);
			// AB-AC || BA-AC
			if ((addA.equals(delA) && addB.equals(delC)) || (addB.equals(delA) && addA.equals(delC))) {
				addedTriangle.setAbnext(deletedTriangle.getCanext());
			}
			// AC-AC || CA-AC
			else if ((addA.equals(delA) && addC.equals(delC)) || (addC.equals(delA) && addA.equals(delC))) {
				addedTriangle.setCanext(deletedTriangle.getCanext());
			}
			// BC-AC || CB-AC
			else {
				addedTriangle.setBcnext(deletedTriangle.getCanext());
			}
		}
		// equals c
		else {
			deletedTriangle.getAbnext().switchneighbors(deletedTriangle, addedTriangle);
			// AB-AB || BA-AB
			if ((addA.equals(delA) && addB.equals(delB)) || (addB.equals(delA) && addA.equals(delB))) {
				addedTriangle.setAbnext(deletedTriangle.getAbnext());
			}
			// AC-AB || CA-AB
			else if ((addA.equals(delA) && addC.equals(delB)) || (addC.equals(delA) && addA.equals(delB))) {
				addedTriangle.setCanext(deletedTriangle.getAbnext());
			}
			// BC-AB || CB-AB
			else {
				addedTriangle.setBcnext(deletedTriangle.getAbnext());
			}
		}
	}

	// update the neighbors of the 2 added Triangle s
	// we assume the 2 triangles share a segment
	// by Doron Ganel & Eyal Roth(2009)
	private void updateNeighbor(Triangle addedTriangle1, Triangle addedTriangle2) {
		Point A1 = addedTriangle1.p1();
		Point B1 = addedTriangle1.p2();
		Point C1 = addedTriangle1.p3();
		Point A2 = addedTriangle2.p1();
		Point B2 = addedTriangle2.p2();
		Point C2 = addedTriangle2.p3();

		// A1-A2
		if (A1.equals(A2)) {
			// A1B1-A2B2
			if (B1.equals(B2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// A1B1-A2C2
			else if (B1.equals(C2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
			// A1C1-A2B2
			else if (C1.equals(B2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// A1C1-A2C2
			else {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
		}
		// A1-B2
		else if (A1.equals(B2)) {
			// A1B1-B2A2
			if (B1.equals(A2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// A1B1-B2C2
			else if (B1.equals(C2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);

			}
			// A1C1-B2A2
			else if (C1.equals(A2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// A1C1-B2C2
			else {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
		}
		// A1-C2
		else if (A1.equals(C2)) {
			// A1B1-C2A2
			if (B1.equals(A2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
			// A1B1-C2B2
			if (B1.equals(B2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
			// A1C1-C2A2
			if (C1.equals(A2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
			// A1C1-C2B2
			else {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
		}
		// B1-A2
		else if (B1.equals(A2)) {
			// B1A1-A2B2
			if (A1.equals(B2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// B1A1-A2C2
			else if (A1.equals(C2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
			// B1C1-A2B2
			else if (C1.equals(B2)) {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// B1C1-A2C2
			else {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
		}
		// B1-B2
		else if (B1.equals(B2)) {
			// B1A1-B2A2
			if (A1.equals(A2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// B1A1-B2C2
			else if (A1.equals(C2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
			// B1C1-B2A2
			else if (C1.equals(A2)) {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// B1C1-B2C2
			else {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
		}
		// B1-C2
		else if (B1.equals(C2)) {
			// B1A1-C2A2
			if (A1.equals(A2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
			// B1A1-C2B2
			if (A1.equals(B2)) {
				addedTriangle1.setAbnext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
			// B1C1-C2A2
			if (C1.equals(A2)) {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
			// B1C1-C2B2
			else {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
		}
		// C1-A2
		else if (C1.equals(A2)) {
			// C1A1-A2B2
			if (A1.equals(B2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
			// C1A1-A2C2
			else if (A1.equals(C2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
			// C1B1-A2B2
			else if (B1.equals(B2)) {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// C1B1-A2C2
			else {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
		}
		// C1-B2
		else if (C1.equals(B2)) {
			// C1A1-B2A2
			if (A1.equals(A2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// C1A1-B2C2
			else if (A1.equals(C2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
			// C1B1-B2A2
			else if (B1.equals(A2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setAbnext(addedTriangle1);
			}
			// C1B1-B2C2
			else {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
		}
		// C1-C2
		else if (C1.equals(C2)) {
			// C1A1-C2A2
			if (A1.equals(A2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
			// C1A1-C2B2
			if (A1.equals(B2)) {
				addedTriangle1.setCanext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
			// C1B1-C2A2
			if (B1.equals(A2)) {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setCanext(addedTriangle1);
			}
			// C1B1-C2B2
			else {
				addedTriangle1.setBcnext(addedTriangle2);
				addedTriangle2.setBcnext(addedTriangle1);
			}
		}
	}

	// finds the a point on the triangle that if connect it to "point" (creating
	// a segment)
	// the other two points of the triangle will be to the left and to the right
	// of the segment
	// by Doron Ganel & Eyal Roth(2009)
	private Point findDiagonal(Triangle triangle, Point point) {
		Point p1 = triangle.p1();
		Point p2 = triangle.p2();
		Point p3 = triangle.p3();

		if ((p1.pointLineTest(point, p3) == Point.LEFT) && (p2.pointLineTest(point, p3) == Point.RIGHT))
			return p3;
		if ((p3.pointLineTest(point, p2) == Point.LEFT) && (p1.pointLineTest(point, p2) == Point.RIGHT))
			return p2;
		if ((p2.pointLineTest(point, p1) == Point.LEFT) && (p3.pointLineTest(point, p1) == Point.RIGHT))
			return p1;
		return null;
	}

	/**
	 * Calculates a Voronoi cell for a given neighborhood in this triangulation.
	 * A neighborhood is defined by a triangle and one of its corner points.
	 * 
	 * By Udi Schneider
	 * 
	 * @param triangle
	 *            a triangle in the neighborhood
	 * @param p
	 *            corner point whose surrounding neighbors will be checked
	 * @return set of Points representing the cell polygon
	 */
	public Point[] calcVoronoiCell(Triangle triangle, Point p) {
		// handle any full triangle
		if (!triangle.isHalfplane()) {

			// get all neighbors of given corner point
			Vector<Triangle> neighbors = findTriangleNeighborhood(triangle, p);

			Iterator<Triangle> itn = neighbors.iterator();
			Point[] vertices = new Point[neighbors.size()];

			// for each neighbor, including the given triangle, add
			// center of circumscribed circle to cell polygon
			int index = 0;
			while (itn.hasNext()) {
				Triangle tmp = itn.next();
				vertices[index++] = tmp.circumcircle().center();
			}

			return vertices;
		}

		// handle half plane
		// in this case, the cell is a single line
		// which is the perpendicular bisector of the half plane line
		else {
			// local friendly alias
			Triangle halfplane = triangle;
			// third point of triangle adjacent to this half plane
			// (the point not shared with the half plane)
			Point third = null;
			// triangle adjacent to the half plane
			Triangle neighbor = null;

			// find the neighbor triangle
			if (!halfplane.getAbnext().isHalfplane()) {
				neighbor = halfplane.getAbnext();
			} else if (!halfplane.getBcnext().isHalfplane()) {
				neighbor = halfplane.getBcnext();
			} else if (!halfplane.getBcnext().isHalfplane()) {
				neighbor = halfplane.getCanext();
			}

			// find third point of neighbor triangle
			// (the one which is not shared with current half plane)
			// this is used in determining half plane orientation
			if (!neighbor.p1().equals(halfplane.p1()) && !neighbor.p1().equals(halfplane.p2()))
				third = neighbor.p1();
			if (!neighbor.p2().equals(halfplane.p1()) && !neighbor.p2().equals(halfplane.p2()))
				third = neighbor.p2();
			if (!neighbor.p3().equals(halfplane.p1()) && !neighbor.p3().equals(halfplane.p2()))
				third = neighbor.p3();

			// delta (slope) of half plane edge
			double halfplaneDelta = (halfplane.p1().y() - halfplane.p2().y())
					/ (halfplane.p1().x() - halfplane.p2().x());

			// delta of line perpendicular to current half plane edge
			double perpDelta = (1.0 / halfplaneDelta) * (-1.0);

			// determine orientation: find if the third point of the triangle
			// lies above or below the half plane
			// works by finding the matching y value on the half plane line
			// equation
			// for the same x value as the third point
			double yOrient = halfplaneDelta * (third.x() - halfplane.p1().x()) + halfplane.p1().y();
			boolean above = true;
			if (yOrient > third.y())
				above = false;

			// based on orientation, determine cell line direction
			// (towards right or left side of window)
			double sign = 1.0;
			if ((perpDelta < 0 && !above) || (perpDelta > 0 && above))
				sign = -1.0;

			// the cell line is a line originating from the circumcircle to
			// infinity
			// x = 500.0 is used as a large enough value
			Point circumcircle = neighbor.circumcircle().center();
			double xCellLine = (circumcircle.x() + (500.0 * sign));
			double yCellLine = perpDelta * (xCellLine - circumcircle.x()) + circumcircle.y();

			Point[] result = new Point[2];
			result[0] = circumcircle;
			result[1] = new Point(xCellLine, yCellLine);

			return result;
		}
	}

	/**
	 * returns an iterator object involved in the last update.
	 * 
	 * @return iterator to all triangles involved in the last update of the
	 *         triangulation NOTE: works ONLY if the are triangles (it there is
	 *         only a half plane - returns an empty iterator
	 */
	public Iterator<Triangle> getLastUpdatedTriangles() {
		Vector<Triangle> tmp = new Vector<Triangle>();
		if (this.trianglesSize() > 1) {
			Triangle t = currT;
			allTriangles(t, tmp, this.modCount);
		}
		return tmp.iterator();
	}

	private void allTriangles(Triangle curr, Vector<Triangle> front, int mc) {
		if (curr != null && curr.getMc() == mc && !front.contains(curr)) {
			front.add(curr);
			allTriangles(curr.getAbnext(), front, mc);
			allTriangles(curr.getBcnext(), front, mc);
			allTriangles(curr.getCanext(), front, mc);
		}
	}

	private Triangle insertPointSimple(Point p) {
		nPoints++;
		if (!allCollinear) {
			Triangle t = find(startTriangle, p);
			if (t.isHalfplane())
				startTriangle = extendOutside(t, p);
			else
				startTriangle = extendInside(t, p);
			return startTriangle;
		}

		if (nPoints == 1) {
			firstP = p;
			return null;
		}

		if (nPoints == 2) {
			startTriangulation(firstP, p);
			return null;
		}

		switch (p.pointLineTest(firstP, lastP)) {
		case Point.LEFT:
			startTriangle = extendOutside(firstT.getAbnext(), p);
			allCollinear = false;
			break;
		case Point.RIGHT:
			startTriangle = extendOutside(firstT, p);
			allCollinear = false;
			break;
		case Point.ONSEGMENT:
			insertCollinear(p, Point.ONSEGMENT);
			break;
		case Point.INFRONTOFA:
			insertCollinear(p, Point.INFRONTOFA);
			break;
		case Point.BEHINDB:
			insertCollinear(p, Point.BEHINDB);
			break;
		}
		return null;
	}

	private void insertCollinear(Point p, int res) {
		Triangle t, tp, u;

		switch (res) {
		case Point.INFRONTOFA:
			t = new Triangle(firstP, p);
			tp = new Triangle(p, firstP);
			t.setAbnext(tp);
			tp.setAbnext(t);
			t.setBcnext(tp);
			tp.setCanext(t);
			t.setCanext(firstT);
			firstT.setBcnext(t);
			tp.setBcnext(firstT.getAbnext());
			firstT.getAbnext().setCanext(tp);
			firstT = t;
			firstP = p;
			break;
		case Point.BEHINDB:
			t = new Triangle(p, lastP);
			tp = new Triangle(lastP, p);
			t.setAbnext(tp);
			tp.setAbnext(t);
			t.setBcnext(lastT);
			lastT.setCanext(t);
			t.setCanext(tp);
			tp.setBcnext(t);
			tp.setCanext(lastT.getAbnext());
			lastT.getAbnext().setBcnext(tp);
			lastT = t;
			lastP = p;
			break;
		case Point.ONSEGMENT:
			u = firstT;
			while (p.isGreater(u.p1()))
				u = u.getCanext();
			t = new Triangle(p, u.p2());
			tp = new Triangle(u.p2(), p);
			u.setB(p);
			u.getAbnext().setA(p);
			t.setAbnext(tp);
			tp.setAbnext(t);
			t.setBcnext(u.getBcnext());
			u.getBcnext().setCanext(t);
			t.setCanext(u);
			u.setBcnext(t);
			tp.setCanext(u.getAbnext().getCanext());
			u.getAbnext().getCanext().setBcnext(tp);
			tp.setBcnext(u.getAbnext());
			u.getAbnext().setCanext(tp);
			if (firstT == u) {
				firstT = t;
			}
			break;
		}
	}

	private void startTriangulation(Point p1, Point p2) {
		Point ps, pb;
		if (p1.isLess(p2)) {
			ps = p1;
			pb = p2;
		} else {
			ps = p2;
			pb = p1;
		}
		firstT = new Triangle(pb, ps);
		lastT = firstT;
		Triangle t = new Triangle(ps, pb);
		firstT.setAbnext(t);
		t.setAbnext(firstT);
		firstT.setBcnext(t);
		t.setCanext(firstT);
		firstT.setCanext(t);
		t.setBcnext(firstT);
		firstP = firstT.p2();
		lastP = lastT.p1();
		startTriangleHull = firstT;
	}

	private Triangle extendInside(Triangle t, Point p) {

		Triangle h1, h2;
		h1 = treatDegeneracyInside(t, p);
		if (h1 != null)
			return h1;

		h1 = new Triangle(t.p3(), t.p1(), p);
		h2 = new Triangle(t.p2(), t.p3(), p);
		t.setC(p);
		t.circumcircle();
		h1.setAbnext(t.getCanext());
		h1.setBcnext(t);
		h1.setCanext(h2);
		h2.setAbnext(t.getBcnext());
		h2.setBcnext(h1);
		h2.setCanext(t);
		h1.getAbnext().switchneighbors(t, h1);
		h2.getAbnext().switchneighbors(t, h2);
		t.setBcnext(h2);
		t.setCanext(h1);
		return t;
	}

	private Triangle treatDegeneracyInside(Triangle t, Point p) {

		if (t.getAbnext().isHalfplane() && p.pointLineTest(t.p2(), t.p1()) == Point.ONSEGMENT)
			return extendOutside(t.getAbnext(), p);
		if (t.getBcnext().isHalfplane() && p.pointLineTest(t.p3(), t.p2()) == Point.ONSEGMENT)
			return extendOutside(t.getBcnext(), p);
		if (t.getCanext().isHalfplane() && p.pointLineTest(t.p1(), t.p3()) == Point.ONSEGMENT)
			return extendOutside(t.getCanext(), p);
		return null;
	}

	private Triangle extendOutside(Triangle t, Point p) {

		if (p.pointLineTest(t.p1(), t.p2()) == Point.ONSEGMENT) {
			Triangle dg = new Triangle(t.p1(), t.p2(), p);
			Triangle hp = new Triangle(p, t.p2());
			t.setB(p);
			dg.setAbnext(t.getAbnext());
			dg.getAbnext().switchneighbors(t, dg);
			dg.setBcnext(hp);
			hp.setAbnext(dg);
			dg.setCanext(t);
			t.setAbnext(dg);
			hp.setBcnext(t.getBcnext());
			hp.getBcnext().setCanext(hp);
			hp.setCanext(t);
			t.setBcnext(hp);
			return dg;
		}
		Triangle ccT = extendcounterclock(t, p);
		Triangle cT = extendclock(t, p);
		ccT.setBcnext(cT);
		cT.setCanext(ccT);
		startTriangleHull = cT;
		return cT.getAbnext();
	}

	private Triangle extendcounterclock(Triangle t, Point p) {

		t.setHalfplane(false);
		t.setC(p);
		t.circumcircle();

		Triangle tca = t.getCanext();

		if (p.pointLineTest(tca.p1(), tca.p2()) >= Point.RIGHT) {
			Triangle nT = new Triangle(t.p1(), p);
			nT.setAbnext(t);
			t.setCanext(nT);
			nT.setCanext(tca);
			tca.setBcnext(nT);
			return nT;
		}
		return extendcounterclock(tca, p);
	}

	private Triangle extendclock(Triangle t, Point p) {

		t.setHalfplane(false);
		t.setC(p);
		t.circumcircle();

		Triangle tbc = t.getBcnext();

		if (p.pointLineTest(tbc.p1(), tbc.p2()) >= Point.RIGHT) {
			Triangle nT = new Triangle(p, t.p2());
			nT.setAbnext(t);
			t.setBcnext(nT);
			nT.setBcnext(tbc);
			tbc.setCanext(nT);
			return nT;
		}
		return extendclock(tbc, p);
	}

	private void flip(Triangle t, int mc) {
		Triangle u = t.getAbnext();
		Triangle v;
		t.setMc(mc);
		if (u.isHalfplane() || !u.circumcircle_contains(t.p3()))
			return;

		if (t.p1() == u.p1()) {
			v = new Triangle(u.p2(), t.p2(), t.p3());
			v.setAbnext(u.getBcnext());
			t.setAbnext(u.getAbnext());
		} else if (t.p1() == u.p2()) {
			v = new Triangle(u.p3(), t.p2(), t.p3());
			v.setAbnext(u.getCanext());
			t.setAbnext(u.getBcnext());
		} else if (t.p1() == u.p3()) {
			v = new Triangle(u.p1(), t.p2(), t.p3());
			v.setAbnext(u.getAbnext());
			t.setAbnext(u.getCanext());
		} else {
			throw new RuntimeException("Error in flip.");
		}

		v.setMc(mc);
		v.setBcnext(t.getBcnext());
		v.getAbnext().switchneighbors(u, v);
		v.getBcnext().switchneighbors(t, v);
		t.setBcnext(v);
		v.setCanext(t);
		t.setB(v.p1());
		t.getAbnext().switchneighbors(u, t);
		t.circumcircle();

		currT = v;
		flip(t, mc);
		flip(v, mc);
	}

	/**
	 * compute the number of vertices in the convex hull. <br />
	 * NOTE: has a 'bug-like' behavor: <br />
	 * in cases of colinear - not on a asix parallel rectangle, colinear points
	 * are reported
	 * 
	 * @return the number of vertices in the convex hull.
	 */
	public int getConvexHullSize() {
		int ans = 0;
		Iterator<Point> it = this.getConvexHullVerticesIterator();
		while (it.hasNext()) {
			ans++;
			it.next();
		}
		return ans;
	}

	/**
	 * finds the triangle the query point falls in, note if out-side of this
	 * triangulation a half plane triangle will be returned (see contains), the
	 * search has expected time of O(n^0.5), and it starts form a fixed triangle
	 * (this.startTriangle),
	 * 
	 * @param p
	 *            query point
	 * @return the triangle that point p is in.
	 */
	public Triangle find(Point p) {

		// If triangulation has a spatial index try to use it as the starting
		// triangle
		Triangle searchTriangle = startTriangle;
		if (gridIndex != null) {
			Triangle indexTriangle = gridIndex.findCellTriangleOf(p);
			if (indexTriangle != null)
				searchTriangle = indexTriangle;
		}

		// Search for the point's triangle starting from searchTriangle
		return find(searchTriangle, p);
	}

	/**
	 * finds the triangle the query point falls in, note if out-side of this
	 * triangulation a half plane triangle will be returned (see contains). the
	 * search starts from the the start triangle
	 * 
	 * @param p
	 *            query point
	 * @param start
	 *            the triangle the search starts at.
	 * @return the triangle that point p is in..
	 */
	public Triangle find(Point p, Triangle start) {
		if (start == null)
			start = this.startTriangle;
		Triangle T = find(start, p);
		return T;
	}

	private static Triangle find(Triangle curr, Point p) {
		if (p == null)
			return null;
		Triangle next_t;
		if (curr.isHalfplane()) {
			next_t = findnext2(p, curr);
			if (next_t == null || next_t.isHalfplane())
				return curr;
			curr = next_t;
		}
		while (true) {
			next_t = findnext1(p, curr);
			if (next_t == null)
				return curr;
			if (next_t.isHalfplane())
				return next_t;
			curr = next_t;
		}
	}

	/*
	 * assumes v is NOT an halfplane! returns the next triangle for find.
	 */
	private static Triangle findnext1(Point p, Triangle v) {
		if (p.pointLineTest(v.p1(), v.p2()) == Point.RIGHT && !v.getAbnext().isHalfplane())
			return v.getAbnext();
		if (p.pointLineTest(v.p2(), v.p3()) == Point.RIGHT && !v.getBcnext().isHalfplane())
			return v.getBcnext();
		if (p.pointLineTest(v.p3(), v.p1()) == Point.RIGHT && !v.getCanext().isHalfplane())
			return v.getCanext();
		if (p.pointLineTest(v.p1(), v.p2()) == Point.RIGHT)
			return v.getAbnext();
		if (p.pointLineTest(v.p2(), v.p3()) == Point.RIGHT)
			return v.getBcnext();
		if (p.pointLineTest(v.p3(), v.p1()) == Point.RIGHT)
			return v.getCanext();
		return null;
	}

	/** assumes v is an halfplane! - returns another (none halfplane) triangle */
	private static Triangle findnext2(Point p, Triangle v) {
		if (v.getAbnext() != null && !v.getAbnext().isHalfplane())
			return v.getAbnext();
		if (v.getBcnext() != null && !v.getBcnext().isHalfplane())
			return v.getBcnext();
		if (v.getCanext() != null && !v.getCanext().isHalfplane())
			return v.getCanext();
		return null;
	}

	/*
	 * Receives a point and returns all the points of the triangles that shares
	 * point as a corner (Connected vertices to this point).
	 * 
	 * Set saveTriangles to true if you wish to save the triangles that were
	 * found.
	 * 
	 * By Doron Ganel & Eyal Roth
	 */
	private Vector<Point> findConnectedVertices(Point point, boolean saveTriangles) {
		Set<Point> pointsSet = new HashSet<Point>();
		Vector<Point> pointsVec = new Vector<Point>();
		Vector<Triangle> triangles = null;
		// Getting one of the neigh
		Triangle triangle = find(point);

		// Validating find result.
		if (!triangle.isCorner(point)) {
			System.err
					.println("findConnectedVertices: Could not find connected vertices since the first found triangle doesn't"
							+ " share the given point.");
			return null;
		}

		triangles = findTriangleNeighborhood(triangle, point);
		if (triangles == null) {
			System.err.println("Error: can't delete a point on the perimeter");
			return null;
		}
		if (saveTriangles) {
			deletedTriangles = triangles;
		}

		for (Triangle tmpTriangle : triangles) {
			Point point1 = tmpTriangle.p1();
			Point point2 = tmpTriangle.p2();
			Point point3 = tmpTriangle.p3();

			if (point1.equals(point) && !pointsSet.contains(point2)) {
				pointsSet.add(point2);
				pointsVec.add(point2);
			}

			if (point2.equals(point) && !pointsSet.contains(point3)) {
				pointsSet.add(point3);
				pointsVec.add(point3);
			}

			if (point3.equals(point) && !pointsSet.contains(point1)) {
				pointsSet.add(point1);
				pointsVec.add(point1);
			}
		}

		return pointsVec;
	}

	// Walks on a consistent side of triangles until a cycle is achieved.
	// By Doron Ganel & Eyal Roth
	// changed to public by Udi
	public Vector<Triangle> findTriangleNeighborhood(Triangle firstTriangle, Point point) {
		Vector<Triangle> triangles = new Vector<Triangle>(30);
		triangles.add(firstTriangle);

		Triangle prevTriangle = null;
		Triangle currentTriangle = firstTriangle;
		Triangle nextTriangle = currentTriangle.nextNeighbor(point, prevTriangle);

		while (nextTriangle != firstTriangle) {
			// the point is on the perimeter
			if (nextTriangle.isHalfplane()) {
				return null;
			}
			triangles.add(nextTriangle);
			prevTriangle = currentTriangle;
			currentTriangle = nextTriangle;
			nextTriangle = currentTriangle.nextNeighbor(point, prevTriangle);
		}

		return triangles;
	}

	/*
	 * find triangle to be added to the triangulation
	 * 
	 * By: Doron Ganel & Eyal Roth
	 */
	private Triangle findTriangle(Vector<Point> pointsVec, Point p) {
		Point[] arrayPoints = new Point[pointsVec.size()];
		pointsVec.toArray(arrayPoints);

		int size = arrayPoints.length;
		if (size < 3) {
			return null;
		}
		// if we left with 3 points we return the triangle
		else if (size == 3) {
			return new Triangle(arrayPoints[0], arrayPoints[1], arrayPoints[2]);
		} else {
			for (int i = 0; i <= size - 1; i++) {
				Point p1 = arrayPoints[i];
				int j = i + 1;
				int k = i + 2;
				if (j >= size) {
					j = 0;
					k = 1;
				}
				// check IndexOutOfBound
				else if (k >= size)
					k = 0;
				Point p2 = arrayPoints[j];
				Point p3 = arrayPoints[k];
				// check if the triangle is not re-entrant and not encloses p
				Triangle t = new Triangle(p1, p2, p3);
				if ((calcDet(p1, p2, p3) >= 0) && !t.contains(p)) {
					if (!t.fallInsideCircumcircle(arrayPoints))
						return t;
				}
				// if there are only 4 points use contains that refers to point
				// on boundary as outside
				if (size == 4 && (calcDet(p1, p2, p3) >= 0) && !t.contains_BoundaryIsOutside(p)) {
					if (!t.fallInsideCircumcircle(arrayPoints))
						return t;
				}
			}
		}
		return null;
	}

	// TODO: Move this to triangle.
	// checks if the triangle is not re-entrant
	private double calcDet(Point A, Point B, Point P) {
		return (A.x() * (B.y() - P.y())) - (A.y() * (B.x() - P.x())) + (B.x() * P.y() - B.y() * P.x());
	}

	/**
	 * 
	 * @param p
	 *            query point
	 * @return true iff p is within this triangulation (in its 2D convex hull).
	 */

	public boolean contains(Point p) {
		Triangle tt = find(p);
		return !tt.isHalfplane();
	}

	/**
	 * 
	 * @param x
	 *            - X cordination of the query point
	 * @param y
	 *            - Y cordination of the query point
	 * @return true iff (x,y) falls inside this triangulation (in its 2D convex
	 *         hull).
	 */
	public boolean contains(double x, double y) {
		return contains(new Point(x, y));
	}

	/**
	 * 
	 * @param q
	 *            Query point
	 * @return the q point with updated Z value (z value is as given the
	 *         triangulation).
	 */
	public Point z(Point q) {
		Triangle t = find(q);
		return t.z(q);
	}

	/**
	 * 
	 * @param x
	 *            - X cordination of the query point
	 * @param y
	 *            - Y cordination of the query point
	 * @return the q point with updated Z value (z value is as given the
	 *         triangulation).
	 */
	public double z(double x, double y) {
		Point q = new Point(x, y);
		Triangle t = find(q);
		return t.z_value(q);
	}

	private void updateBoundingBox(Point p) {
		double x = p.x(), y = p.y(), z = p.z();
		if (bbMin == null) {
			bbMin = new Point(p);
			bbMax = new Point(p);
		} else {
			if (x < bbMin.x())
				bbMin.setX(x);
			else if (x > bbMax.x())
				bbMax.setX(x);
			if (y < bbMin.y())
				bbMin.setY(y);
			else if (y > bbMax.y())
				bbMax.setY(y);
			if (z < bbMin.z())
				bbMin.setZ(z);
			else if (z > bbMax.z())
				bbMax.setZ(z);
		}
	}

	/**
	 * @return The bounding rectange between the minimum and maximum coordinates
	 */
	public BoundingBox getBoundingBox() {
		return new BoundingBox(bbMin, bbMax);
	}

	/**
	 * return the min point of the bounding box of this triangulation
	 * {{x0,y0,z0}}
	 */
	public Point minBoundingBox() {
		return bbMin;
	}

	/**
	 * return the max point of the bounding box of this triangulation
	 * {{x1,y1,z1}}
	 */
	public Point maxBoundingBox() {
		return bbMax;
	}

	/**
	 * computes the current set (vector) of all triangles and return an iterator
	 * to them.
	 * 
	 * @return an iterator to the current set of all triangles.
	 */
	public Iterator<Triangle> trianglesIterator() {
		if (this.size() <= 2)
			triangles = new Vector<Triangle>();
		initTriangles();
		return triangles.iterator();
	}

	/**
	 * returns an iterator to the set of all the points on the XY-convex hull
	 * 
	 * @return iterator to the set of all the points on the XY-convex hull.
	 */
	public Iterator<Point> getConvexHullVerticesIterator() {
		Vector<Point> ans = new Vector<Point>();
		Triangle curr = this.startTriangleHull;
		boolean cont = true;
		double x0 = bbMin.x(), x1 = bbMax.x();
		double y0 = bbMin.y(), y1 = bbMax.y();
		boolean sx, sy;
		while (cont) {
			sx = curr.p1().x() == x0 || curr.p1().x() == x1;
			sy = curr.p1().y() == y0 || curr.p1().y() == y1;
			if ((sx & sy) | (!sx & !sy)) {
				ans.add(curr.p1());
			}
			if (curr.getBcnext() != null && curr.getBcnext().isHalfplane())
				curr = curr.getBcnext();
			if (curr == this.startTriangleHull)
				cont = false;
		}
		return ans.iterator();
	}

	/**
	 * returns an iterator to the set of points compusing this triangulation.
	 * 
	 * @return iterator to the set of points compusing this triangulation.
	 */
	public Iterator<Point> verticesIterator() {
		return this.vertices.iterator();
	}

	private void initTriangles() {
		if (modCount == modCount2)
			return;
		if (this.size() > 2) {
			modCount2 = modCount;
			Vector<Triangle> front = new Vector<Triangle>();
			triangles = new Vector<Triangle>();
			front.add(this.startTriangle);
			while (front.size() > 0) {
				Triangle t = front.remove(0);
				if (t.isMark() == false) {
					t.setMark(true);
					triangles.add(t);
					if (t.getAbnext() != null && !t.getAbnext().isMark()) {
						front.add(t.getAbnext());
					}
					if (t.getBcnext() != null && !t.getBcnext().isMark()) {
						front.add(t.getBcnext());
					}
					if (t.getCanext() != null && !t.getCanext().isMark()) {
						front.add(t.getCanext());
					}
				}
			}
			// _triNum = _triangles.size();
			for (int i = 0; i < triangles.size(); i++) {
				triangles.elementAt(i).setMark(false);
			}
		}
	}

	/**
	 * Index the triangulation using a grid index
	 * 
	 * @param xCellCount
	 *            number of grid cells in a row
	 * @param yCellCount
	 *            number of grid cells in a column
	 */
	public void indexData(int xCellCount, int yCellCount) {
		gridIndex = new GridIndex(this, xCellCount, yCellCount);
	}

	/**
	 * Remove any existing spatial indexing
	 */
	public void removeIndex() {
		gridIndex = null;
	}
}
