package dm.clustering.unsupervised;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.awt.geom.Point2D;

public class Kmeans {
	private int K;
	private ArrayList<Point2D.Double> mu; // Mu or Mean value of cluster
										  // individuals
	private ArrayList<Point2D.Double> data; // Data values
	private ArrayList<Integer> clusterNum; // The cluster number to which the
										   // data point belongs
	private String inFileName;

//	public static void main(String[] args) {
//		//Kmeans obj = new Kmeans(args);
//		Kmeans obj = new Kmeans(new String[]{"/home/atif/work/java/DMExercises/kmeans_2d_data.txt", "2"});
//		obj.displayCentroids();
//	}

	public Kmeans(String[] args) {
		inFileName = args[0];
		K = Integer.parseInt(args[1]);
		data = new ArrayList<Point2D.Double>();
		mu = new ArrayList<Point2D.Double>(K);
		clusterNum = new ArrayList<Integer>();
		getInputData();
		initializeCentroids();
		clusterData();
	}

	private void clusterData() {
		double eps = 1e-6;
		double minChange;
		double dist;
		double minDist;
		int count;
		double sumX, sumY;
		int minDistIndex;
		do {
			minChange = 0;
			clusterNum.clear();
			for (int i = 0; i < data.size(); i++) {
				minDistIndex = Integer.MAX_VALUE;
				dist = Double.MAX_VALUE;
				minDist = Double.MAX_VALUE;
				for (int k = 0; k < K; k++) {
					dist = data.get(i).distance(mu.get(k));
					if (dist < minDist) {
						minDist = dist;
						minDistIndex = k;
					}
				}
				clusterNum.add(i, minDistIndex);
			}
			for (int k = 0; k < K; k++) {
				count = 0;
				sumX = 0;
				sumY = 0;
				for (int i = 0; i < data.size(); i++) {
					if (clusterNum.get(i) == k) {
						sumX += data.get(i).getX();
						sumY += data.get(i).getY();
						count++;
					}
				}
				if (count > 0) {
					sumX = sumX / count;
					sumY = sumY / count;
					minChange += mu.get(k).distance(sumX, sumY);
					mu.get(k).setLocation(sumX, sumY);
				}
			}
		} while (minChange > eps);
	}

	private void initializeCentroids() {
		for (int i = 0; i < K; i++) {
			mu.add(new Point2D.Double(Math.random() * 100, Math.random() * 100));
		}
	}

	private void getInputData() {
		String line;
		String point;
		double x;
		double y;
		Point2D.Double coordinates;
		int startPt;
		int endPt;
		int startNum, endNum;
		try {
			FileReader frInputFile = new FileReader(inFileName);
			BufferedReader brInputFile = new BufferedReader(frInputFile);
			while ((line = brInputFile.readLine()) != null) {
				do {
					startPt = line.indexOf("(") + 1; // Add 1 to shift to the
													 // next character from the
													 // bracket
					endPt = line.indexOf(")");
					point = line.substring(startPt, endPt);

					startNum = 0;
					endNum = point.indexOf(",");
					x = Double.parseDouble(point.substring(startNum, endNum));
					startNum = point.indexOf(",") + 1;
					endNum = point.length();
					y = Double.parseDouble(point.substring(startNum, endNum));
					coordinates = new Point2D.Double(x, y);
					data.add(coordinates);
					startPt = line.indexOf("(", endPt);
					if (startPt == -1) {
						break;
					} else {
						line = line.substring(startPt);
					}
				} while (line.length() > 1);
			}
			brInputFile.close();
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	public void displayCentroids() {
		for (Point2D.Double pt : mu) {
			System.out.println(pt.toString());
		}
	}
}
