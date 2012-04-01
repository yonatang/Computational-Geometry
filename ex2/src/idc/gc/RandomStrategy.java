package idc.gc;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomStrategy implements Strategy {

	private Random rnd = new Random();
	private Benchmarker b = new Benchmarker();
	private final int TRIES = 10000;

	@Override
	public Set<Circle> execute(Set<Point> points, int n) {
		int best = Integer.MAX_VALUE;
		int tryNum = TRIES;
		Set<Circle> bestResults = null;
		while (tryNum-- > 0) {
			Set<Circle> results = new HashSet<Circle>();
			for (int i = 0; i < n; i++) {
				double r = rnd.nextDouble() * 50;
				double x = r + rnd.nextDouble() * (100 - r*2);
				double y = r + rnd.nextDouble() * (100 - r*2);
				results.add(new Circle(new Point(x, y), r));
			}
			int score = b.score(points, results);
			if (score < best) {
				best = score;
				bestResults = results;
			}
		}

		return bestResults;
	}

}
