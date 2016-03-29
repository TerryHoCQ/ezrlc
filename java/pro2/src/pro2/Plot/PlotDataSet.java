package pro2.Plot;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import pro2.util.MathUtil;

public class PlotDataSet {

	//================================================================================
    // Private Data
    //================================================================================
	private List<Double> x_data;
	private List<Double> y_data;
	private int points = 0;
	private double x_max;
	private double x_min;
	private double y_max;
	private double y_min;
	private double x_span;
	private double y_span;
	
	private Axis x_axis;
	private Axis y_axis;
	
	private List<Point> data_pts;
	

	//================================================================================
    // Constructors
    //================================================================================
	/**
	 * Add new Dataset. Both parameters must be of the same size!!
	 * @param x: X Data
	 * @param y: Y Data
	 */
	public PlotDataSet(List<Double> x,List<Double> y) {
		if(x.size() != y.size()) {
			System.out.println("PlotDataSet: Error! Data not the same size");
			return;
		}
		this.x_data = x;
		this.y_data = y;
		this.points = x.size();
		this.data_pts = new ArrayList<Point>(this.points);
		for(int i =0; i < this.points; i++) {
			this.data_pts.add(new Point(0,0));
		}
		
		// Get the datas max and min
		x_max = MathUtil.getMax(this.x_data);
		x_min = MathUtil.getMin(this.x_data);
		y_max = MathUtil.getMax(this.y_data);
		y_min = MathUtil.getMin(this.y_data);

		this.x_span = this.x_max - this.x_min;
		if(this.x_span == 0) this.x_span = Double.MIN_VALUE;
		this.y_span = this.y_max - this.y_min;
		if(this.y_span == 0) this.y_span = Double.MIN_VALUE;
		
		System.out.println("New Dataset added. x_min:" +this.x_min +" x_max:" +this.x_max +" y_min:" +this.y_min +" y_max:" +this.y_max);
	}

	//================================================================================
    // Private Functions
    //================================================================================
	/**
	 * Evaluates the pixel positions of the individual datapoints
	 */
	private void eval() {
		int i = 0;
		for (Point point : this.data_pts) {
			point.x = this.x_axis.getPixelValue(this.x_data.get(i));
			point.y = this.y_axis.getPixelValue(this.y_data.get(i));
			i++;
		}
	}
	
	/** 
	 * Draws a data point at the given position
	 * @param g
	 * @param pt
	 */
	private void drawPoint (Graphics g, Point pt) {
		g.drawOval(pt.x-1, pt.y-1, 2, 2);
	}
	
	/**
	 * Connects the two datapoints with a line
	 * @param g
	 * @param a
	 * @param b
	 */
	private void connectPoints(Graphics g, Point a, Point b) {
		g.drawLine(a.x, a.y, b.x, b.y);
	}
	
	//================================================================================
    // Public Functions
    //================================================================================
	/**
	 * Paint the Dataset
	 * @param g
	 */
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		this.eval();
		
//		for (Point point : this.data_pts) {
//			this.drawPoint(g, point);
//		}
//		
		for(int i = 0; i<(this.points-1); i++) {
			this.drawPoint(g, this.data_pts.get(i));
			this.connectPoints(g, this.data_pts.get(i), this.data_pts.get(i+1));
		}

		this.drawPoint(g, this.data_pts.get(this.points-1));
	}
	
	/**
	 * Set the parent Axis of the dataset
	 * @param x: x Axis
	 * @param y: y Axis
	 */
	public void setAxis(Axis x, Axis y) {
		this.x_axis=x;
		this.y_axis=y;
	}
	
	

}