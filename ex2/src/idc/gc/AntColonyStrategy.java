package idc.gc;

import idc.gc.dt.Circle;
import idc.gc.dt.Point;
import idc.gc.strategy.Strategy;
import idc.gc.strategy.StrategyData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class AntColonyStrategy implements Strategy {
	private final Random rnd = new Random();

	private final double SIZE = StrategyData.FIELD_SIZE;

	private final double MIN_STEP = 5;
	private final double INITIAL_SCENT = 2d;
	private final double MIN_SCENT = 0.00001;
	private final double MAX_SCENT = 1000;

	private final double SUCCESS_INC_FACTOR = 1.1;
	private final double FAIL_DEC_FACTOR = 1;
	private final double DECEY_FACTOR = 1.1;

	private enum Action {
		MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, INC, DEC
	}

	private double stepSize() {
		return MIN_STEP + rnd.nextDouble() * 5;
	}

	private class CircleAction {

		public CircleAction(Circle circle, Action action) {
			super();
			this.circle = circle;
			this.action = action;

		}

		private final Circle circle;
		private final Action action;

		public Circle getCircle() {
			return circle;
		}

		public Action getAction() {
			return action;
		}

		public void doAction(double step) {
			Circle c = getCircle();
			Point p = c.getP();

			switch (getAction()) {
			case MOVE_UP:
				p.setY(p.getY() - step);
				break;
			case MOVE_DOWN:
				p.setY(p.getY() + step);
				break;
			case MOVE_LEFT:
				p.setX(p.getX() - step);
				break;
			case MOVE_RIGHT:
				p.setX(p.getX() + step);
				break;
			case INC:
				c.setR(c.getR() + step);
				break;
			case DEC:
				c.setR(Math.max(c.getR() -step, 1));
				break;
			}
			// case MOVE_UP:
			// p.setY(Math.max(p.getY() - stepSize(), 0));
			// break;
			// case MOVE_DOWN:
			// p.setY(Math.min(p.getY() + stepSize(), SIZE));
			// break;
			// case MOVE_LEFT:
			// p.setX(Math.max(p.getX() - stepSize(), 0));
			// break;
			// case MOVE_RIGHT:
			// p.setX(Math.min(p.getX() + stepSize(), SIZE));
			// break;
			// case INC:
			// c.setR(Math.min(c.getR() + stepSize(), 50));
			// break;
			// case DEC:
			// c.setR(Math.max(c.getR() - stepSize(), 1));
			// break;
			// }
		}

		@Override
		public String toString() {
			return "CircleAction [circle=" + circle + ", action=" + action + "]";
		}
	}

	private class Phermons {
		private Map<CircleAction, Double> phermons = new HashMap<AntColonyStrategy.CircleAction, Double>();

		public void addCircleAction(CircleAction action, double scent) {
			phermons.put(action, scent);
		}

		public double getScent(CircleAction action) {
			return phermons.get(action);
		}

		public void deceyScent() {
			for (CircleAction action : phermons.keySet()) {
				double scent = phermons.get(action);
				updateScent(action, scent / DECEY_FACTOR);
			}
		}

		public void updateScent(CircleAction action, double scent) {
			phermons.put(action, Math.min(Math.max(scent, MIN_SCENT), MAX_SCENT));
		}

		public CircleAction followScent() {
			double sum = 0;
			for (Double d : phermons.values()) {
				sum += d;
			}

			double random = rnd.nextDouble() * sum;

			double soFar = 0;
			for (Entry<CircleAction, Double> e : phermons.entrySet()) {
				soFar += e.getValue();
				if (soFar > random)
					return e.getKey();
			}
			System.out.println(sum);
			System.out.println(soFar);
			System.out.println(random);
			System.out.println(phermons);
			throw new IllegalStateException("Reached end. Weird.");
		}

		public Circle getSmallestCircle() {
			Set<Circle> circles = new HashSet<Circle>();
			for (CircleAction c : phermons.keySet()) {
				circles.add(c.getCircle());
			}
			Circle worst = null;
			double min = Double.MAX_VALUE;
			for (Circle c : circles) {
				if (c.getR() < min) {
					min = c.getR();
					worst = c;
				}
			}
			return worst;
		}

		public Circle getWorstCircle() {
			HashMap<Circle, Double> circles = new HashMap<Circle, Double>();
			for (Entry<CircleAction, Double> e : phermons.entrySet()) {
				Circle c = e.getKey().getCircle();
				if (circles.containsKey(c)) {
					circles.put(c, circles.get(c) + e.getValue());
				} else {
					circles.put(c, e.getValue());
				}
			}
			Circle worst = null;
			double min = Double.MAX_VALUE;
			for (Circle c : circles.keySet()) {
				if (circles.get(c) < min) {
					min = circles.get(c);
					worst = c;
				}
			}
			return worst;
		}
	}

	@Override
	public Set<Circle> execute(Set<Point> points, int n) {
		// Map<CircleAction, Double> phermons = new
		// HashMap<AntColonyStrategy.CircleAction, Double>();
		Phermons ph = new Phermons();
		Set<Circle> circles = new HashSet<Circle>();
		Benchmarker b = new Benchmarker();
		for (int i = 0; i < n; i++) {
			Circle c = new Circle(new Point(rnd.nextDouble() * StrategyData.FIELD_SIZE, rnd.nextDouble() * StrategyData.FIELD_SIZE),
					MIN_STEP);
			circles.add(c);
			for (Action a : Action.values()) {
				ph.addCircleAction(new CircleAction(c, a), INITIAL_SCENT);
			}
		}

		int lastScore = b.score(points, circles);
		int notImproved = 0;
		long iterations = 0;
		Set<Circle> best=null;
		int bestScore=Integer.MAX_VALUE;
		while (true) {
			notImproved++;
			iterations++;
			CircleAction action = ph.followScent();
			double step=stepSize();
			System.out.println(action+" - "+step);
			action.doAction(step);
			int score = b.score(points, circles);
			double scent = ph.getScent(action);
			if (score < lastScore) {
				ph.updateScent(action, scent * SUCCESS_INC_FACTOR);
				notImproved = 0;
			} else if (score > lastScore) {
				ph.updateScent(action, scent / FAIL_DEC_FACTOR);
			}
			if (score<bestScore){
				best=copyResult(circles);
				bestScore=score;
			}
			ph.deceyScent();
			if (iterations % 1000 == 0) {
				System.out.println("Iteration " + iterations +" score is "+score);

			}
			if (iterations > 10000) {
				System.out.println("Force quit");
				break;
			}
			if (notImproved > 2000)
				break;
		}

		System.out.println("Iterations: " + iterations);
		return best;
	}
	
	private Set<Circle> copyResult(Set<Circle> circles){
		HashSet<Circle> result=new HashSet<Circle>();
		for (Circle c:circles){
			result.add(c.deepClone());
		}
		return result;
	}

	@Override
	public String getName() {
		return "Ant Colony";
	}

}
