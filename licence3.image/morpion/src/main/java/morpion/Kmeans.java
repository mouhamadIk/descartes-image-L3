package morpion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ij.blob.Blob;
import ij.blob.ManyBlobs;



public class Kmeans {

	private List<ManyBlobs> classes;
	private int nbClasses;

	/**
	 * Kmeans contructor, permet d'executer un algorithme Kmeans a k classes
	 * 
	 * @param k
	 * @param mb
	 */
	public Kmeans(int k, ManyBlobs mb) {
		this.nbClasses = k;
		classes = new ArrayList<>();
		for (int i = 0; i < k; i++)
			classes.add(new ManyBlobs());
		kMeans(mb);
	}

	public List<ManyBlobs> getClasses() {
		return classes;
	}

	/**
	 * Calcule la distance euclidienne entre deux GFD ( float[][]) Comme la taille
	 * du vecteur GFD est fixe, les deux vecteurs sont donc de meme taille.
	 * 
	 * @param gfd1
	 * @param gfd2
	 * @return la distance euclidienne entre les deux GFD
	 * 
	 */
	public static float distance(GFD gfd1, GFD gfd2) {
		float res = 0;
		for (int i = 0; i < gfd1.getGFD().length; i++) {
			for (int j = 0; j < gfd1.getGFD()[i].length; j++) {
				res += (Math.pow(gfd1.getGFD()[i][j] - gfd2.getGFD()[i][j], 2));
			}
		}
		return (float) Math.sqrt(res);
	}
	
	public static float mesureSimilarite(GFD gfd1, GFD gfd2) {
		float min = 0, max = 0;
		for (int i = 0; i < gfd1.getGFD().length; i++) {
			for (int j = 0; j < gfd1.getGFD()[i].length; j++) {
				min += Math.min(gfd1.getGFD()[i][j], gfd2.getGFD()[i][j]);
				max += Math.max(gfd1.getGFD()[i][j], gfd2.getGFD()[i][j]);
			}
		}
		return (min / max);
	}

	/**
	 * Execute l'algorithme K-means pour k classes donnee dans le constructeur
	 * 
	 * @param mb
	 */
	private void kMeans(ManyBlobs mb) {
		System.out.println("Kmeans premiere iteration");

		int randomCC = 0;
		ManyBlobs mbTmp = mb;
		int cpt = 0;
		List<GFD> gfds = new ArrayList<>();
		List<Float> histoDist = new ArrayList<>();
		List<ManyBlobs> classesTmp = new ArrayList<>();
		List<float[][]> recalculGFD = new ArrayList<>();
		boolean bool = true;
		float[][] floatTmp = new float[4][9];

		GFD gfdTmp;
		// initialisation des tableau de recalculGFD
		// Et en meme temps
		// random
		for (int i = 0; i < nbClasses; i++) {
			randomCC = (int) (Math.random() * (mbTmp.size()));
			classes.get(i).add(mbTmp.get(randomCC));
			recalculGFD.add(new float[4][9]);
			mbTmp.remove(randomCC);
		}

		// Calcule du premier GFD
		for (int i = 0; i < nbClasses; i++) // pour init les GFD
			gfds.add(new GFD(Blob.generateBlobImage(classes.get(i).get(0)).getProcessor()));

		// premiere confrontation des mesureSimilarites
		for (Blob b : mb) {
			gfdTmp = new GFD(Blob.generateBlobImage(b).getProcessor());
			// gfdTmp.histo_GFD();
			// enregistrement des mesureSimilarites pour les comparer
			for (int i = 0; i < nbClasses; i++) {
				histoDist.add(Kmeans.mesureSimilarite(gfds.get(i), gfdTmp));
			}

			// Trie les mesureSimilarites
			histoDist.sort(new Comparator<Float>() {

				@Override
				public int compare(Float num1, Float num2) {
					if (num1.compareTo(num2) > 0) {
						return -1;
					} else if (num1.compareTo(num2) < 0) {
						return 1;
					} else {
						return 0;
					}
				}
			});

			// On trouve la plus petite et on affecte a la classe adaptee
			for (int i = 0; i < nbClasses; i++) {
				if (Kmeans.mesureSimilarite(gfds.get(i), gfdTmp) == histoDist.get(0)) {
					classes.get(i).add(b);
				}
			}
			histoDist.clear();
		}

		// on boucle pour trouver le moment ou les classes ne changeront plus

		do {
			// On enregistre le classes actuel dans classesTMP
			for (int i = 0; i < nbClasses; i++) {
				for (ManyBlobs b : classes)
					classesTmp.add(b);
			}

			gfds.clear();
			// calcule des nouveaux GFD de classes
			for (ManyBlobs m : classes) {
				for (int col1 = 0; col1 < floatTmp.length; col1++) {
					for (int col2 = 0; col2 < floatTmp[col1].length; col2++) {
						floatTmp[col1][col2] = 0;
					}
				}
				cpt = 0;
				for (Blob b : m) {
					gfds.add(new GFD(Blob.generateBlobImage(b).getProcessor()));
				}
				// floatTmp = recalculGFD.get(cpt);
				for (GFD g : gfds) {
					// g.histo_GFD();
					for (int col1 = 0; col1 < g.getGFD().length; col1++) {
						for (int col2 = 0; col2 < g.getGFD()[col1].length; col2++) {
							floatTmp[col1][col2] += g.getGFD()[col1][col2];
						}
					}
					for (int col1 = 0; col1 < floatTmp.length; col1++) {
						for (int col2 = 0; col2 < floatTmp[col1].length; col2++) {
							floatTmp[col1][col2] = floatTmp[col1][col2] / gfds.size();
						}
					}
					recalculGFD.remove(cpt);
					recalculGFD.add(cpt, floatTmp);
				}
				cpt++;

			}
			for (ManyBlobs b : classes) {
				b.clear();
			}

			// Recalcule les mesureSimilarites avec les points
			for (Blob b : mb) {
				gfdTmp = new GFD(Blob.generateBlobImage(b).getProcessor());
				// gfdTmp.histo_GFD();
				// enregistrement des mesureSimilarites pour les comparer
				for (int i = 0; i < nbClasses; i++) {
					histoDist.add(Kmeans.mesureSimilarite(gfds.get(i), gfdTmp));
				}

				// Trie les mesureSimilarites

				histoDist.sort(new Comparator<Float>() {

					@Override
					public int compare(Float num1, Float num2) {
						if (num1.compareTo(num2) > 0) {
							return -1;
						} else if (num1.compareTo(num2) < 0) {
							return 1;
						} else {
							return 0;
						}
					}
				});

				// On trouve la plus petite et on affecte a la classe adaptee
				for (int i = 0; i < gfds.size(); i++) {
					if (Kmeans.mesureSimilarite(gfds.get(i), gfdTmp) == histoDist.get(0)) {
						classes.get(i).add(b);
					}
				}
				histoDist.clear();
			}

			for (int i = 0; i < nbClasses; i++) {
				if (!classes.get(i).containsAll(classesTmp.get(i))) {
					bool = true;
					i = nbClasses;
				} else {
					bool = false;
				}
			}
		} while (bool);
		cpt = 1;
		for (ManyBlobs m : classes) {
			System.out.println("Classe num" + cpt);
			for (Blob b : m) {
				System.out.println(b);
			}
			cpt++;
		}
	}
}