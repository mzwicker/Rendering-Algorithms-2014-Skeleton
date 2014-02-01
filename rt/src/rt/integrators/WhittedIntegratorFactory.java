package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;

/**
 * Makes a {@link WhittedIntegrator}.
 */
public class WhittedIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene) {
		return new WhittedIntegrator(scene);
	}

	public void prepareScene(Scene scene) {
		// TODO Auto-generated method stub
	}

}
