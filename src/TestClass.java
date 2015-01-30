//import dm.association.freqpatterns.SimpleApriori;
//import dm.clustering.unsupervised.Kmeans;
//import dm.clustering.unsupervised.EM;
//import dm.timeseries.unsupervised.DTW;
import dm.timeseries.unsupervised.DTWwindowed;

public class TestClass {
	public static void main(String[] args) {
//		SimpleApriori ap = new SimpleApriori(args);
//		ap.output();
		
//		Kmeans obj = new Kmeans(new String[]{"/home/atif/work/java/DMExercises/kmeans_2d_data.txt", "2"});
//		obj.displayCentroids();
		
//		EM obj = new EM("data/em_data.txt",			// Data file
//		                2,							// Number of distributions
//		                new double[]{0.5, 0.5},		// Initial probabilities
//		                new double[]{1, 4},			// Distribution means
//		                new double[]{1, 1});		// Distribution variances
//		obj.calculateEM();
//		obj.displayResult();
		double[] testSample = {0f, 1f, 1f, 2f, 3f, 2f, 1f};
			//{1.5f,	3.9f,	4.1f,	3.3f,	6.1f,	5.9f,	7.1f,	4.5f};
		double[] refSample = {0f, 1f, 1f, 2f, 3f, 2f, 0f}; 
				//{2.1f,	2.45f,	3.673f,	4.32f,	2.05f,	1.93f,	5.67f,	6.01f, 4.19f};
//		DTW dtw = new DTW(testSample, refSample);
		DTWwindowed dtwW = new DTWwindowed(testSample, refSample, 3);
//		System.out.println();
//		System.out.println(dtw);
		System.out.println(dtwW);
	}
}
