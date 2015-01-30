package dm.timeseries.unsupervised;

public class DTWwindowed {
	private double[] refSeq;
	private int n;

	private double[] testSeq;
	private int m;
	
	private int K;
	private int window;
	
	private int[][] warpingPath;
	private double warpingDistance;
	
	public DTWwindowed(double[] test, double[] ref, int winSize) {
		refSeq = ref;
		n = refSeq.length;	
		
		testSeq = test;
		m = testSeq.length;
		K = 1;
		
		window = Math.max(winSize, Math.abs(n-m));
		
		warpingPath = new int[n + m][2];	// max(n, m) <= K < n + m
		warpingDistance = 0.0;
		
		this.compute();
	}
	
	public void compute() {
		double cost = 0.0;
		
		double[][] d = new double[n][m];	// local distances
		double[][] D = new double[n][m];	// global distances
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				d[i][j] = distanceBetween(refSeq[i], testSeq[j]);
			}
		}
//		for(int row=0; row<n; row++) {
//			for(int col = 0; col<m; col++) {
//				System.out.print("\t" + d[row][col]);
//			}
//			System.out.println();
//		}
		
		D[0][0] = d[0][0];
		
		for (int i = 1; i < n; i++) {
			for (int j = 1; j < m; j++) {
				D[i][j] = Double.POSITIVE_INFINITY;
			}
		}

		for (int i = 1; i < n; i++) {
			for (int j = Math.max(1, i-window); j < Math.min(m, i+window); j++) {
				D[i][j] = d[i][j] + Math.min(Math.min(D[i-1][j], D[i-1][j-1]), D[i][j-1]);
				//cost = d[i][j] + Math.min(Math.min(D[i-1][j], D[i-1][j-1]), D[i][j-1]);
				//D[i][j] = cost;
			}
		}
		
		for(int row=0; row<n; row++) {
			for(int col = 0; col<m; col++) {
				System.out.print("\t" + D[row][col]);
			}
			System.out.println();
		}
		System.out.println();
		

		cost = D[n - 1][m - 1];

		int i = n - 1;
		int j = m - 1;
		int minIndex = 1;
	
		warpingPath[K - 1][0] = i;
		warpingPath[K - 1][1] = j;
		
		while ((i + j) != 0) {
			if (i == 0) {
				j -= 1;
			} else if (j == 0) {
				i -= 1;
			} else {	// i != 0 && j != 0
				double[] array = { D[i - 1][j], D[i][j - 1], D[i - 1][j - 1] };
				minIndex = this.getIndexOfMinimum(array);

				if (minIndex == 0) {
					i -= 1;
				} else if (minIndex == 1) {
					j -= 1;
				} else if (minIndex == 2) {
					i -= 1;
					j -= 1;
				}
			} // end else
			K++;
			warpingPath[K - 1][0] = i;
			warpingPath[K - 1][1] = j;
		} // end while
		warpingDistance = cost / K;
		
		this.reversePath(warpingPath);
	}
	
	private void reversePath(int[][] path) {
		int[][] newPath = new int[K][2];
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < 2; j++) {
				newPath[i][j] = path[K - i - 1][j];
			}
		}
		warpingPath = newPath;
	}
	
	private double distanceBetween(double p1, double p2) {
		return Math.sqrt(Math.pow(p1 - p2, 2));
	}

	private int getIndexOfMinimum(double[] array) {
		int index = 0;
		double val = array[0];

		for (int i = 1; i < array.length; i++) {
			if (array[i] < val) {
				val = array[i];
				index = i;
			}
		}
		return index;
	}

	public String toString() {
		String retVal = "Warping Distance: " + warpingDistance + "\n";
		retVal += "Warping Path: {";
		for (int i = 0; i < K; i++) {
			retVal += "(" + warpingPath[i][0] + ", " +warpingPath[i][1] + ")";
			retVal += (i == K - 1) ? "}" : ", ";
			
		}
		return retVal;
	}
	
//	public static void main(String[] args) {
//		double[] testSample = 
//				{1f, 1f, 2f, 3f, 2f, 0f};
//				//{1.5f,	3.9f,	4.1f,	3.3f,	6.1f,	5.9f,	7.1f,	4.5f};
//		double[] refSample = 
//				{0f, 1f, 1f, 2f, 3f, 2f, 1f}; 
//				//{2.1f,	2.45f,	3.673f,	4.32f,	2.05f,	1.93f,	5.67f,	6.01f, 4.19f};
//		DTWwindowed dtw = new DTWwindowed(testSample, refSample, 5);
//		System.out.println();
//		System.out.println(dtw);
//	}
}
