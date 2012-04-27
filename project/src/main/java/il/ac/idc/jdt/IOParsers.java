package il.ac.idc.jdt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class IOParsers {

	public static Point[] read(File file) throws IOException {
		return read(new FileInputStream(file));
	}

	public static Point[] read(String file) throws IOException {
		return read(new FileInputStream(file));
	}

	public static Point[] read(InputStream is) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			String s;
			while ((s = br.readLine()).startsWith("/")) {
			}
			if (s.equals("begin")) {
				return readSmf(br);
			} else if (Utils.isNumeric(s)) {
				return readTsin(br, s);
			} else {
				throw new UnsupportedFormatException("File format not recognized");
			}
		} finally {
			Utils.closeQuietly(br);
		}
	}

	private static Point[] readSmf(BufferedReader is) throws IOException {
		String s;
		while (!(s = is.readLine()).startsWith("v")) {

		}

		double dx = 1, dy = 1, dz = 1, minX = 0, minY = 0, minZ = 0;

		Vector<Point> vec = new Vector<Point>();
		Point[] ans = null; //
		while (s != null && s.charAt(0) == 'v') {
			StringTokenizer st = new StringTokenizer(s);
			st.nextToken();
			double d1 = new Double(st.nextToken()).doubleValue() * dx + minX;
			double d2 = new Double(st.nextToken()).doubleValue() * dy + minY;
			double d3 = new Double(st.nextToken()).doubleValue() * dz + minZ;
			vec.add(new Point((int) d1, (int) d2, d3));
			s = is.readLine();
		}
		ans = new Point[vec.size()];
		for (int i = 0; i < vec.size(); i++)
			ans[i] = (Point) vec.elementAt(i);
		return ans;
	}

	private static Point[] readTsin(BufferedReader is, String firstLine) throws IOException {

		StringTokenizer st;
		int numOfVer = new Integer(firstLine).intValue();

		Point[] ans = new Point[numOfVer];

		// reading the file verteces - insert them to the triangulation
		for (int i = 0; i < numOfVer; i++) {
			st = new StringTokenizer(is.readLine());
			double d1 = new Double(st.nextToken()).doubleValue();
			double d2 = new Double(st.nextToken()).doubleValue();
			double d3 = new Double(st.nextToken()).doubleValue();
			ans[i] = new Point((int) d1, (int) d2, d3);
		}
		return ans;
	}

	public static void exportSmf(DelaunayTriangulation dto, OutputStream os) {
		exportSmf(dto, new OutputStreamWriter(os));
	}

	public static void exportSmf(DelaunayTriangulation dto, Writer writer) {
		int len = dto.size();
		Point[] ans = new Point[len];
		Iterator<Point> it = dto.verticesIterator();
		Comparator<Point> comp = Point.getComparator();
		for (int i = 0; i < len; i++) {
			ans[i] = it.next();
		}
		Arrays.sort(ans, comp);

		PrintWriter os = new PrintWriter(writer);
		// prints the tsin file header:
		try {
			os.println("begin");

			for (int i = 0; i < len; i++) {
				os.println("v " + ans[i].toFile());
			}
			int i1 = -1, i2 = -1, i3 = -1;
			for (Iterator<Triangle> dt = dto.trianglesIterator(); dt.hasNext();) {
				Triangle curr = dt.next();
				if (!curr.halfplane) {
					i1 = Arrays.binarySearch(ans, curr.getA(), comp);
					i2 = Arrays.binarySearch(ans, curr.getB(), comp);
					i3 = Arrays.binarySearch(ans, curr.getC(), comp);
					if (i1 < 0 || i2 < 0 || i3 < 0)
						throw new RuntimeException("wrong triangulation inner bug - cant write as an SMF file!");
					os.println("f " + (i1 + 1) + " " + (i2 + 1) + " " + (i3 + 1));
				}
			}
			os.println("end");
		} finally {
			Utils.closeQuietly(os);
		}

	}

	public static void exportSmf(DelaunayTriangulation dto, File smfFile) throws IOException {
		exportSmf(dto, new FileWriter(smfFile));
	}

	public static void exportSmf(DelaunayTriangulation dto, String smfFile) throws IOException {
		exportSmf(dto, new FileWriter(smfFile));
	}

	public static void exportTsin(DelaunayTriangulation dto, File tsinFile) throws IOException {
		exportTsin(dto, new FileWriter(tsinFile));
	}

	public static void exportTsin(DelaunayTriangulation dto, String tsinFile) throws IOException {
		exportTsin(dto, new FileWriter(tsinFile));
	}

	public static void exportTsin(DelaunayTriangulation dto, OutputStream os) {
		exportTsin(dto, new OutputStreamWriter(os));
	}

	public static void exportTsin(DelaunayTriangulation dto, Writer writer) {
		PrintWriter os = new PrintWriter(writer);
		try {
			// prints the tsin file header:
			int len = dto.size();
			os.println(len);
			Iterator<Point> it = dto.verticesIterator();
			while (it.hasNext()) {
				os.println(it.next().toFile());
			}
		} finally {
			Utils.closeQuietly(os);
		}
	}

	public static void exportCHTsin(DelaunayTriangulation dto, String tsinFile) throws IOException {
		FileWriter fw = new FileWriter(tsinFile);
		PrintWriter os = new PrintWriter(fw);
		// prints the tsin file header:
		os.println(dto.getConvexHullSize());
		Iterator<Point> it = dto.getConvexHullVerticesIterator();
		while (it.hasNext()) {
			os.println(it.next().toFileXY());
		}
		os.close();
		fw.close();

	}

}
