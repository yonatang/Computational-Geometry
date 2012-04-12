package idc.gc.strategy;

import idc.gc.Benchmarker;
import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class StatisticalTest {

	private static void params(String input, int n, int decay,int fail,int success) throws IOException, InterruptedException, ExecutionException{
		AntColonyStrategy st1=new AntColonyStrategy();
		st1.setDeceyRate(decay);
		st1.setFailRate(fail);
		st1.setSuccessRate(success);
		StrategyRunner sr1=new StrategyRunner(st1, input, n);
		long now=System.currentTimeMillis();
		double result=sr1.exec();
		long duration=System.currentTimeMillis()-now;
		System.out.println("n: "+n+"\t dec:"+decay+",fail:"+fail+",succ:"+success+"\t duration: "+duration+"\t score: "+result);
	}
	
	public static void main(String[] args) throws Exception{
		String input="samples/bm_grid100_.txt";
		double result;
		long now;
		long duration;
		
		for (int n:Arrays.asList(10,20,30,40)){
		AntColonyStrategy st1=new AntColonyStrategy() {protected int getExp(int inputSize, int n) {return inputSize*n/10;};};
			StrategyRunner sr=new StrategyRunner(st1, input, n);
			now=System.currentTimeMillis();
			result=sr.exec();
			duration=System.currentTimeMillis()-now;
			System.out.println("n: "+n+"\t inputSize*n/10\t duration: "+duration+"\t score: "+result);
			
			AntColonyStrategy st3=new AntColonyStrategy() {protected int getExp(int inputSize, int n) {return inputSize*10;};};
			StrategyRunner sr3=new StrategyRunner(st3, input, n);
			now=System.currentTimeMillis();
			result=sr3.exec();
			duration=System.currentTimeMillis()-now;
			System.out.println("n: "+n+"\t inputSize*10\t duration: "+duration+"\t score: "+result);
			
			AntColonyStrategy st2=new AntColonyStrategy() {protected int getExp(int inputSize, int n) {return inputSize*n;};};
			StrategyRunner sr2=new StrategyRunner(st2, input, n);
			now=System.currentTimeMillis();
			result=sr2.exec();
			duration=System.currentTimeMillis()-now;
			System.out.println("n: "+n+"\t inputSize*n\t duration: "+duration+"\t score: "+result);
		}
		System.out.println();
		System.out.println("=====");
		
		for (int n:Arrays.asList(10,20,30,40)){
			System.out.println();
			params(input, n, 2, 1, 2);
			params(input, n, 2, 2, 2);
			params(input, n, 2, 4, 2);
			params(input, n, 2, 16, 2);
			System.out.println();
			params(input, n, 2, 2, 1);
			params(input, n, 2, 2, 4);
			params(input, n, 2, 2, 16);
			System.out.println();
			params(input, n, 1, 2, 1);
			params(input, n, 2, 2, 1);
			params(input, n, 4, 2, 1);
			System.out.println();
			params(input, n, 1, 2, 2);
			params(input, n, 2, 2, 2);
			params(input, n, 4, 2, 2);
		}


	}

	public static class StrategyRunner {
		final AntColonyStrategy strategy;
		final String input;
		final int n;

		public StrategyRunner(AntColonyStrategy strategy, String input, int n) {
			this.strategy = strategy;
			strategy.setSilent(true);
			this.input = input;
			this.n = n;
		}

		public double exec() throws IOException, InterruptedException, ExecutionException {
			final Benchmarker b = new Benchmarker();
			FileReader fr = new FileReader(input);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			final Set<Point> points = new HashSet<Point>();
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(", ");
				points.add(new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
			}
			ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			int testsNum = 40;
			List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
			for (int i = 0; i < testsNum; i++) {
				tasks.add(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						Set<Circle> circles = strategy.execute(points, n);
						return b.score(points, circles);
					}
				});
			}
			List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
			for (Callable<Integer> task:tasks){
				futures.add(es.submit(task));
			}
			es.shutdown();

			int sum = 0;
			for (Future<Integer> f : futures) {
				sum += f.get();
			}
			
			es.awaitTermination(10, TimeUnit.SECONDS);
			return (new Double(sum) / new Double(testsNum));

		}

	}
}

