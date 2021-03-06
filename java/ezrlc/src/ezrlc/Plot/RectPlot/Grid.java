package ezrlc.Plot.RectPlot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

/**
 * Grid of a rectangular plot
 * 
 * @author noah
 *
 */
public class Grid {
	// ================================================================================
	// Public Data
	// ================================================================================
	public enum Orientation {
		HORIZONTAL, VERTICAL
	};

	// ================================================================================
	// Private Data
	// ================================================================================
	private RectangularPlot parent;
	private Orientation or;
	private Axis ax;
	private Color color;

	private int numLines = 0;

	private int size = 0; // Size of the grid (height or width)

	private GridLine[] lines;

	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Add a new Grid
	 * 
	 * @param parent:
	 *            Parent plot
	 * @param or:
	 *            Orientation of the plot lines, either HORIZONTAL or VERTICAL
	 * @param color:
	 *            Color of the grid
	 * @param ax:
	 *            Parent Axis
	 * @param size:
	 *            Size of the grid, relative to the parent plot (0 is maximum)
	 */
	public Grid(RectangularPlot parent, Orientation or, Color color, Axis ax, int size) {
		this.parent = parent;
		this.or = or;
		this.ax = ax;
		this.size = size;
		this.color = color;
	}

	// ================================================================================
	// Public Functions
	// ================================================================================
	/**
	 * Paints the Grid
	 * 
	 * @param g
	 *            graphics object
	 */
	public void paint(Graphics g) {
		this.evalSize();
		for (GridLine gridLine : this.lines) {
			gridLine.paint(g);
		}
	}

	// ================================================================================
	// Private Functions
	// ================================================================================
	/**
	 * Calculates all the necessary pixel values to paint the plot
	 */
	private void evalSize() {
		List<Point> points = this.ax.getTicPoints();
		this.numLines = points.size();
		int length = 0;

		lines = new GridLine[numLines];
		int ctr = 0;
		if (this.or == Orientation.VERTICAL) {
			length = this.size;
			for (Point point : points) {
				lines[ctr++] = new GridLine(this.color, point, new Point(point.x, length));
			}
		}
		if (this.or == Orientation.HORIZONTAL) {
			length = parent.getWidth() - this.size;
			for (Point point : points) {
				lines[ctr++] = new GridLine(this.color, point, new Point(length, point.y));
			}
		}
	}

}
