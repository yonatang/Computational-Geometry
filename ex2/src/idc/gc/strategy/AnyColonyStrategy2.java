package idc.gc.strategy;

import idc.gc.Benchmarker;
import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AnyColonyStrategy2 implements Strategy {

	private final Random RND = new Random();

//	private final double DECEY_RATE = 2;
//	private final double SUCCESS_RATE=2;
//	private final double FAIL_RATE=4;
//	private final double MIN_SCENT = 0.0001;
//	private final double MAX_SCENT = 10000;
//	private final long DURATION=TimeUnit.SECONDS.toMillis(30);

	private double deceyRate = 2;
	private double successRate=10;
	private double failRate=4;
	private double minScent = 1;
	private double maxScent = 100;
	private final long DURATION=TimeUnit.SECONDS.toMillis(30);

	
	@Override
	public Set<Circle> execute(Set<Point> points, int n) {
		System.out.println("Executing on "+points.size()+" points with "+n+" circles");
		int exp=points.size()/10*n;
		Phermons ph = new Phermons();
		Set<Circle> circles = new HashSet<Circle>();
		Benchmarker b = new Benchmarker();
		for (int i = 0; i < n; i++) {
			Circle c = new Circle(new Point(RND.nextDouble() * StrategyData.FIELD_SIZE, RND.nextDouble() * StrategyData.FIELD_SIZE), 5);
			circles.add(c);
			for (Action a : Action.values()) {
				ph.addCircleAction(new CircleAction(c, a), 1);
			}
		}
		int lastScore = b.score(points, circles);
		int notImproved = 0;
		int iterations = 0;
		int bestScore=Integer.MAX_VALUE;
		Set<Circle> bestSet=null;
		final long timeout=System.currentTimeMillis()+DURATION;
		while (System.currentTimeMillis()<timeout) {
			notImproved++;
			iterations++;
			CircleAction action = ph.followScent();
			double step = RND.nextGaussian() * 10;
//			System.out.print(action + ", step: " + step);
			action.doAction(step);
			int score = b.score(points, circles);
//			System.out.println(", score: " + score);
			double scent = ph.getScent(action);
			if (score < lastScore) {
				ph.updateScent(action, scent * successRate);
				
			} else if (score > lastScore) {
				ph.updateScent(action, scent / failRate);
			}
			lastScore = score;
			if (score<bestScore){
				bestSet=copyResult(circles);
				bestScore=score;
				System.out.println("Improved after "+notImproved);
				notImproved = 0;
			}
			ph.deceyScent();
			if (iterations % 1000 == 0) {
				System.out.println("Iteration " + iterations + " score is " + bestScore);

			}
			if (notImproved > exp*100)
				break;
		}
		return bestSet;
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

	private enum Action {
		MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, INC, DEC
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
				if (p.getY()<c.getR()){
					p.setY(c.getR());
				}
				break;
			case MOVE_DOWN:
				p.setY(p.getY() + step);
				if (p.getY()+c.getR()>StrategyData.FIELD_SIZE){
					p.setY(StrategyData.FIELD_SIZE-c.getR());
				}
				break;
			case MOVE_LEFT:
				p.setX(p.getX() - step);
				if (p.getX()<c.getR()){
					p.setX(c.getR());
				}
				break;
			case MOVE_RIGHT:
				p.setX(p.getX() + step);
				if (p.getX()+c.getR()>StrategyData.FIELD_SIZE){
					p.setX(StrategyData.FIELD_SIZE-c.getR());
				}
				break;
			case INC:
				c.setR(c.getR() + step);
				if (c.getR()*2>StrategyData.FIELD_SIZE){
					c.setR(StrategyData.FIELD_SIZE/2);
				}
				break;
			case DEC:
				c.setR(Math.max(c.getR() - step, 1));
				break;
			}
		}

		@Override
		public String toString() {
			return "CircleAction [circle=" + circle + ", action=" + action + "]";
		}
	}

	private class Phermons {
		private Map<CircleAction, Double> phermons = new HashMap<CircleAction, Double>();

		public void addCircleAction(CircleAction action, double scent) {
			phermons.put(action, scent);
		}

		public double getScent(CircleAction action) {
			return phermons.get(action);
		}

		public void deceyScent() {
			for (CircleAction action : phermons.keySet()) {
				double scent = phermons.get(action);
				updateScent(action, scent / deceyRate);
			}
		}

		public void updateScent(CircleAction action, double scent) {
			phermons.put(action, Math.min(Math.max(scent, minScent), maxScent));
		}

		public CircleAction followScent() {
			double sum = 0;
			for (Double d : phermons.values()) {
				sum += d;
			}

			double random = RND.nextDouble() * sum;

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
	}
	
	public double getDeceyRate() {
		return deceyRate;
	}

	public void setDeceyRate(double deceyRate) {
		this.deceyRate = deceyRate;
	}

	public double getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(double successRate) {
		this.successRate = successRate;
	}

	public double getFailRate() {
		return failRate;
	}

	public void setFailRate(double failRate) {
		this.failRate = failRate;
	}

	public double getMinScent() {
		return minScent;
	}

	public void setMinScent(double minScent) {
		this.minScent = minScent;
	}

	public double getMaxScent() {
		return maxScent;
	}

	public void setMaxScent(double maxScent) {
		this.maxScent = maxScent;
	}	

}
