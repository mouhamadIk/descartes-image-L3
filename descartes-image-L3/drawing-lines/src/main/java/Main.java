import net.imagej.ImageJ;

/**
 * Main class to launch ImageJ
 * @author Daniel Felipe Gonzalez Obando
 */
public class Main {
	public static void main(String[] args) {
		final ImageJ ij = new ImageJ();
		ij.launch(args);
	}
}
