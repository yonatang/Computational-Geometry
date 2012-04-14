package idc.gc;

import idc.gc.dt.Board;

import org.junit.Test;

public class BoardTest {

	@Test
	public void t(){
		Board b=new Board(4);
		b.addToRow(1);
	}
}
