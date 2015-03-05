//import dm.association.freqpatterns.SimpleApriori;
//import dm.clustering.unsupervised.Kmeans;
//import dm.clustering.unsupervised.EM;
import dm.timeseries.unsupervised.DTW;
//import dm.timeseries.unsupervised.DTWwindowed;

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
//		double[] train = {-0.8322489017, -1.1931432668, -1.4383300629, -1.5355203329, -1.5760134382, -1.4058373088, -1.0124809856, -0.2450203967, 0.414756585, 0.890416685, 0.9956348629, 0.9207329353, 0.7255759844, 0.3507978423, 0.2214349269, 0.2727404527, 0.3328191452, 0.553387854, 0.8308624965, 1.2828731559, 0.9977267532, 0.68149382, 0.1216088508, -0.3542676659};
		double[] train = {-0.3003904685, -0.8376110782, -1.1590628815, -1.3523498083, -1.3902962815, -1.2714194803, -1.237593618, -0.8474446458, 0.0611163981, 0.9067542116, 1.1970606435, 1.1949258768, 0.9739296832, 0.5654512479, 0.3487082912, 0.3902532731, 0.399899231, 0.2397281628, 0.1031163147, 0.1799944291, 0.2338054518, 0.7506620104, 0.6607518549, 0.1900111705};
			//{1.5f,	3.9f,	4.1f,	3.3f,	6.1f,	5.9f,	7.1f,	4.5f};
//		double[] test = {-0.7675200048, -1.1576046688, -1.4221042922, -1.5390708932, -1.5681495696, -1.4359471541, -1.0919486413, -0.3202383862, 0.3847862058, 0.8853429554, 1.0295545405, 0.9727260505, 0.779707544, 0.364169017, 0.2333806483, 0.2769323225, 0.3145695311, 0.4866341451, 0.9202445171, 1.2499536413, 0.9513623713, 0.6149260528, 0.123494535, -0.2852004671};
		double[] test = {-0.3879889374, -0.8731403491, -1.216750192, -1.3725213798, -1.4331384675, -1.3282584986, -1.2417257644, -0.8295365296, 0.1880470813, 1.0021340517, 1.2165391326, 1.1912327235, 0.9843410543, 0.5457732573, 0.4182438466, 0.4636027189, 0.4696695962, 0.2810521592, 0.1483568994, 0.1587572354, 0.2159705109, 0.6483052338, 0.581153693, 0.1698809291};
				//{2.1f,	2.45f,	3.673f,	4.32f,	2.05f,	1.93f,	5.67f,	6.01f, 4.19f};
		DTW dtw = new DTW(train, test);
//		DTWwindowed dtwW = new DTWwindowed(testSample, refSample, 3);
//		System.out.println();
		System.out.println(dtw);
//		System.out.println(dtwW);
	}
}
