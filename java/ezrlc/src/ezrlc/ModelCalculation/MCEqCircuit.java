package ezrlc.ModelCalculation;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;

import ezrlc.RFData.RFData;
import ezrlc.util.Complex;

/**
 * Model Calculation Equivalent Circuit
 * 
 * Represents an equivalent circuit model
 * 
 * Calculates the frequency-response of a model Parameter array format
 * 
 * R0 f0 alpha R1 L C0 C1 [0] [1] [2] [3] [4] [5] [6]
 * 
 * @author noah
 *
 */
public class MCEqCircuit implements Runnable {

	// ================================================================================
	// Public Data
	// ================================================================================
	public enum CircuitType {
		MODEL0, MODEL1, MODEL2, MODEL3, MODEL4, MODEL5, MODEL6, MODEL7, MODEL8, MODEL9, MODEL10, MODEL11, MODEL12, MODEL13, MODEL14, MODEL15, MODEL16, MODEL17, MODEL18, MODEL19, MODEL20
	}

	// ================================================================================
	// Private Data
	// ================================================================================
	private CircuitType circuitType;
	private MCOptions ops;

	private double[] parameters;
	private double[] shortParameters;

	private double[] wvector;

	private double z0;

	// Used for threadded optimizing
	private Complex[] ys;

	private double optStepDefault = 0.001;
	// private double optRelThDefault = 1e-11;
	// private double optAbsThDefault = 1e-14;
	private double optRelThDefault = 1e-12;
	private double optAbsThDefault = 1e-15;

	private SimplexOptimizer optimizer;
	private PointValuePair optimum;
	private MCErrorSum errorFunction;
	private double[] optStep;

	private double small = Math.pow(10, -50);

	// ================================================================================
	// Constructor
	// ================================================================================
	/**
	 * Creates a new Equivalent Circuit
	 * 
	 * @param circuitType
	 *            circuit type of the circuit
	 */
	public MCEqCircuit(CircuitType circuitType) {
		this.circuitType = circuitType;
		initOptimizer();
		parameters = new double[7];
		for (int i = 0; i < 7; i++) {
			parameters[i] = 0.0;
		}
	}

	/**
	 * Creates a new Equivalent Circuit and assigns parameters
	 * 
	 * @param circuitType
	 *            circuit type of the circuit
	 * @param params
	 *            parameter array
	 */
	public MCEqCircuit(CircuitType circuitType, double[] params) {
		this.circuitType = circuitType;
		initOptimizer();
		parameters = new double[7];
		System.arraycopy(params, 0, this.parameters, 0, params.length);
	}

	// ================================================================================
	// Getter and Setter Functions
	// ================================================================================
	/**
	 * Set the frequency vector in omega
	 * 
	 * @param w
	 *            w vecotr
	 */
	public final void setWVector(double[] w) {
		this.wvector = new double[w.length];
		System.arraycopy(w, 0, this.wvector, 0, w.length);
	}

	/**
	 * Sets the parameter array of the circuit
	 * 
	 * @param params
	 *            Parameter array [7]
	 */
	public final void setParameters(double[] params) {
		System.arraycopy(params, 0, parameters, 0, params.length);
	}

	/**
	 * Sets a single parameter in the parameter list
	 * 
	 * @param i
	 *            index of parameter
	 * @param d
	 *            parameter value
	 */
	public void setParameter(int i, double d) {
		parameters[i] = d;
	}

	/**
	 * Returns a copy of the circuit parameters
	 * 
	 * @return parameter array
	 */
	public double[] getParameters() {
		double[] res = new double[7];
		System.arraycopy(parameters, 0, res, 0, 7);
		return res;
	}

	public CircuitType getCircuitType() {
		return this.circuitType;
	}

	public void setZ0(double rref) {
		this.z0 = rref;
	}

	public double getZ0() {
		return z0;
	}

	public void setOps(MCOptions ops) {
		this.ops = ops;
	}

	public MCOptions getOps() {
		return ops;
	}

	// ================================================================================
	// Public Functions
	// ================================================================================

	/**
	 * Returns a copy of the stored frequency vector
	 * 
	 * @return frequency vector
	 */
	public double[] getF() {
		double[] res = new double[wvector.length];
		for (int i = 0; i < wvector.length; i++) {
			res[i] = wvector[i] / (2.0 * Math.PI);
		}
		return res;
	}

	/**
	 * Returns the scattering parameters to the stored freq parameters
	 * 
	 * @return Complex array with scattering parameters
	 */
	public final Complex[] getS() {
		// convert to s parameter
		Complex[] ys = RFData.z2s(z0, this.getZ());

		return ys;
	}

	/**
	 * Returns the scattering parameters to the given freq parameters and given
	 * zref
	 * 
	 * @param zref
	 *            reference resistance
	 * @return Complex array with scattering parameters
	 */
	public final Complex[] getS(double zref) {
		// convert to s parameter
		Complex[] ys = RFData.z2s(zref, this.getZ());

		return ys;
	}

	/**
	 * Returns the admittance parameters to the given freq parameters
	 * 
	 * @return Complex array with admittance parameters
	 */
	public final Complex[] getY() {
		// convert to s parameter
		Complex[] yy = RFData.z2y(this.getZ());

		return yy;
	}

	/**
	 * Returns the impedance parameters to the given freq parameters
	 * 
	 * @return Complex array with impedance parameters
	 */
	public final Complex[] getZ() {
		Complex[] yz = null;

		switch (this.circuitType) {
		case MODEL0:
			yz = model0();
			break;
		case MODEL1:
			yz = model1();
			break;
		case MODEL2:
			yz = model2();
			break;
		case MODEL3:
			yz = model3();
			break;
		case MODEL4:
			yz = model4();
			break;
		case MODEL5:
			yz = model5();
			break;
		case MODEL6:
			yz = model6();
			break;
		case MODEL7:
			yz = model7();
			break;
		case MODEL8:
			yz = model8();
			break;
		case MODEL9:
			yz = model9();
			break;
		case MODEL10:
			yz = model10();
			break;
		case MODEL11:
			yz = model11();
			break;
		case MODEL12:
			yz = model12();
			break;
		case MODEL13:
			yz = model13();
			break;
		case MODEL14:
			yz = model14();
			break;
		case MODEL15:
			yz = model15();
			break;
		case MODEL16:
			yz = model16();
			break;
		case MODEL17:
			yz = model17();
			break;
		case MODEL18:
			yz = model18();
			break;
		case MODEL19:
			yz = model19();
			break;
		case MODEL20:
			yz = model20();
			break;
		default:
			System.err.println("FATAL: Model idx not found");
			;
		}
		return yz;
	}

	/**
	 * Returns the size of the w vector
	 * 
	 * @return size of w vector
	 */
	public int getWSize() {
		return this.wvector.length;
	}

	/**
	 * Sets all unused parameters to zero
	 */
	public void clean() {
		switch (this.circuitType) {
		case MODEL0:
		case MODEL1:
			parameters[1] = 0.0;
			parameters[2] = 0.0;
			parameters[3] = 0.0;
			parameters[5] = 0.0;
			parameters[6] = 0.0;
			break;
		case MODEL2:
		case MODEL3:
			parameters[1] = 0.0;
			parameters[2] = 0.0;
			parameters[3] = 0.0;
			parameters[4] = 0.0;
			parameters[6] = 0.0;
			break;
		case MODEL4:
		case MODEL5:
		case MODEL6:
		case MODEL7:
		case MODEL8:
			parameters[1] = 0.0;
			parameters[2] = 0.0;
			parameters[3] = 0.0;
			parameters[6] = 0.0;
			break;
		case MODEL9:
		case MODEL10:
		case MODEL11:
			parameters[1] = 0.0;
			parameters[2] = 0.0;
			parameters[6] = 0.0;
			break;
		case MODEL12:
			parameters[1] = 0.0;
			parameters[2] = 0.0;
			parameters[3] = 0.0;
			break;
		case MODEL13:
		case MODEL14:
		case MODEL15:
			parameters[3] = 0.0;
			parameters[6] = 0.0;
			break;
		case MODEL16:
			parameters[4] = 0.0;
			parameters[6] = 0.0;
			break;
		case MODEL17:
		case MODEL18:
		case MODEL19:
			parameters[6] = 0.0;
			break;
		case MODEL20:
			parameters[3] = 0.0;
			break;
		}
	}

	/**
	 * Prints the stored parameters to syso
	 */
	public void printParameters() {
		double[] res = parameters;
		System.out.println("-------------------------");
		System.out.println(this.circuitType.toString() + " Results");
		System.out.println("-------------------------");
		System.out.println("R0= " + res[0]);
		System.out.println("f0= " + res[1]);
		System.out.println("a = " + res[2]);
		System.out.println("R1= " + res[3]);
		System.out.println("L = " + res[4]);
		System.out.println("C0= " + res[5]);
		System.out.println("C1= " + res[6]);
		System.out.println("-------------------------");
	}

	/**
	 * Optimizes the circuit to the given ys vector
	 * 
	 * @param ys
	 *            complex scattering parameters to which the model is optimized
	 */
	public void optimize(Complex[] ys) {
		// shorten parameters to optimize
		shortParameters = MCUtil.shortenParam(circuitType, parameters);
		errorFunction = new MCErrorSum(ys, this);
		optimum = null;
		try {
			optimum = optimizer.optimize(new MaxEval(10000), new ObjectiveFunction(errorFunction), GoalType.MINIMIZE,
					new InitialGuess(shortParameters), new NelderMeadSimplex(optStep));
			parameters = MCUtil.topo2Param(circuitType, optimum.getPoint());
		} catch (TooManyEvaluationsException ex) {
			// This exception can be ignored. If max eval is reached, the recent
			// parameters are stored
			// and no null pointer can appear
			parameters = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
		}
		// save new parameters
	}

	/**
	 * Optimizes the circuit to the given ys vector in a threaded operation
	 * Usage: eqc.optimizeThreaded(ys); Thread t2_1 = new Thread(eqc,
	 * "EQC-Thread-t2_1"); t2_1.run();
	 * 
	 * @param ys
	 *            complex scattering parameters to which the model is optimized
	 */
	public void optimizeThreaded(Complex[] ys) {
		this.ys = ys;
	}

	/**
	 * Run threaded optimizing
	 */
	@Override
	public void run() {
		optimize(ys);
	}

	// ================================================================================
	// Private Functions
	// ================================================================================
	/**
	 * Inits the optimizer
	 */
	private void initOptimizer() {
		int nelements = MCUtil.modelNParameters[this.circuitType.ordinal()];
		optStep = new double[nelements];
		for (int i = 0; i < nelements; i++) {
			optStep[i] = optStepDefault;
		}
		optimizer = new SimplexOptimizer(optRelThDefault, optAbsThDefault);
	}

	/**
	 * Calculates the impedance parameters of the model 0
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model0() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, 0, p[4] + small, p[0] + small);
		Polynomial pd = new Polynomial(0, 0, 0, 1);
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model1
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model1() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, 0, (p[4] + small) * (p[0] + small), 0);
		Polynomial pd = new Polynomial(0, 0, (p[4] + small), (p[0] + small));
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 2
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model2() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, 0, (p[5] + small) * (p[0] + small), 1);
		Polynomial pd = new Polynomial(0, 0, (p[5] + small), 0);
		Complex[] res;
		res = pn.polydiv(pd, wvector);

		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 3
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model3() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, 0, 0, (p[0] + small));
		Polynomial pd = new Polynomial(0, 0, (p[5] + small) * (p[0] + small), 1);
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 4
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model4() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, (p[4] + small) * (p[5] + small), (p[0] + small) * (p[5] + small), 1);
		Polynomial pd = new Polynomial(0, 0, (p[5] + small), 0);
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 5
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model5() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, 0, (p[0] + small) * (p[4] + small), 0);
		Polynomial pd = new Polynomial(0, (p[5] + small) * (p[4] + small) * (p[0] + small), (p[4] + small),
				(p[0] + small));
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 6
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model6() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, 0, (p[4] + small), (p[0] + small));
		Polynomial pd = new Polynomial(0, (p[5] + small) * (p[4] + small), (p[5] + small) * (p[0] + small), 1);
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 7
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model7() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, (p[5] + small) * (p[4] + small) * (p[0] + small), (p[4] + small),
				(p[0] + small));
		Polynomial pd = new Polynomial(0, 0, (p[5] + small) * (p[0] + small), 1);
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 8
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model8() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, 0, (p[5] + small) * (p[0] + small) * (p[3] + small),
				(p[0] + small) + (p[3] + small));
		Polynomial pd = new Polynomial(0, 0, (p[5] + small) * (p[0] + small), 1);
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 9
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model9() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, (p[4] + small) * (p[5] + small) * (p[3] + small),
				(p[4] + small) + (p[5] + small) * (p[3] + small) * (p[0] + small), (p[0] + small) + (p[3] + small));
		Polynomial pd = new Polynomial(0, 0, (p[5] + small) * (p[3] + small), 1);
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 10
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model10() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, (p[5] + small) * (p[4] + small) * (p[0] + small) * (p[3] + small),
				(p[4] + small) * (p[0] + small) + (p[4] + small) * (p[3] + small), (p[3] + small) * (p[0] + small));
		Polynomial pd = new Polynomial(0, (p[5] + small) * (p[4] + small) * (p[0] + small), (p[4] + small),
				(p[0] + small));
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 11
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model11() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, 0, (p[4] + small) * (p[0] + small), (p[3] + small) * (p[0] + small));
		Polynomial pd = new Polynomial(0, (p[5] + small) * (p[4] + small) * (p[0] + small),
				(p[5] + small) * (p[3] + small) * (p[0] + small) + (p[4] + small), (p[3] + small) + (p[0] + small));
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 12
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model12() {
		double[] p = this.parameters;
		Polynomial pn = new Polynomial(0, (p[4] + small) * (p[6] + small), (p[0] + small) * (p[6] + small), 1);
		Polynomial pd = new Polynomial((p[5] + small) * (p[6] + small) * (p[4] + small),
				(p[5] + small) * (p[6] + small) * (p[0] + small), (p[5] + small) + (p[6] + small), 0);
		Complex[] res;
		res = pn.polydiv(pd, wvector);
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 13
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model13() {
		double r0 = this.parameters[0] + small;
		double w0 = (this.parameters[1] * 2 * Math.PI) + small;
		double a = this.parameters[2] + small;
		double l = this.parameters[4] + small;
		double c0 = this.parameters[5] + small;
		Complex[] res = new Complex[wvector.length];
		for (int i = 0; i < wvector.length; i++) {
			Complex Zc0 = new Complex(0, -1 / (wvector[i] * c0));
			Complex Zl = new Complex(0, wvector[i] * l);
			Complex Zr0 = new Complex(r0 * (1 + Math.pow(wvector[i] / w0, a)), 0);
			res[i] = Complex.add(Zr0, Complex.add(Zc0, Zl));
		}
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 14
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model14() {
		double r0 = this.parameters[0] + small;
		double w0 = (this.parameters[1] * 2 * Math.PI) + small;
		double a = this.parameters[2] + small;
		double l = this.parameters[4] + small;
		double c0 = this.parameters[5] + small;
		Complex[] res = new Complex[wvector.length];
		for (int i = 0; i < wvector.length; i++) {
			Complex Zc0 = new Complex(0, -1 / (wvector[i] * c0));
			Complex Zl = new Complex(0, wvector[i] * l);
			Complex Zr0 = new Complex(r0 * (1 + Math.pow(wvector[i] / w0, a)), 0);

			Complex Yc0 = Zc0.reciprocal();
			res[i] = Complex.div(new Complex(1, 0),
					Complex.add(Complex.div(new Complex(1, 0), Complex.add(Zl, Zr0)), Yc0));
		}
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 15
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model15() {
		double r0 = this.parameters[0] + small;
		double w0 = (this.parameters[1] * 2 * Math.PI) + small;
		double a = this.parameters[2] + small;
		double l = this.parameters[4] + small;
		double c0 = this.parameters[5] + small;
		Complex[] res = new Complex[wvector.length];
		for (int i = 0; i < wvector.length; i++) {
			Complex Zc0 = new Complex(0, -1 / (wvector[i] * c0));
			Complex Yc0 = Zc0.reciprocal();
			Complex Zl = new Complex(0, wvector[i] * l);
			Complex Zr0 = new Complex(r0 * (1 + Math.pow(wvector[i] / w0, a)), 0);
			res[i] = Complex.add(Complex.div(new Complex(1, 0), Complex.add(Yc0, Complex.div(new Complex(1, 0), Zr0))),
					Zl);
		}
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 16
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model16() {
		double r0 = this.parameters[0] + small;
		double w0 = (this.parameters[1] * 2 * Math.PI) + small;
		double a = this.parameters[2] + small;
		double r1 = this.parameters[3] + small;
		double c0 = this.parameters[5] + small;
		Complex[] res = new Complex[wvector.length];
		for (int i = 0; i < wvector.length; i++) {
			Complex Zc0 = new Complex(0, -1 / (wvector[i] * c0));
			Complex Zr0 = new Complex(r0 * (1 + Math.pow(wvector[i] / w0, a)), 0);
			Complex Yc0 = Zc0.reciprocal();
			res[i] = Complex.add(Complex.div(new Complex(1, 0), Complex.add(Yc0, new Complex(1 / r1, 0))), Zr0);
		}
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 17
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model17() {
		double r0 = this.parameters[0] + small;
		double w0 = (this.parameters[1] * 2 * Math.PI) + small;
		double a = this.parameters[2] + small;
		double r1 = this.parameters[3] + small;
		double l = this.parameters[4] + small;
		double c0 = this.parameters[5] + small;
		Complex[] res = new Complex[wvector.length];
		for (int i = 0; i < wvector.length; i++) {
			Complex Zc0 = new Complex(0, -1 / (wvector[i] * c0));
			Complex Yc0 = Zc0.reciprocal();
			Complex Zl = new Complex(0, wvector[i] * l);
			Complex Zr0 = new Complex(r0 * (1 + Math.pow(wvector[i] / w0, a)), 0);
			res[i] = Complex.add(
					Complex.add(Complex.div(new Complex(1, 0), Complex.add(Yc0, new Complex(1 / r1, 0))), Zr0), Zl);
		}
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 18
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model18() {
		double r0 = this.parameters[0] + small;
		double w0 = (this.parameters[1] * 2 * Math.PI) + small;
		double a = this.parameters[2] + small;
		double r1 = this.parameters[3] + small;
		double l = this.parameters[4] + small;
		double c0 = this.parameters[5] + small;
		Complex[] res = new Complex[wvector.length];
		for (int i = 0; i < wvector.length; i++) {
			Complex Zc0 = new Complex(0, -1 / (wvector[i] * c0));
			Complex Zl = new Complex(0, wvector[i] * l);
			Complex Zr1 = new Complex(r1 * (1 + Math.pow(wvector[i] / w0, a)), 0);

			Complex Yc0 = Zc0.reciprocal();
			Complex Yl = Zl.reciprocal();
			res[i] = Complex.add(
					Complex.div(new Complex(1, 0), Complex.add(Complex.add(new Complex(1 / r0, 0), Yl), Yc0)), Zr1);
		}
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 19
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model19() {
		double r0 = this.parameters[0] + small;
		double w0 = (this.parameters[1] * 2 * Math.PI) + small;
		double a = this.parameters[2] + small;
		double r1 = this.parameters[3] + small;
		double l = this.parameters[4] + small;
		double c0 = this.parameters[5] + small;
		Complex[] res = new Complex[wvector.length];
		for (int i = 0; i < wvector.length; i++) {
			Complex Zc0 = new Complex(0, -1 / (wvector[i] * c0));
			Complex Zl = new Complex(0, wvector[i] * l);
			Complex Zr1 = new Complex(r1 * (1 + Math.pow(wvector[i] / w0, a)), 0);
			Complex Yc0 = Zc0.reciprocal();
			res[i] = Complex.div(new Complex(1, 0), Complex.add(
					Complex.add(new Complex(1 / r0, 0), Complex.div(new Complex(1, 0), Complex.add(Zl, Zr1))), Yc0));
		}
		return res;
	}

	/**
	 * Calculates the impedance parameters of the model 20
	 * 
	 * @return impedance parameters
	 */
	private Complex[] model20() {
		double w0 = (this.parameters[1] * 2 * Math.PI) + small;
		double a = this.parameters[2] + small;
		double r0 = this.parameters[0] + small;
		double l = this.parameters[4] + small;
		double c0 = this.parameters[5] + small;
		double c1 = this.parameters[6] + small;
		Complex[] res = new Complex[wvector.length];
		for (int i = 0; i < wvector.length; i++) {
			Complex Zc0 = new Complex(0, -1 / (wvector[i] * c0));
			Complex Zc1 = new Complex(0, -1 / (wvector[i] * c1));
			Complex Zl = new Complex(0, wvector[i] * l);
			Complex Zr0 = new Complex(r0 * (1 + Math.pow(wvector[i] / w0, a)), 0);
			Complex Yc0 = Zc0.reciprocal();
			res[i] = Complex.div(new Complex(1, 0),
					Complex.add(Complex.div(new Complex(1, 0), Complex.add(Complex.add(Zr0, Zc1), Zl)), Yc0));
		}
		return res;
	}

}
