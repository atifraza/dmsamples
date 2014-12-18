package dm.clustering.unsupervised;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;

public class EM {
	private String inFileName;
	private int numOfDist;
	private ArrayList<Double> data;
	private double[] weights;
	private double[] distProb;
	private double[] mu;
	private double[] sigma;

	public EM(String dataFile, int nDist, double[] probs, double[] distMeans, double[] distSigma) {
		inFileName = dataFile;
		numOfDist = nDist;
		distProb = probs;
		mu = distMeans;
		sigma = distSigma;
		data = new ArrayList<Double>();
		getInputData();
		weights = new double[data.size()];
	}

	private void getInputData() {
		String line;
		FileReader frInputFile;
		BufferedReader brInputFile;
		try {
			frInputFile = new FileReader(inFileName);
			brInputFile = new BufferedReader(frInputFile);
			while ((line = brInputFile.readLine()) != null) {
				data.add(Double.parseDouble(line));
			}
			brInputFile.close();
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	public void calculateEM() {
		double[] oldMu = new double[numOfDist];
		double[] oldSigma = new double[numOfDist];
		double tol = 1e-6;
		boolean isImproving = true;
		int count = 0;
		while(isImproving || count < 25) {
			for(int j=0; j<numOfDist; j++) {
				oldMu[j] = mu[j];
				oldSigma[j] = sigma[j];
			}
			calcLogLikelihood();
			stepE();
			stepM();
			if(Math.abs(oldMu[0]-mu[0])<tol 
					&& Math.abs(oldMu[1]-mu[1])<tol 
					&& Math.abs(oldSigma[0]-sigma[0])<tol 
					&& Math.abs(oldSigma[1]-sigma[1])<tol) {
				isImproving = false;
			}
			System.out.println(count++);
			displayResult();			
		}
	}
	
	private void stepE() {
		double probx;
		double xi;
		double sumWeights = 0;
		for(int i=0; i<data.size(); i++) {
			xi = data.get(i).doubleValue();
			probx = 0;
			for(int k=0; k<numOfDist; k++) {
				probx += calculateF(xi, mu[k], sigma[k])*distProb[k];				
			}
			weights[i] = calculateF(xi, mu[0], sigma[0]) * sigma[0] / probx;
			sumWeights += weights[i];
		}
		for(int i=0; i<weights.length; i++) {
			weights[i] = weights[i]/sumWeights;
		}
	}
	
	private void stepM() {
		double sumWi = 0;
		double sum1minusWi = 0;
		double sumWiXi = 0;
		double sum1minusWiXi = 0;
		for(int i=0; i<data.size(); i++) {
			sumWi += weights[i];
			sumWiXi += weights[i]*data.get(i).doubleValue();
			sum1minusWi += 1-weights[i];
			sum1minusWiXi += (1-weights[i])*data.get(i).doubleValue();
		}
		
		mu[0] = sumWiXi/sumWi;
		mu[1] = sum1minusWiXi/sum1minusWi;
		
		double a;
		double b;
		double sumWai_sqrda = 0;
		double sumWbi_sqrdb = 0;
		
		for(int i=0; i<data.size(); i++) {
			a = Math.pow(data.get(i).doubleValue()-mu[0], 2);
			sumWai_sqrda += a*weights[i];
			b = Math.pow(data.get(i).doubleValue()-mu[1], 2);
			sumWbi_sqrdb += b*(1-weights[i]);
		}
		sigma[0] = Math.sqrt(sumWai_sqrda/sumWi);
		sigma[1] = Math.sqrt(sumWbi_sqrdb/sum1minusWi);
	}
	
	private double calculateF(double x, double mu, double sigma) {
		return Math.exp(-1*Math.pow(x-mu, 2)/(2*Math.pow(sigma, 2)))/(sigma*Math.sqrt(2*Math.PI));
	}
	
	private double calcLogLikelihood() {
		double result = 0;
		double sum;
		double xi;
		for(int i=0; i<data.size(); i++) {
			xi = data.get(i).doubleValue();
			sum = 0;
			for(int k=0; k<numOfDist; k++) {
				sum += distProb[k]* calculateF(xi, mu[k], sigma[k]);
			}
			result = result+ Math.log(sum);
		}
		return result;
	}
	
	public void displayResult() {
		DecimalFormat mf = new DecimalFormat("00.0000");
		System.out.println("Mean\t" + mf.format(mu[0]) + "\t" + mf.format(mu[1]));
		System.out.println("Vari\t" + mf.format(sigma[0]) + "\t" + mf.format(sigma[1]));
	}
	
//	public static void main(String[] args) throws Exception {
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//System.out.println("Data file to be used is: " + args[0]);

//		int numOfDist;
//		double[] probs;
//		double[] mu;
//		double[] sigma;

//		try {
//			System.out.println("Please enter the number of distributions: ");
//			numOfDist = Integer.parseInt(br.readLine());
//			probs = new double[numOfDist];
//			mu = new double[numOfDist];
//			sigma = new double[numOfDist];
//			for (int i = 0; i < numOfDist; i++) {
//				System.out.println("DISTRIBUTION " + (i+1));
//				System.out.println("Please enter the probability of occurance :");
//				probs[i] = Double.parseDouble(br.readLine());
//				System.out.println("Please enter the starting mean :");
//				mu[i] = Double.parseDouble(br.readLine());
//				System.out.println("Please enter the starting standard deviation :");
//				sigma[i] = Double.parseDouble(br.readLine());
//			}
//			EM obj = new EM("data.txt",					// Data file
//			                2,							// Number of distributions
//			                new double[]{0.5, 0.5},		// Initial probabilities
//			                new double[]{1, 4},			// Distribution means
//			                new double[]{1, 1});		// Distribution variances
			//EM obj = new EM(args[0], numOfDist, probs, mu, sigma);
//			obj.calculateEM();
//			obj.displayResult();
//		} catch (Exception e) {
//
//		} finally {
//			//br.close();
//		}
//	}
}
