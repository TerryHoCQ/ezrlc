package pro2.MVC;

import pro2.MVC.Model;
import pro2.Plot.PlotDataSet;
import pro2.Plot.RectPlot.RectPlotNewMeasurement;
import pro2.RFData.RFData;
import pro2.View.MainView;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.UUID;

public class Controller {

	//================================================================================
    // Public Data
    //================================================================================
	public enum DataSource {FILE, MODEL};
	
	
	
	//================================================================================
    // Private Data
    //================================================================================
	private Model model;
    private MainView view;
    

	//================================================================================
    // Public Functions
    //================================================================================

	public Controller(Model model, MainView view) {
		// TODO Auto-generated constructor stub
		this.model = model;
        this.view = view;
		
	}

	public void contol() {
		// TODO Auto-generated method stub
		System.out.println("control");
		
	}
	/**
	 * Reads the inputfile given by the user
	 * @param file
	 */
	public void loadFile (File file) {
        System.out.println("Opening: " + file.getName());
        // Read the file
        UUID uid = this.model.newInputFile(file);
        view.setFileName(file.getName());
	}

	public MainView getMainView() {
		// TODO Auto-generated method stub
		return this.view;
	}
	
	public void manualNotify () {
		model.manualNotify();
	}
	
	/**
	 * Adds a new Dataset in the model
	 * @param src Datasource (File or Model)
	 * @param id Model ID, if File then not used
	 * @param measType RFData.MeasurementType
	 * @param cpxMod RFData.ComplexModifier
	 * @return	unique data identifier of the plotdataset
	 */
	public int createDataset(DataSource src, int id, RFData.MeasurementType measType, RFData.ComplexModifier cpxMod) {
		return model.createDataset(src,id,measType,cpxMod);
	}
	/**
	 * Adds a new Dataset in the model
	 * @param nm RectPlotNewMeasurement
	 * @return unique data identifier of the plotdataset
	 */
	public int createDataset(RectPlotNewMeasurement nm) {
		return model.createDataset(nm);
	}

	public String getFilename() {
		// TODO Auto-generated method stub
		return model.getFilename();
	}
}