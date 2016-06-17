package ezrlc.ModelCalculation;

import java.util.Arrays;

import org.apache.commons.math3.analysis.MultivariateFunction;

import ezrlc.RFData.RFData.ComplexModifier;
import ezrlc.RFData.RFData.MeasurementType;
import ezrlc.util.Complex;

/**
 * Functions to get the error sum, least square
 * 
 * @author noah
 *
 */
public class MCErrorSum implements MultivariateFunction {

	// ================================================================================
	// Private Data
	// ================================================================================
	private MCEqCircuit circuit;
	private Complex[] measured;
	private double[] measuredValue;
	
	// ================================================================================
	// Constructors
	// ================================================================================
	/**
	 * Create new error sum object
	 * 
	 * @param measured
	 *            measured data
	 * @param circuit
	 *            equivalent circuit object
	 */
	public MCErrorSum(Complex[] measured, MCEqCircuit circuit) {
		this.circuit = circuit;
		this.measured = new Complex[measured.length];
		System.arraycopy(measured, 0, this.measured, 0, measured.length);
		// calculate value
		measuredValue = MCUtil.applyOptimizerOpsToData(circuit.getOptimizerOps(), measured);
	}

	// ================================================================================
	// Private Functions
	// ================================================================================
	/**
	 * Builds the square of the delta between measured and simulated and sums
	 * them up
	 * 
	 * @param measured
	 * @param simulated
	 * @return error sum
	 */
	private static double leastSquare(double[] measured, double[] simulated) {
		double error = 0;
		double delta = 0;

		for (int ctr = 0; ctr < measured.length; ctr++) {
			delta = simulated[ctr] - measured[ctr];
			error = error + Math.pow(delta, 2);
		}

		return error;
	}

	private static double leastSquare(Complex[] measured, Complex[] simulated) {
		double error = 0;
		Complex delta = new Complex(0, 0);

		for (int ctr = 0; ctr < measured.length; ctr++) {
			delta = Complex.sub(simulated[ctr], measured[ctr]);
			error = error + Math.pow(delta.abs(), 2);
		}

		return error;
	}

	// ================================================================================
	// Public static Functions
	// ================================================================================
	/**
	 * Returns the Error sum
	 * 
	 * @param measured
	 *            measured data (abs)
	 * @param simulated
	 *            simulated data (abs)
	 * @return error
	 */
	public static final double getError(double[] measured, double[] simulated) {
		return leastSquare(measured, simulated);
	}

	/**
	 * Returns the Error sum
	 * 
	 * @param measured
	 *            measured data (Complex)
	 * @param simulated
	 *            simulated data (Complex)
	 * @return error
	 */
	public static final double getError(Complex[] measured, Complex[] simulated) {
		return leastSquare(measured, simulated);
	}

	// ================================================================================
	// Interface methods
	// ================================================================================
	/**
	 * Gets called by optimizer to calculate error
	 * 
	 * @param params:
	 *            parameter array from optimizer
	 * @return error
	 */
	@Override
	public double value(double[] params) {
		// set new parameter
		double[] p = MCUtil.topo2Param(this.circuit.getCircuitType(), params, circuit.getLock(), circuit.getParameters());
		circuit.setParameters(p);
		
		// Get values of model
		Complex[] modelData = new Complex[circuit.getWSize()];
		if(circuit.getOptimizerOps().measType == MeasurementType.S) modelData = circuit.getS();
		if(circuit.getOptimizerOps().measType == MeasurementType.Y) modelData = circuit.getY();
		if(circuit.getOptimizerOps().measType == MeasurementType.Z) modelData = circuit.getZ();
		double[] modelVal = MCUtil.applyOptimizerOpsToData(circuit.getOptimizerOps(), modelData);
		
		// calc error
		double error = MCErrorSum.getError(measuredValue, modelVal); // Complex oder mag??
		System.out.println("Params: " +Arrays.toString(p));
		System.out.println("   Err: " +error);
		return error;
	}

}
