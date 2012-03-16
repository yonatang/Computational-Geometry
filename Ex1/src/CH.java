import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CH {
	public static String NEW_LINE = System.getProperty("line.separator");

	public CH(String[] args) {
		if (args.length < 2) {
			throw new IllegalArgumentException(
					"Bad arguments.\nCH <input> <output> [-log] [-alternative]");
		}
		this.inFile = new File(args[0]);
		this.outFile = new File(args[1]);
		for (int i = 2; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-log"))
				logger = true;
			if (args[i].equalsIgnoreCase("-alternative"))
				alternative = true;
		}

		if (!inFile.exists() || !inFile.isFile()) {
			throw new IllegalArgumentException("File " + inFile + " not exist");
		}
		if (outFile.exists()) {
			throw new IllegalArgumentException("File " + outFile
					+ " already exist");
		}

	}

	private boolean logger = false;
	private boolean alternative = false;
	private File inFile;
	private File outFile;
	private Set<Point> points;
	private List<Point> result;

	private void readFile() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(inFile));
			// Can throw value, allocating the collection dynamically. Assuming
			// input is valid, offcourse.
			reader.readLine();
			String pair = null;
			points = new HashSet<CH.Point>();
			while ((pair = reader.readLine()) != null) {
				String[] parts = pair.split(" ");
				if (parts.length != 2)
					throw new RuntimeException("File " + inFile
							+ " is of wrong format at line " + pair);
				try {
					double x = Double.parseDouble(parts[0]);
					double y = Double.parseDouble(parts[1]);
					Point p = new Point(x, y);
					points.add(p);
				} catch (NumberFormatException e) {
					throw new RuntimeException("File " + inFile
							+ " is of wrong format at line " + pair
							+ " - bad number format");
				}
			}
			if (points.size() == 0)
				throw new RuntimeException("No data in file " + inFile);
			System.out.println("Read " + points.size() + " points.");
		} catch (IOException e) {
			throw new RuntimeException("Problem reading file " + inFile, e);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}

	}

	public void scan() {
		result = new ArrayList<CH.Point>();
		Point highestPoint = null;
		for (Point p : points) {
			if (highestPoint == null) {
				highestPoint = p;
				continue;
			}
			if (highestPoint.getY() < p.getY())
				highestPoint = p;
		}
		log("Starting from point " + highestPoint);

		result.add(highestPoint);
		// points.remove(highestPoint);
		Point currentPoint = highestPoint;
		double backAngle = 0;
		while (true) {

			Point nextPoint = null;
			double nextAngle = 360;
			for (Point p : points) {
				if (currentPoint.equals(p)) {
					continue;
				}
				double canidateAngle = currentPoint.angleTo(p);
				log(" Considering point " + p + " with angle " + canidateAngle);
				if (backAngle < canidateAngle && canidateAngle < nextAngle) {
					nextAngle = canidateAngle;
					nextPoint = p;
				}
			}

			if (nextPoint == null) {
				log(" --Couldn't find next point. Retrying without backangle limitation");
				for (Point p : points) {
					if (currentPoint.equals(p)) {
						continue;
					}
					double canidateAngle = currentPoint.angleTo(p);
					log(" Considering point " + p + " with angle "
							+ canidateAngle);
					if (canidateAngle < nextAngle) {
						nextAngle = canidateAngle;
						nextPoint = p;
					}
				}
			}
			if (nextPoint.equals(highestPoint)) {
				log("Reached starting point at " + highestPoint);
				System.out.println("Done.");
				// We're done!
				break;
			}
			result.add(nextPoint);
			backAngle = nextPoint.angleTo(currentPoint);
			if (backAngle > 180)
				backAngle = 0;
			log("Adding point " + nextPoint + " with angle " + nextAngle
					+ ", back angle is " + backAngle);
			currentPoint = nextPoint;

		}

	}

	public void writeFile() {
		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new FileWriter(outFile));
			br.write(String.valueOf(result.size()));
			System.out.println("Min Convex Hull contains " + result.size()
					+ " points");
			br.write(NEW_LINE);
			for (Point p : result) {
				br.write(p.toString());
				br.write(NEW_LINE);
			}

		} catch (IOException e) {
			throw new RuntimeException("Problem writing into file " + outFile,
					e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private void log(String s) {
		if (logger) {
			System.out.println(s);
		}
	}

	public void run() {
		System.out.println("Convex Hull calculation");
		System.out.println("Input file: " + inFile);
		System.out.println("Output file: " + outFile);
		if (logger)
			System.out.println("Verbos logging on");
		if (alternative)
			System.out.println("Using non algorthm taken from the net");
		readFile();
		long now = System.currentTimeMillis();
		scan();
		long duration = System.currentTimeMillis() - now;
		writeFile();
		System.out.println("Took me " + duration + "ms to calculcate it.");

	}

	public static void main(String[] args) {

		try {
			CH ch = new CH(args);
			ch.run();
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

	}

	static class Point {
		private double x;
		private double y;

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double angleTo(Point p) {
			// Me no like radians.
			double degree = Math.toDegrees(Math.atan2(p.getX() - getX(),
					p.getY() - getY()));
			if (degree < 0) {
				degree = 360 + degree;
			}
			return degree;
		}

		@Override
		public boolean equals(Object obj) {
			// Eliminate point duplication issues
			if (obj instanceof Point) {
				return (Double.compare(((Point) obj).getX(), x) == 0)
						&& (Double.compare(((Point) obj).getY(), y) == 0);
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return x + " " + y;
		}
	}

}
