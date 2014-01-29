package rt;

public class DebugIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene)
	{
		return new DebugIntegrator(scene);
	}
	
	public void prepareScene(Scene scene)
	{		
	}

}
