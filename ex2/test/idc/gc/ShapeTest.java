package idc.gc;

import idc.gc.dt.Point;
import idc.gc.dt.Squere;

import org.junit.Assert;
import org.junit.Test;

public class ShapeTest {

	@Test
	public void shouldBeInSquere(){
		Squere s=new Squere(new Point(5, 5), 3);
		Assert.assertTrue(s.contains(new Point(7,7)));
	}
	
	@Test
	public void shouldNotBeInSquere1(){
		Squere s=new Squere(new Point(5, 5), 3);
		Assert.assertFalse(s.contains(new Point(4,4)));
	}
	
	@Test
	public void shouldNotBeInSquere2(){
		Squere s=new Squere(new Point(5, 5), 3);
		Assert.assertFalse(s.contains(new Point(8.1,8.1)));
	}


}
