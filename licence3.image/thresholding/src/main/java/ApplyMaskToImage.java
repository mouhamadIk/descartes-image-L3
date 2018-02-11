import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImgPlus;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Command.class, menuPath = "Plugins>TD 3>Apply mask to image")
public class ApplyMaskToImage<T extends RealType<T>> implements Command {

	@Parameter(persist = false)
	ImgPlus<T> image;

	@SuppressWarnings("rawtypes")
	@Parameter(persist = false)
	ImgPlus<RealType> mask;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<T> result;

	@Override
	public void run() {
		// Create result image
		result = image.copy();

		// Use cursors for input, mask, and output images
		Cursor<T> imageCursor = image.localizingCursor();
		@SuppressWarnings("rawtypes")
		RandomAccess<RealType> maskCursor = mask.randomAccess();
		RandomAccess<T> resultCursor = result.randomAccess();

		long[] dims = new long[image.numDimensions()];
		image.dimensions(dims);

		long[] pos = new long[dims.length];
		while (imageCursor.hasNext()) {
			imageCursor.fwd();
			for (int i = 0; i < pos.length; i++) {
				pos[i] = imageCursor.getLongPosition(i);
			}
			maskCursor.setPosition(pos);
			resultCursor.setPosition(pos);

			// Affectez la valeur du pixel sur l'image resultat selon la valeur de la
			// masque.
			
		}

	}

}
