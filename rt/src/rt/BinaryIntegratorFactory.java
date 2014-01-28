package rt;

public class BinaryIntegratorFactory extends IntegratorFactory {

	public Integrator make(Intersectable objects, LightList lights, EnvironmentMap envMap) 
	{
		return new BinaryIntegrator(objects);
	}
	
	public void prepareScene(Intersectable objects, LightList lights, EnvironmentMap envMap)
	{
		// Does nothing
	}
}
