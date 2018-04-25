package morpion;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.blob.Blob;
import ij.blob.ManyBlobs;
import ij.plugin.Duplicator;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import net.imagej.Dataset;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Command.class, name = "morpion", menuPath = "Plugins>Morpion")
public class Morpion<T extends RealType<T>> implements Command {
	private ArrayList<Blob> joueur1;
	private ArrayList<Blob> joueur2;
	private ArrayList<Blob> unknown;
	@Parameter
	CommandService cs;

	@Parameter
	OpService ops;

	// @Parameter(persist = false)
	// ImgPlus<T> image;

	// @Parameter(required = false)
	// int threshold = 80;

	// @Parameter(type = ItemIO.INPUT)
	// private ImgPlus<T> img;

	// @Parameter(type = ItemIO.OUTPUT)
	// ImgPlus<T> imageConv;

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

		ImagePlus image_grill = Blob.generateBlobImage(greaterBlob);

		MorpionGame game = new MorpionGame(joueur1, joueur2, unknown, image_convolved);

		// System.out.println("joueur 1 : ");
		// for(Blob b : joueur1)
		// System.out.println(b.getCenterOfGravity());
		// System.out.println("joueur 2 : ");
		// for(Blob b : joueur2)
		// System.out.println(b.getCenterOfGravity());

		// System.out.println(greaterBlob.getCenterOfGravity());
		game.printBoard();
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
		joueur1 = new ArrayList<>();
		joueur2 = new ArrayList<>();

		manyBlobs = manyBlobs.filterBlobs(20, Blob.GETPERIMETER);

		System.out.println(manyBlobs.size());

		Blob greaterBlob = manyBlobs.get(0);
		manyBlobs.sort(new Comparator<Blob>() {
			@Override
			public int compare(Blob b1, Blob b2) {
				return (int) (b1.getPerimeter() - b2.getPerimeter());

			}
		});

		greaterBlob = manyBlobs.get(manyBlobs.size() - 1);

		manyBlobs.remove(greaterBlob);
		Map<Blob, GFD> gfds = new HashMap<>();
		for (Blob blob : manyBlobs) {
			gfds.put(blob, new GFD(Blob.generateBlobImage(blob).getProcessor()));
		}

		// Three classes, player 1, player 2 and unknown
		// Kmeans km = new Kmeans (3, manyBlobs);
		//
		// km.getClasses().sort(new Comparator<ManyBlobs>() {
		// @Override
		// // classes decreasing size sort
		// public int compare(ManyBlobs b1, ManyBlobs b2) {
		// return (int) (b2.size() - b1.size());
		//
		// }
		// });
		//
		// for (int i = 0; i < 3; i++) {
		// ManyBlobs mb = km.getClasses().get(i);
		// for (Blob blob : mb) {
		// if (i == 0) {joueur1.add(blob);}
		// if (i == 1) {joueur2.add(blob);}
		// if (i == 2) {unknown.add(blob);}
		// }
		//
		// }
		GFD g = gfds.get(manyBlobs.get(manyBlobs.size() - 1));

		// Sort by Thinnes Ratio
		manyBlobs.sort(new Comparator<Blob>() {
			@Override
			public int compare(Blob b1, Blob b2) {
				return (int) (b1.getThinnesRatio() - b2.getThinnesRatio());

			}
		});
		
		ArrayList<ManyBlobs> arr = jenksNaturalBreakByThinnesRatio(manyBlobs);

//		for (Blob b : manyBlobs) {
//			System.out.println("Mesure simi : " + b.getThinnesRatio());
//		}		
			joueur1 = arr.get(0);
			joueur2 = arr.get(1);

		return greaterBlob;
	}

	private static ArrayList<ManyBlobs> jenksNaturalBreakByThinnesRatio(ManyBlobs mb) {

		float sdam_min = Float.MAX_VALUE;
		ManyBlobs r1Min = null, r2Min = null;
		// compute SDCM_ALL : 2 ranges
		for (int i = 0; i < mb.size(); i++) {
			ManyBlobs r1 = new ManyBlobs();
			ManyBlobs r2 = new ManyBlobs();

			for (int j = 0; j < i; j++) {
				r1.add(mb.get(j));
			}
			for (int j = i; j < mb.size(); j++) {
				r2.add(mb.get(j));
			}

			float r1Sdam = sdam(r1);
			float r2Sdam = sdam(r2);
			float sumSdam = r1Sdam + r2Sdam;
			if (sumSdam < sdam_min) {
				sdam_min = sumSdam;
				r1Min = r1;
				r2Min = r2;
			}
		}
		ArrayList<ManyBlobs> arr = new ArrayList<ManyBlobs>();
		arr.add(r1Min);
		arr.add(r2Min);
		return arr;
	}

	private static float sdam(ManyBlobs mb) {
		// compute SDAM
		float meanThinnesRatio = meanThinnesRatio(mb);
		float sdam = 0;
		for (Blob blob : mb) {
			sdam += Math.pow(blob.getThinnesRatio() - meanThinnesRatio, 2);
		}
		return sdam;
	}

	private static float meanThinnesRatio(ManyBlobs mb) {
		float meanThinnesRatio = 0;

		for (Blob blob : mb) {
			meanThinnesRatio += blob.getThinnesRatio();
		}
		meanThinnesRatio /= mb.size();
		return meanThinnesRatio;
	}

	private ImagePlus getLines(ImagePlus imp) {
		ByteProcessor pr = (ByteProcessor) imp.getProcessor().convertToByte(true);
		int[] kernel = { 1, 0, -1, 2, 0, -2, 1, 0, -1 };
		BinaryProcessor binPr = new BinaryProcessor(pr);
		binPr.convolve3x3(kernel);

		return imp;
	}

}
