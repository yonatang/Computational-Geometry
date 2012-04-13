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
//		File[] list = f.listFiles(filter);
		File[] list=new File[]{new File("samples/bm_grid1000_.txt")};
		BlowingStrategy str = new BlowingStrategy();
		Benchmarker b = new Benchmarker();

		for (int step : new int[] {  10 }) {
			System.out.println("Step " + step);
			int totalScore=0;
			int totalDuration=0;
			str.setStepSize(step);
			for (File file : list) {
				Set<Point> points = Main.readFile(file);
//				for (int n : new int[] { 2, 4,5,7 ,8, 12,16, 19,20,21,23,32,55,56,57,58, 63 }) {
				for (int n =2;n<64;n++){
					long now = System.currentTimeMillis();
					Set<Circle> result = str.execute(points, n);
					long duration = System.currentTimeMillis() - now;
					int score = b.score(points, result);
					totalDuration+=duration;
					totalScore+=score;
					System.out.println("File: " + file + ", points: " + points.size() + ", n: " + n + ", score: " + score + ", duration: "
							+ duration);
				}
			}
			System.out.println("Total score: "+totalScore);
			System.out.println("Total duration: "+totalDuration);
			System.out.println();
		}
	}
}
