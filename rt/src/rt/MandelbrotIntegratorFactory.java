package rt;

public class MandelbrotIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene)
	{
		return new MandelbrotIntegrator();
	}
	
	public void prepareScene(Scene scene)
	{		
	}

}
