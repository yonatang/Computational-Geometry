package idc.gc;

import idc.gc.dt.Circle;
import idc.gc.dt.Point;
import idc.gc.strategy.BlowingStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

public class BlowTest {

	@Test
	public void run() throws FileNotFoundException, IOException {
		File f = new File("samples");
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("bm_") && (name.endsWith(".txt"));
			}
		};
		File[] list = f.listFiles(filter);
		BlowingStrategy str = new BlowingStrategy();
		Benchmarker b = new Benchmarker();
		for (File file : list) {
			Set<Point> points = Main.readFile(file);
			for (int n : new int[] { 2, 4, 8, 16, 32, 63 }) {
				long now = System.currentTimeMillis();
				Set<Circle> result = str.execute(points, n);
				long duration = System.currentTimeMillis() - now;
				int score = b.score(points, result);
				System.out.println("File: " + file + ", points: " + points.size() + ", n: " + n + ", score: " + score
						+ ", duration: " + duration);
			}

		}
	}
}
