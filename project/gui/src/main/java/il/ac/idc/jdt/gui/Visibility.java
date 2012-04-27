package il.ac.idc.jdt.gui;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;

import java.util.Vector;

public class Visibility {
	Vector<Point> _section;
	Vector<Triangle> _tr;
	Point _p1, _p2, last = null;
	DelaunayTriangulation _dt;

	Visibility(DelaunayTriangulation dt) {
		_dt = dt;
		_section = new Vector<Point>();
	}

	/**
	 * this method checke if there is a Line Of Site between p1 and p2, note the
	 * z value of the points is the Hieght ABOVE ground!!
	 * 
	 * @param p1
	 *            "guard",
	 * @param p2
	 *            "client"
	 * @return true iff both points are valid (not null && inside the
	 *         triangulation)
	 */
	public boolean los(Point p1, Point p2) {
		boolean ans = true;
		if (_dt == null)
			throw new RuntimeException("** ERR: null pointer triangulation (LOS) **");
		if (p1 == null || p2 == null)
			throw new RuntimeException("** ERR: null pointer points (LOS) **");
		if (!_dt.contains(p1.getX(), p1.getY()))
			throw new RuntimeException("** ERR: p1:" + p1 + " is NOT contained in the triangulation **");
		if (!_dt.contains(p2.getX(), p2.getY()))
			throw new RuntimeException("** ERR: p2:" + p2 + " is NOT contained in the triangulation **");

		this.computeSection(p1, p2);
		ans = this.isVisible(p1.getZ(), p2.getZ());
		this.computeSection(p2, p1);
		ans = ans && this.isVisible(p2.getZ(), p1.getZ());
		printSection(p1, p2);
		return ans;
	}

	void printSection(Point p1, Point p2) {
		// System.out.println("G: "+p1);
		// System.out.println("C: "+p2);
		double dx = p1.distance(p2);
		double z1 = p1.getZ() + this._dt.z(p1).getZ();
		double z2 = p2.getZ() + this._dt.z(p2).getZ();
		double dz = z2 - z1;
		double gap = 100000;
		for (int i = 0; i < _section.size(); i++) {
			Point curr = _section.elementAt(i);
			double d = curr.distance(p1);
			double z = z1 + dz * (d / dx);
			double dzz = z - curr.getZ();
			if (gap > dzz)
				gap = dzz;
			// System.out.println(i+")  dist1:"+curr.distance(p1)+"   dist2:"+curr.distance(p2)+"  c.z:"+curr.getZ()+"  Z:"+z+"  block:"+(curr.getZ()>z));
			System.out.println(i + ") dist1:" + curr.distance(p1) + "  dist2:" + curr.distance(p2) + "  Curr: " + curr + "  DZ:"
					+ (z - curr.getZ()) + "  block:" + (curr.getZ() > z));
		}
		System.out.println("Triangle size: " + _tr.size() + "  min Gap:" + gap);

	}

	void computeSection(Point p1, Point p2) {

		Triangle t1 = _dt.find(p1);
		Triangle t2 = _dt.find(p2);
		_p1 = t1.getZ(p1);
		_p2 = t2.getZ(p2);
		if (_tr == null)
			_tr = new Vector<Triangle>();
		else
			_tr.clear();
		if (_section == null)
			_section = new Vector<Point>();
		else
			_section.clear();
		Triangle curr_t = t1;
		while (curr_t != t2 && curr_t != null) {
			_tr.add(curr_t);
			cut(curr_t);
			curr_t = next_t(p1, p2, curr_t, _tr);
		}
		_tr.add(t2);
	}

	Triangle next_t(Point pp1, Point pp2, Triangle curr, Vector<Triangle> tr) {
		Triangle ans = null, t12, t23, t31;
		t12 = curr.getAbTriangle();
		t23 = curr.getBcTriangle();
		t31 = curr.getCaTriangle();
		if (t12 != null && cut(pp1, pp2, t12) && !tr.contains(t12))
			ans = t12;
		else if (t23 != null && cut(pp1, pp2, t23) && !tr.contains(t23))
			ans = t23;
		else if (t31 != null && cut(pp1, pp2, t31) && !tr.contains(t31))
			ans = t31;
		return ans;
	}

	/** return true iff the segment _p1,_p2 is cutting t */
	boolean cut(Point pp1, Point pp2, Triangle t) {
		boolean ans = false;
		if (t.isHalfplane())
			return false;
		Point p1 = t.getA(), p2 = t.getB(), p3 = t.getC();
		int f1 = p1.pointLineTest(pp1, pp2);
		int f2 = p2.pointLineTest(pp1, pp2);
		int f3 = p3.pointLineTest(pp1, pp2);

		if ((f1 == Point.LEFT || f1 == Point.RIGHT) && (f1 == f2 && f1 == f3))
			return false;

		if (f1 != f2 && pp1.pointLineTest(p1, p2) != pp2.pointLineTest(p1, p2))
			return true;
		if (f2 != f3 && pp1.pointLineTest(p2, p3) != pp2.pointLineTest(p2, p3))
			return true;
		if (f3 != f1 && pp1.pointLineTest(p3, p1) != pp2.pointLineTest(p3, p1))
			return true;

		return ans;
	}

	/**
	 * add the intersections of triangle t with the section to the list of
	 * intersection (set)
	 */
	void cut(Triangle t) {
		if (t.isHalfplane())
			return;
		Point p1 = t.getA(), p2 = t.getB(), p3 = t.getC();
		int f1 = p1.pointLineTest(_p1, _p2);
		int f2 = p2.pointLineTest(_p1, _p2);
		int f3 = p3.pointLineTest(_p1, _p2);

		if ((f1 == Point.LEFT || f1 == Point.RIGHT) && (f1 == f2 && f1 == f3))
			return;
		if (f1 != f2 && _p1.pointLineTest(p1, p2) != _p2.pointLineTest(p1, p2))
			add(intersection(p1, p2));
		if (f2 != f3 && _p1.pointLineTest(p2, p3) != _p2.pointLineTest(p2, p3))
			add(intersection(p2, p3));
		if (f3 != f1 && _p1.pointLineTest(p3, p1) != _p2.pointLineTest(p3, p1))
			add(intersection(p3, p1));
	}

	void add(Point p) {
		int len = _section.size();
		if (p != null && (len == 0 || _p1.distance(p) > _p1.distance(_section.elementAt(len - 1))))
			_section.add(p);
	}

	Point intersection(Point q1, Point q2) {
		Point ans = null;
		double x1 = _p1.getX(), x2 = _p2.getX();
		double xx1 = q1.getX(), xx2 = q2.getX();
		double dx = x2 - x1, dxx = xx2 - xx1;
		if (dx == 0 && dxx == 0) {
			ans = q1;
			if (q2.distance(_p1) < q1.distance(_p1))
				ans = q2;
		} else if (dxx == 0) {
			ans = new Point(q1.getX(), f(_p1, _p2, q1.getX()), fz(_p1, _p2, q1.getX()));
		} else if (dx == 0) {
			ans = new Point(_p1.getX(), f(q1, q2, _p1.getX()), fz(q1, q1, _p1.getX()));
		} else {
			double x = (k(_p1, _p2) - k(q1, q2)) / (m(q1, q2) - m(_p1, _p2));
			double y = m(_p1, _p2) * x + k(_p1, _p2);
			double z = mz(q1, q2) * x + kz(q1, q2);
			ans = new Point(x, y, z);
		}
		return ans;
	}

	/** assume z = m*x + k (as a 2D XZ!! linear function) */
	private double mz(Point p1, Point p2) {
		double ans = 0;
		double dx = p2.getX() - p1.getX(), dz = p2.getZ() - p1.getZ();
		if (dx != 0)
			ans = dz / dx;
		return ans;
	}

	private double kz(Point p1, Point p2) {
		double k = p1.getZ() - mz(p1, p2) * p1.getX();
		return k;
	}

	private double f(Point p1, Point p2, double x) {
		return m(p1, p2) * x + k(p1, p2);
	}

	private double fz(Point p1, Point p2, double x) {
		return mz(p1, p2) * x + kz(p1, p2);
	}

	/** assume y = m*x + k (as a 2D XY !! linear function) */
	private double m(Point p1, Point p2) {
		double ans = 0;
		double dx = p2.getX() - p1.getX(), dy = p2.getY() - p1.getY();
		if (dx != 0)
			ans = dy / dx;
		return ans;
	}

	private double k(Point p1, Point p2) {
		double k = p1.getY() - m(p1, p2) * p1.getX();
		return k;
	}

	/**
	 * checks if a tower of height h1 at _p1 can see the tip of a tower of size
	 * h2 at _p2
	 */
	boolean isVisible(double h1, double h2) {
		boolean ans = false;
		if (_section != null) {
			ans = true;
			double z1 = _p1.getZ() + h1, z2 = _p2.getZ() + h2, dz = z2 - z1, dist = _p1.distance(_p2);
			int len = _section.size();
			for (int i = 0; i < len && ans; i++) {
				Point curr_p = _section.elementAt(i);
				double d = _p1.distance(curr_p);
				// System.out.println(i+")  curr Z: "+(int)curr_p.getZ()+"    sec z: "+(int)(z1+dz*(d/dist)));
				if (curr_p.getZ() > z1 + dz * (d / dist))
					ans = false;
			}
		}
		return ans;
	}
}
