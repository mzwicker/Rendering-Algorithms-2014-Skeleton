package rt;

public class DirectIlluminationIntegratorFactory extends IntegratorFactory {

	public Integrator make(Intersectable objects, LightList lights, EnvironmentMap envMap) 
	{
		return new DirectIlluminationIntegrator(objects, lights);
	}
	
	public void prepareScene(Intersectable objects, LightList lights, EnvironmentMap envMap)
	{
		// Does nothing
	}

}
