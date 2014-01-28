package rt;

public class PathTracingIntegratorFactory extends IntegratorFactory {

	public Integrator make(Intersectable objects, LightList lights, EnvironmentMap envMap) 
	{
		return new PathTracingIntegrator(objects, lights, envMap);
	}
	
	public void prepareScene(Intersectable objects, LightList lights, EnvironmentMap envMap)
	{
		// Does nothing
	}

}
