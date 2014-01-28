package rt;

public abstract class IntegratorFactory {

	public abstract Integrator make(Intersectable objects, LightList lights, EnvironmentMap envMap);
	public abstract void prepareScene(Intersectable objects, LightList lights, EnvironmentMap envMap);
}
