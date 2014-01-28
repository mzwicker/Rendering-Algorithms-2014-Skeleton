package rt;

public class WhittedIntegratorFactory extends IntegratorFactory 
{
	public Integrator make(Intersectable objects, LightList lights, EnvironmentMap envMap) 
	{
		return new WhittedIntegrator(objects, lights);
	}

	public void prepareScene(Intersectable objects, LightList lights, EnvironmentMap envMap)
	{
		// Does nothing
	}

}
