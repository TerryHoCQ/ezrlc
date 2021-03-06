package ezrlc.ModelCalculation;

import java.util.List;

import ezrlc.util.Complex;

/**
 * Handles polynomial calculations specific for Model calculation
 * 
 * @author noah
 *
 */
public class Polynomial {
	// ================================================================================
	// Private Data
	// ================================================================================

	/**
	 * Stores the coefficients, where [0] is the highest exponent [length - 1]
	 * is ^0
	 */
	private double[] coeffs;

	/**
	 * skin effect enabled or not
	 */
	private boolean skinEnabled = false;

	private double r0, w0, alpha;
	private List<Integer> skinIdx;

	// ================================================================================
	// Constructors
	// ================================================================================

	public Polynomial() {
	}

	/**
	 * Creates a new polynomial with the given coefficients
	 * 
	 * @param coeff
	 *            coefficients where the last one is the lowest exponent ^0
	 */
	public Polynomial(double... coeff) {
		// store coefficients
		coeffs = new double[coeff.length];
		for (int i = 0; i < coeff.length; ++i) {
			coeffs[i] = coeff[i];
		}
	}

	// ================================================================================
	// Private methods
	// ================================================================================
	/**
	 * Returns the skin effect value at a given frequency
	 * 
	 * @param w
	 * @return
	 */
	private double skinAt(double w) {
		return r0 * (1.0 + Math.pow(w / w0, alpha));
	}

	// ================================================================================
	// Public setters and getters
	// ================================================================================
	/**
	 * Sets the skin effect enabled flag
	 * 
	 * @param b
	 *            enabled
	 */
	public final void setSkinEnabled(boolean b) {
		this.skinEnabled = b;
	}

	/**
	 * Returns skinEnabled
	 * 
	 * @return skinEnabled
	 */
	public final boolean getSkinEnabled() {
		return this.skinEnabled;
	}

	public double[] getCoeffs() {
		return coeffs;
	}

	/**
	 * store coefficients
	 * 
	 * @param coeff
	 *            coefficients
	 */
	public void setCoeffs(double... coeff) {
		// store coefficients
		coeffs = new double[coeff.length];
		for (int i = 0; i < coeff.length; ++i) {
			coeffs[i] = coeff[i];
		}
	}

	public int getLength() {
		return coeffs.length;
	}

	// ================================================================================
	// Public Functions
	// ================================================================================

	/**
	 * Evaluates the polynomial at the given x WITHOUT skin
	 * 
	 * @param x
	 *            eval location
	 * @return complex result
	 */
	public final Complex polyval(Complex x) {
		Complex val = new Complex(0, 0);
		for (int i = 0; i < coeffs.length; i++) {
			val = x.pow(i).times(coeffs[coeffs.length - 1 - i]).plus(val);
		}
		return val;
	}

	/**
	 * Evaluates the polynomial at the given w[omega] vector with skin efffect
	 * enabled if available
	 * 
	 * @param w
	 *            frequencz in omega = 2*pi*f
	 * @return value
	 */
	public final Complex[] polyval(double[] w) {
		double d = 1;
		Complex x, val;
		Complex[] vals = new Complex[w.length];

		// for every frequency
		for (int j = 0; j < w.length; j++) {
			x = new Complex(0, w[j]);
			val = new Complex(0, 0);
			// for every coefficient
			for (int i = 0; i < coeffs.length; i++) {
				d = 1;
				if (this.skinEnabled) {
					if (skinIdx.contains(coeffs.length - 1 - i)) {
						// if skin is enabled, multiply the skin effect to the
						// coeff
						d = skinAt(w[j]);
					}
				}
				val = x.pow(i).times((coeffs[coeffs.length - 1 - i]) * d).plus(val);
			}
			vals[j] = new Complex(val);
		}
		return vals;
	}

	/**
	 * Evaluates two polynomials at the given x and divides by p
	 * 
	 * @param p
	 *            divide this by p
	 * @param x
	 *            location
	 * @return complex result this/p at x
	 */
	public final Complex polydiv(Polynomial p, Complex x) {
		Complex n = this.polyval(x);
		Complex d = p.polyval(x);
		Complex res = n.divides(d);
		return res;
	}

	/**
	 * Returns a list of divisions of this by n at w
	 * 
	 * @param d
	 *            denumerator polynomial
	 * @param w
	 *            frequency vector in omega
	 * @return complex result this/p at x
	 */
	public final Complex[] polydiv(Polynomial d, double[] w) {
		Complex[] res = new Complex[w.length];
		// get list of both polynomials
		Complex[] yn = this.polyval(w);
		Complex[] yd = d.polyval(w);
		// divide the results and add them to the list
		for (int i = 0; i < w.length; i++) {
			res[i] = yn[i].divides(yd[i]);
		}
		return res;
	}

}
