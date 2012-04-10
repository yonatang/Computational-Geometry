package idc.gc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SqueresStrategy implements Strategy {

	private Set<Squere> squeres=new HashSet<Squere>();
	@Override
	public Set<Circle> execute(Set<Point> points, int n) {
		final Squere ZERO_SQUERE=new Squere(new Point(0, 0), StrategyData.FIELD_SIZE);
		
		squeres.add(ZERO_SQUERE);
		
		// TODO Auto-generated method stub
		return null;
	}
	
//	private Polygon findLargestIntersect(Set<Squere> squeres,Set<Point> points){
//		Squere[] cArr = new Squere[squeres.size()];
//		{
//			int i = 0;
//			for (Squere c : squeres) {
//				cArr[i] = c;
//				i++;
//			}
//		}
//		HashMap<String, Set<Point>> results = new HashMap<String, Set<Point>>();
//		for (Point p : points) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(';');
//			for (int i = 0; i < cArr.length; i++) {
//				//TODO - create a shape, and implement Contains(Point) method, than divider will be generic
//				double dist=dist(cArr[i].getP(), p);
//				if (dist < cArr[i].getR()) {
//					sb.append(i).append(';');
//				}
//			}
//			String circleStr = sb.toString();
//			if (results.containsKey(circleStr)) {
//				results.get(circleStr).add(p);
//			} else {
//				Set<Point> part=new HashSet<Point>();
//				part.add(p);
//				results.put(circleStr, part);
//			}
//		}
//		return results.values();
//	}

}
