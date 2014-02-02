package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;

/**
 * Makes a {@link PointLightIntegrator}.
 */
public class PointLightIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene) {
		return new PointLightIntegrator(scene);
	}

	public void prepareScene(Scene scene) {
		// TODO Auto-generated method stub
	}

}
