package dm.association.freqpatterns;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SimpleApriori {
//	public static void main(String[] args) {
//		SimpleApriori ap = new SimpleApriori(args);
//		ap.output();
//	}

	private String infileName;
	private String outfileName;
	private int minSup; // Used as absolute support. NOT relative support
	private int numTransactions;
	private int numItems;
	long start;
	long end;
	private HashMap<Integer, ArrayList<ArrayList<Integer>>> itemsetsLevel;
	private HashMap<Integer, ArrayList<Integer>> supportCount;
	private ArrayList<ArrayList<Integer>> transactions;

	public SimpleApriori(String[] args) {
		numTransactions = 0;
		numItems = 0;
		transactions = new ArrayList<ArrayList<Integer>>();
		itemsetsLevel = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
		supportCount = new HashMap<Integer, ArrayList<Integer>>();
		setup(args);
		startApriori();
	}

	// Methods
	private void setup(String[] args) {
		FileReader file = null;
		BufferedReader reader = null;
		String item, line;
		int commaIdx;
		ArrayList<Integer> currTransaction;
		double support;

		if (args.length != 0) {
			infileName = args[0];
		} else {
			infileName = "../../data/Apriori/dmbookexample.csv";
		}

		if (args.length > 1) {
			support = Double.valueOf(args[1]).doubleValue();
		} else {
			support = 0.22;
		}
		int extDotIdx = infileName.lastIndexOf(".");
		outfileName = infileName.substring(0, extDotIdx) + " " + support
		              + ".out";

		// Read in file and read the transactions into the ArrayList
		try {
			file = new FileReader(infileName);
			reader = new BufferedReader(file);
			while ((line = reader.readLine()) != null) {
				if (line.matches("\\s*")) {
					continue;
				}
				line += ",";
				numTransactions++;
				currTransaction = new ArrayList<Integer>();
				for (int i = 0, num = 0; i < line.length(); i++) {
					commaIdx = line.indexOf(",", i);
					item = line.substring(i, commaIdx);

					if (item.equals("1")) {
						currTransaction.add(num++);
					} else if (item.equals("0")) {
						num++;
					}
				}
				transactions.add(currTransaction);
				// Crude calculation of numItems
				numItems = line.length() / 2;
			}
			minSup = (int) Math.round(support * numTransactions);
			reader.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (Exception e) {
					// Ignore issues during closing
				}
			}
		}
	}

	private void startApriori() {
		// start timing
		start = System.currentTimeMillis();
		long startOfLevel;
		long endOfLevel;

		// Create 1-itemsets
		// This function is the starting point of the Apriori process so it uses
		// constant (zero) in function calls for calculation of support and
		// pruning etc.
		startOfLevel = System.currentTimeMillis();
		createOne_Itemsets();
		endOfLevel = System.currentTimeMillis();
		System.out.println("Execution time (1-itemsets):\t"
		                   + ((double) (endOfLevel - startOfLevel) / 1000)
		                   + " seconds");

		for (int k = 1; !itemsetsLevel.get(k - 1).isEmpty(); k++) {
			startOfLevel = System.currentTimeMillis();
			generateApriori(itemsetsLevel.get(k - 1), k);
			calculate_Support(k);
			pruneInfrequent(k);
			endOfLevel = System.currentTimeMillis();
			System.out.println("Execution time (" + (k + 1) + "-itemsets):\t"
			                   + ((double) (endOfLevel - startOfLevel) / 1000)
			                   + " seconds");
		}

		end = System.currentTimeMillis();
	}

	private void generateApriori(ArrayList<ArrayList<Integer>> lvlK_1, int level) {
		ArrayList<ArrayList<Integer>> candidate_K = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> temp; // Currently created k-itemset
		int sz_lvlK_1 = lvlK_1.size(); // Size of level (k-1)-itemsets
		int sz_li; // Size of list i
		int sz_lj; // Size of list j

		// List "i" and "j" contain the ith and jth (k-1)-itemset
		List<Integer> li;
		List<Integer> lj;

		for (int i = 0; i < sz_lvlK_1; i++) {
			li = lvlK_1.get(i);
			sz_li = li.size();
			for (int j = i + 1; j < sz_lvlK_1; j++) {
				lj = lvlK_1.get(j);
				sz_lj = lj.size();

				if (li.subList(0, sz_li - 1).containsAll(lj.subList(0,
				                                                    sz_lj - 1))) {
					if (li.get(sz_li - 1) < lj.get(sz_lj - 1)) {
						temp = new ArrayList<Integer>();
						temp.addAll(li);
						temp.addAll(lj.subList(sz_lj - 1, sz_lj));
						if (hasInfrequentSubset(temp, lvlK_1)) {
							continue;
						} else {
							candidate_K.add(candidate_K.size(), temp);
						}
					}
				}
			}
		}

		// if (level == 1) {
		// for (int i = 0; i < sz_lvlK_1; i++) {
		// li = lvlK_1.get(i);
		// sz_li = li.size();
		// for (int j = i + 1; j < sz_lvlK_1; j++) {
		// lj = lvlK_1.get(j);
		// sz_lj = lj.size();
		// temp = new ArrayList<Integer>();
		// temp.add(li.get(0));
		// temp.add(lj.get(0));
		// if (hasInfrequentSubset(temp, lvlK_1)) {
		// continue;
		// } else {
		// candidate_K.add(candidate_K.size(), temp);
		// } } }
		// } else {
		// for (int i = 0; i < sz_lvlK_1; i++) {
		// li = lvlK_1.get(i);
		// sz_li = li.size();
		// for (int j = i + 1; j < sz_lvlK_1; j++) {
		// lj = lvlK_1.get(j);
		// sz_lj = lj.size();
		// if (li.subList(0, sz_li - 1).containsAll(lj.subList(0, sz_lj - 1))) {
		// if (li.get(sz_li - 1) < lj.get(sz_lj - 1)) {
		// temp = new ArrayList<Integer>();
		// temp.addAll(li);
		// temp.addAll(lj.subList(sz_lj - 1, sz_lj));
		// if (hasInfrequentSubset(temp, lvlK_1)) {
		// continue;
		// } else {
		// candidate_K.add(candidate_K.size(), temp);
		// } } } } } }

		itemsetsLevel.put(level, candidate_K);
	}

	// OPTIMIZED Functions
	private boolean hasInfrequentSubset(ArrayList<Integer> c,
	                                    ArrayList<ArrayList<Integer>> lvlK_1) {
		// Create ALL k-1 subsets of c
		ArrayList<ArrayList<Integer>> s = new ArrayList<ArrayList<Integer>>();

		// Remove the ith element from the k-itemset and we get all k-1 subsets
		for (Integer i : c) {
			ArrayList<Integer> temp = new ArrayList<Integer>(c);
			temp.remove(i);
			s.add(temp);
		}

		// Check if ALL k-1 subsets of c are present in Level k-1 item set
		for (ArrayList<Integer> a : s) {
			if (!lvlK_1.contains(a)) { // s does not belong to Level k-1
				return true;
			}
		}
		return false;
	}

	private void createOne_Itemsets() {
		ArrayList<ArrayList<Integer>> lvlOneItemsets = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < numItems; i++) {
			// Add a single copy of i in the ith location to generate 1-itemsets
			lvlOneItemsets.add(i,
			                   new ArrayList<Integer>(Collections.nCopies(1, i)));
		}

		// This is the generation and calculation for the Level 1-itemsets
		// So we pass the value of zero (in accordance with Java indices)
		int level = 0;
		itemsetsLevel.put(level, lvlOneItemsets);
		calculate_Support(level);
		pruneInfrequent(level);
	}

	private void calculate_Support(int lvl) {
		boolean match;

		supportCount.put(lvl,
		                 new ArrayList<Integer>(
		                                        Collections.nCopies(itemsetsLevel.get(lvl)
		                                                                         .size(),
		                                                            0)));
		// The above statement is equivalent to the following loop
		// it adds zeroes equal to the size of the itemsets at level "lvl"
		// afterwards each value can hold the support count of the corresponding
		// itemset
		// for(int i=0; i<itemsetsLevel.get(lvl).size(); i++)
		// supportCount.get(lvl).add(0);

		for (int trNum = 0; trNum < transactions.size(); trNum++) {
			for (int itNum = 0; itNum < itemsetsLevel.get(lvl).size(); itNum++) {
				match = true;
				for (Integer item : itemsetsLevel.get(lvl).get(itNum)) {
					if (!transactions.get(trNum).contains(item)) {
						match = false;
						break;
					}
				}
				if (match) {
					supportCount.get(lvl)
					            .set(itNum,
					                 supportCount.get(lvl).get(itNum) + 1);
				}
			}
		}
	}

	private void pruneInfrequent(int level) {
		ArrayList<Integer> removalList = new ArrayList<Integer>();
		for (int i = 0; i < itemsetsLevel.get(level).size(); i++) {
			if (supportCount.get(level).get(i) < minSup) {
				removalList.add(i);
			}
		}

		for (int index = removalList.size() - 1; index >= 0; index--) {
			supportCount.get(level).remove((int) removalList.get(index));
			itemsetsLevel.get(level).remove((int) removalList.get(index));
		}
	}

	public void output() {
		StringBuilder sb = new StringBuilder();
		sb.append("File Processed:\t\t" + infileName + "\n");
		sb.append("Total Items:\t\t" + numItems + "\n");
		sb.append("Total Transactions:\t" + numTransactions + "\n");
		sb.append("Relative Support:\t" + (double) minSup / numTransactions
		          + "\n");
		sb.append("Absolute Support:\t" + minSup + "\n");
		sb.append("Execution time:\t" + ((double) (end - start) / 1000)
		          + " seconds\n\n");
		int count = 0;
		DecimalFormat mf = new DecimalFormat("#.###");
		ArrayList<ArrayList<Integer>> itemset_i;
		ArrayList<Integer> support_i;
		for (int i = 0; i < itemsetsLevel.size(); i++) {
			itemset_i = itemsetsLevel.get(i);
			support_i = supportCount.get(i);
			sb.append(itemset_i.size() + " Frequent " + (i + 1)
			          + "-itemsets discovered\n");
			sb.append("======================================\n");
			sb.append("Support\tItemset\n");
			count += itemset_i.size();
			for (int j = 0; j < itemset_i.size(); j++) {
				for (int k = 0; k < itemset_i.get(j).size(); k++) {
					itemset_i.get(j).set(k, itemset_i.get(j).get(k) + 1);
				}
				sb.append(mf.format((double) support_i.get(j) / numTransactions)
				          + "\t" + itemset_i.get(j) + "\n");
			}
			sb.append("\n");
		}
		sb.append("Total itemsets:\t" + count + "\n");
		System.out.print(sb);
		try {
			FileWriter fw = new FileWriter(outfileName);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(sb.toString());
			bw.close();
		} catch (Exception e) {
			System.out.print("Exception in FileWrite");
		}

	}
}
