package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;

/**
 * Makes integrators of type {@link rt.integrators.MandelbrotIntegrator}.
 */
public class MandelbrotIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene)
	{
		return new MandelbrotIntegrator();
	}
	
	public void prepareScene(Scene scene)
	{		
	}

}
