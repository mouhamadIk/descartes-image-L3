package morpion;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.plugin.Duplicator;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import net.imagej.Dataset;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;
import projetL3.traitement.Blob;
import projetL3.traitement.ManyBlobs;

@Plugin(type = Command.class, name = "morpion", menuPath = "Plugins>Morpion")
public class Morpion<T extends RealType<T>> implements Command {
	private List<Blob> joueur1;
	private List<Blob> joueur2;
	@Parameter
	CommandService cs;

	@Parameter
	OpService ops;

	@Parameter
	ConvertService conv;

	@Parameter
	Dataset inputImage;

	@Parameter(type = ItemIO.OUTPUT)
	ImagePlus output;

	@Override
	public void run() {
		ImagePlus image = convertInputToImagePlus();
		Duplicator dupli = new Duplicator();

		ImagePlus image_thresholded = dupli.run(image);
		image_thresholded.getProcessor().autoThreshold();

		ImagePlus image_skeletonized = skeletonize(dupli.run(image_thresholded));

		ImagePlus image_convolved = convolve(dupli.run(image_skeletonized));

		image_convolved.getProcessor().threshold(127);

		Blob greaterBlob = getLargestConnectedComponants(dupli.run(image_thresholded));

		double angle = greaterBlob.getOrientationMajorAxis();

		ImagePlus image_grill = Blob.generateBlobImage(greaterBlob);

		image_grill.getProcessor().rotate(angle * 9 / 10);

		System.out.println(greaterBlob.getCenterOfGravity());
		System.out.println(angle);
		ImagePlus imp = dupli.run(image_thresholded);

		output = image_grill;
	}

	private ImagePlus convertInputToImagePlus() {
		ImagePlus imp = conv.convert(inputImage, ImagePlus.class);
		ImageConverter c = new ImageConverter(imp);
		c.convertToGray8();
		imp = imp.duplicate();
		return imp;
	}

	private ImagePlus skeletonize(ImagePlus imp) {
		ByteProcessor pr = (ByteProcessor) imp.getProcessor().convertToByte(true);
		BinaryProcessor binPr = new BinaryProcessor(pr);
		binPr.skeletonize();

		return imp;
	}

	private ImagePlus convolve(ImagePlus imp) {
		ByteProcessor pr = (ByteProcessor) imp.getProcessor().convertToByte(true);
		int[] kernel = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		BinaryProcessor binPr = new BinaryProcessor(pr);
		binPr.convolve3x3(kernel);

		return imp;
	}

	private Blob getLargestConnectedComponants(ImagePlus imp) {
		ManyBlobs manyBlobs = new ManyBlobs(imp);
		manyBlobs.findConnectedComponents();
		Blob greaterBlob = manyBlobs.get(0);

		for (Blob blob : manyBlobs) {
			if (blob.getPerimeter() > greaterBlob.getPerimeter()) {
				greaterBlob = blob;
			}
		}

		return greaterBlob;
	}

	private void findSymboles(ImagePlus imp) {
		ManyBlobs manyBlobs = new ManyBlobs(imp);
		manyBlobs.findConnectedComponents();
		joueur1 = new ArrayList<>();
		joueur2 = new ArrayList<>();

		manyBlobs = manyBlobs.filterBlobs(20, Blob.GETPERIMETER);

		manyBlobs.sort(new Comparator<Blob>() {
			@Override
			public int compare(Blob b1, Blob b2) {
				return (int) (b1.getPerimeter() - b2.getPerimeter());

			}
		});

		Map<Blob, GFD> gfds = new HashMap<>();
		for (Blob blob : manyBlobs) {
			gfds.put(blob, new GFD(Blob.generateBlobImage(blob).getProcessor()));
		}

		GFD g = gfds.get(manyBlobs.get(manyBlobs.size() - 1));
		for (Blob b : manyBlobs) {
			System.out.println("Mesure simi : " + g.mesureSimilarite(gfds.get(b)));
			if (g.mesureSimilarite(gfds.get(b)) > 0.6) {
				joueur1.add(b);
			} else {
				joueur2.add(b);
			}
		}
	}

	// private ImagePlus getLines(ImagePlus imp) {
	// ByteProcessor pr = (ByteProcessor) imp.getProcessor().convertToByte(true);
	// int[] kernel = { 1, 0, -1, 2, 0, -2, 1, 0, -1 };
	// BinaryProcessor binPr = new BinaryProcessor(pr);
	// binPr.convolve3x3(kernel);
	//
	// return imp;
	// }

}