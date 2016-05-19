import pro2.ModelCalculation.MCEqCircuit.CircuitType;
import pro2.RFData.RFData;
import pro2.util.Complex;
import pro2.util.MathUtil;

public class MCExpander {

	//================================================================================
    // Private variables
    //================================================================================

	//================================================================================
    // Constructors
    //================================================================================
	public MCExpander() {

	}

	//================================================================================
    // Public functions
    //================================================================================
	/**
	 * extends 2 Element EQCircuits to a 3 Element EQCircuit 
	 */

	public static MCEqCircuit expand(MCEqCircuit eqc, Complex[] yz, double[] w) {
		
		//EQCircuits
		Complex[] zeqc=eqc.getZ();
		double[] zeqcAbs=MathUtil.abs(zeqc);
		
		//Measurement
		double[] yzAbs = MathUtil.abs(yz);
		
		//Eval delta Max
		double[] deltaZAbs = MCErrorArray.getErrorArray(yzAbs, zeqcAbs);
		int deltaMaxIndex=MathUtil.getMaxIndex(deltaZAbs);
		
		//deltaMax Values
		double wDeltaMax=w[deltaMaxIndex];
		Complex zDeltaMax=eqc.getZ()[deltaMaxIndex];
		
		//expand EQcircuit
		double r0 = eqc.getParameters()[0];
		double l = eqc.getParameters()[4];
		double c0 = eqc.getParameters()[5];
		Complex Y = zDeltaMax.reciprocal();
		MCEqCircuit eqcExt=null;
		switch (eqc.getCircuitType()){
		case MODEL0:	
			eqcExt = new MCEqCircuit(CircuitType.MODEL6);
			c0=(Y.im()+wDeltaMax*l/(Math.pow(r0, 2)+Math.pow(wDeltaMax*l,2)))/wDeltaMax;
			break;
		case MODEL1:
			eqcExt = new MCEqCircuit(CircuitType.MODEL5); 
			c0=(Y.im()+1/(wDeltaMax*l))/wDeltaMax;
			break;
		case MODEL2:
			eqcExt = new MCEqCircuit(CircuitType.MODEL4); 
			l=(zDeltaMax.im()+1/(wDeltaMax*c0))/wDeltaMax;
			break;
		case MODEL3:
			eqcExt = new MCEqCircuit(CircuitType.MODEL7);
			l=(zDeltaMax.im()+wDeltaMax*c0/(1/Math.pow(r0, 2)+Math.pow(wDeltaMax*c0,2)))/wDeltaMax;
			break;
		}
		eqcExt.setParameter(0, r0);
		eqcExt.setParameter(4, l);
		eqcExt.setParameter(5, c0);
	
		return eqcExt;
	}

}
