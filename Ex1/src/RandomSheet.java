import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomSheet {
	public static String NEW_LINE = System.getProperty("line.separator");

	public static void main(String args[]) throws IOException {
		if (args.length < 2) {
			System.out.println("Not enough args. Need <filename> <#of points>");
			System.exit(1);
		}

		File file = new File(args[0]);
		int amount = Integer.parseInt(args[1]);
		if (file.exists()) {
			if (!file.delete()) {
				System.out.println("Couldn't delete file");
				System.exit(1);
			}
		}
		BufferedWriter br = new BufferedWriter(new FileWriter(file));
		double maxValue = Math.sqrt(amount);
		Random rnd = new Random();
		try {
			br.write(String.valueOf(amount));
			br.write(NEW_LINE);
			for (int i = 0; i < amount; i++) {
				br.write(String.valueOf(rnd.nextDouble() * maxValue));
				br.write(' ');
				br.write(String.valueOf(rnd.nextDouble() * maxValue));
				br.write(NEW_LINE);
			}
		} finally {
			br.close();
		}

	}
}
