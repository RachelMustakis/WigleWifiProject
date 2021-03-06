package project;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * the class is responsible for finding a router location of a router 
 * by an ArrayList of scans
 * 
 * @author Rachel
 */
public class routerLocation {

	/**
	 * the function takes a filename of combined file
	 * and a damaged  filename of combined file 
	 * sends to read the good file
	 * sends to read the damaged file and split it by macs, delete all the doubles
	 * and send it one by one to find the locations of the mac
	 * and then send the whole file to the writer
	 * 
	 * @param filename
	 * @param filenameToFix
	 */
	public  static void location(String filename , int k) {


		CombinedFileReader listToFix=new CombinedFileReader();
		listToFix.readAndSplit(filename);
		ArrayList<WifiScan> list = new ArrayList<WifiScan>();
		
		for (int i = 0; i < listToFix.Lines.size(); i++) 
			list.add(listToFix.Lines.get(i));	

		Writer d = new Writer();
		listToFix.deleteEquals();

		Point3D p;
		for (int i = 0; i < listToFix.Lines.size(); i++) {
			p=location(list, listToFix.Lines.get(i).wifis.get(0).mac,k);
			if (p!=null)
				listToFix.Lines.get(i).setLocation(p);
		}

		String s="";

		// insert the ArrayList to the String
		for (int i = 0; i < listToFix.Lines.size(); i++)
			if (listToFix.Lines.get(i).p!=null)
				s= s+ i + "," + listToFix.Lines.get(i).wifis.get(0).mac 
				+ "," + listToFix.Lines.get(i).wifis.get(0).SSID
				+ "," + listToFix.Lines.get(i).wifis.get(0).frequncy
				+ "," + listToFix.Lines.get(i).wifis.get(0).Signal
				+ "," + listToFix.Lines.get(i).p.latitude 
				+ "," + listToFix.Lines.get(i).p.longtitude
				+ "," + listToFix.Lines.get(i).p.altitude
				+ "," + listToFix.Lines.get(i).date
				+ " " + listToFix.Lines.get(i).time
				+ "," + "Approx. w-center algo1" 
				+ System.lineSeparator();
			else
				s= s+ i + "," + listToFix.Lines.get(i).wifis.get(0).mac 
				+ "," + listToFix.Lines.get(i).wifis.get(0).SSID
				+ "," + listToFix.Lines.get(i).wifis.get(0).frequncy
				+ "," + listToFix.Lines.get(i).wifis.get(0).Signal
				+ ",NaN ,NaN ,NaN "
				+ "," + listToFix.Lines.get(i).date
				+ " " + listToFix.Lines.get(i).time
				+ ",Approx. w-center algo1" 
				+ System.lineSeparator();

		try {

			d.csvWriter(s,"C:\\Users\\Rachel\\Downloads\\study\\OR\\fixedCsvAlgo1.csv"); 
		}
		catch (IOException e) {

			e.printStackTrace();

		} 

	}

	/**
	 * the function takes an ArrayLis of RowsRead and a mac address
	 * and calculate the estimated location of the router 
	 *
	 * @param list
	 * @param macToFind
	 * @param k 
	 * @return
	 */
	public  static Point3D location(ArrayList<WifiScan> list, String macToFind, int k) {
		ArrayList<WifiScan> hasTheMac= new ArrayList<WifiScan>();
		for (int i = 0; i <list.size(); i++) 
			if (list.get(i).wifis.get(0).mac.equals(macToFind))
				hasTheMac.add(list.get(i));
		Collections.sort(hasTheMac);

		int num =Math.min(hasTheMac.size(),k);
		double [][] weight=new double[num+1][4];
		for (int i = 0; i < num; i++) {
			setWeight( hasTheMac.get(i), weight[i]);
		}
		if (num!=0){
			for (int i = 0; i < weight.length; i++) {
				weight[num][0]+=weight[i][0];
				weight[num][1]+=weight[i][1];
				weight[num][2]+=weight[i][2];
				weight[num][3]+=weight[i][3];
			}
			Point3D p=new Point3D(weight[num][1]/weight[num][0],weight[num][2]/weight[num][0], weight[num][3]/weight[num][0]);
			return p;
		}
		else 
			return null;
	}

	/**
	 * the function get a row and calculate the weight of the line
	 * 
	 * @param r
	 * @param weight
	 */
	public static void setWeight(WifiScan r, double [] weightLine){
		weightLine[0]=1/(double)(r.wifis.get(0).Signal*r.wifis.get(0).Signal);			// 1/signal^2
		weightLine[1]=r.p.longtitude*weightLine[0];		// lon*weight
		weightLine[2]=r.p.latitude*weightLine[0];		// lat*weight
		weightLine[3]=r.p.altitude*weightLine[0];		// alt*weight
	}
}
