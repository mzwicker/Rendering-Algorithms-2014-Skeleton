package rt;

/**
 * Makes a {@link DebugIntegrator}.
 */
public class DebugIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene)
	{
		return new DebugIntegrator(scene);
	}
	
	public void prepareScene(Scene scene)
	{		
	}

}
