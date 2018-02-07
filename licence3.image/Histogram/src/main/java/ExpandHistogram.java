import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImgPlus;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imglib2.Cursor;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Op.class, name = "Expand histogram", menuPath = "Plugins>TD 2>Expand histogram")
public class ExpandHistogram<T extends RealType<T>> extends AbstractOp {

	@Parameter
	private ImgPlus<T> image;

	@Parameter(type = ItemIO.OUTPUT)
	private ImgPlus<T> expanded;

	@Override
	public void run() {
		double minIntensity = Double.MAX_VALUE;
		double maxIntensity = 0d;
		Cursor<T> cursorImg = image.cursor();
		while (cursorImg.hasNext()) {
			cursorImg.fwd();
			T pixel = cursorImg.get();
			minIntensity = Math.min(minIntensity, pixel.getRealDouble());
			maxIntensity = Math.max(maxIntensity, pixel.getRealDouble());
		}

		expanded = image.copy();
		Cursor<T> cursor = expanded.cursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			T pixel = cursor.get();
			computeNewIntensity(pixel, minIntensity, maxIntensity, 0, 255);
		}
	}

	private void computeNewIntensity(T pixel, double minIntensity, double maxIntensity, double minLimitIntensity,
			double maxLimitIntensity) {
		double intensity = pixel.getRealDouble();
		double range = maxIntensity - minIntensity;
		double targetRange = maxLimitIntensity - minLimitIntensity;
		double newIntensity = 0; // Changez cette ligne par le calcul de l'expansion.
		pixel.setReal(newIntensity);
	}

}
