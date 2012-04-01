package idc.gc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

public class Main {

	private Benchmarker b=new Benchmarker();
	private File input;
	public Main(String in){
		input=new File(in);
	}
	public Main(String[] args) {
		input=new File(args[0]);
//		File output=new File("output.txt");
	}
	public void run()throws IOException{
		Strategy str=new RandomStrategy();
		FileReader fr=new FileReader(input);
		System.out.println("REading file "+input);
		BufferedReader br=new BufferedReader(fr);
		String line=null;
		Set<Point> points=new HashSet<Point>();
		while ((line=br.readLine())!=null){
			String[] parts=line.split(", ");
			points.add(new Point(Double.parseDouble(parts[0]),Double.parseDouble(parts[1])));
		}
		
		Set<Circle> circles=str.execute(points,6);
		for (Circle c:circles){
			System.out.println(c);
		}
		System.out.println("Score: "+b.score(points, circles));
		
		JFrame frame=new JFrame("Result");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(410, 410);
		frame.getContentPane().add(new GraphicComponent(points, circles));
		frame.setVisible(true);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
//		new Main(args).run();
		String file;
		file="samples/bm_grid100_.txt";
//		file="samples/bm_grid1000_.txt";
//		file="samples/bm_random_1000_1.txt";
		new Main(file).run();

	}

}
