import net.imagej.Dataset;
import net.imagej.ImageJ;

/**
 * This class shows some basic command calls to the ImageJ API.
 * 
 * @author Daniel Felipe Gonzalez Obando
 */
public class IntroToImageJAPI {
	public static void main(String[] args) throws Exception {
		// Create ImageJ instance
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		// Counting plugins
		final int pluginCount = ij.plugin().getIndex().size();
		System.out.printf("There are %s plugins available.\n", pluginCount);

		// Loggin a warning message
		ij.log().warn("Death Star approaching!");

		// Setting a message on the status bar
		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ij.status().showStatus((i + 1) * 10, 100, String.format("Long Task %d/%d", i + 1, 10));
			}
		}).start();

		// Counting menu items
		final int menuItemCount = ij.menu().getMenu().size();
		System.out.printf("There are %d menu items in total.\n", menuItemCount);

		// Opening a URL from ImageJ
		// ij.platform().open(new URL("http://imagej.net/"));

		// Opening an image
		Dataset clown = (Dataset) ij.io().open("http://imagej.net/images/clown.png");
		ij.ui().show(clown);
	}
}
