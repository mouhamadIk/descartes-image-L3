import net.imagej.ImageJ;

/**
 * @author Daniel Felipe Gonzalez Obando
 *
 */
public class Main {

	public static void main(String[] args) {
		// Load ImageJ UI
		final ImageJ ij = new ImageJ();
		ij.launch(args);

		// Run the gradient command.
		ij.command().run(Gradient.class, true);
	}

}
