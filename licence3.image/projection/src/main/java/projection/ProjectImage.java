package projection;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;

@Plugin(type = Command.class, menuPath = "Plugins>TD 4>Projection")
public class ProjectImage<T extends RealType<T>> implements Command {

	@Parameter
	DatasetService ds;

	@Parameter
	OpService ops;

	@Parameter
	Dataset img;

	@Parameter
	boolean horizontal; // true = horizontal projection,
											// false = vertical projection

	@Parameter(type = ItemIO.OUTPUT)
	Dataset proj;

	@Override
	public void run() {

		long[] dims = new long[img.numDimensions()];
		img.dimensions(dims);

		// la taille du resultat est largeur x 10 ou 10 x hauteur
		long[] projDims = new long[] { horizontal ? dims[0] : 10, horizontal ? 10 : dims[1] };
		Img<IntType> res = ArrayImgs.ints(projDims);

		RandomAccess<RealType<?>> imgCursor = img.randomAccess();
		RandomAccess<IntType> projCursor = res.randomAccess();

		long[] posImg = new long[img.numDimensions()];
		long[] posProj = new long[res.numDimensions()];

		// Completez ce code:
		// 1. Pour Chaque ligne/colonne
		// 2. On somme les intensités de toutes les intensités de la colonne/ligne
		// 3. On affecte la somme au pixel(s) de l'image de resultat

		proj = ds.create(res);
	}

}
