package ezrlc;

import java.awt.EventQueue;

import ezrlc.MVC.Controller;
import ezrlc.MVC.Model;
import ezrlc.View.MainView;

/**
 * EzRLC, contains main routine
 * @author noah
 */
public class EzRLC {

	/**
	 * Main routine, gets called at program start
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Model model = new Model();
				MainView view = new MainView();
				Controller controller = new Controller(model, view);

				view.setController(controller);

				view.build();
				view.setVisible(true);

				// Add observers
				model.addObserver(view);

				controller.contol();
			}
		});
	}
}