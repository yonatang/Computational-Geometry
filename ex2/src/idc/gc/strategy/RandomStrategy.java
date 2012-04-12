package idc.gc.strategy;

import idc.gc.Benchmarker;
import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RandomStrategy implements Strategy {

	private Random rnd = new Random();
	private Benchmarker b = new Benchmarker();
	private final long DURATION=TimeUnit.SECONDS.toMillis(30);

	@Override
	public Set<Circle> execute(Set<Point> points, int n) {
		System.out.println("Executing on "+points.size()+" points with "+n+" circles");
		int best = Integer.MAX_VALUE;
		Set<Circle> bestResults = null;
		final long timeout=System.currentTimeMillis()+DURATION;
		while (System.currentTimeMillis()<timeout) {
			Set<Circle> results = new HashSet<Circle>();
			for (int i = 0; i < n; i++) {
				double r = rnd.nextDouble() * StrategyData.FIELD_SIZE / 2;
				double x = r + rnd.nextDouble() * (StrategyData.FIELD_SIZE - r * 2);
				double y = r + rnd.nextDouble() * (StrategyData.FIELD_SIZE - r * 2);
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
	
	public String getName(){
		return "Trivial Radnom Madness";
	}

}
