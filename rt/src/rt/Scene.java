package rt;

/*
 * Defines scene properties that need to be made accessible to the renderer.
 */
public interface Scene {
	
	Camera getCamera();
	Film getFilm();
	Intersectable getIntersectable();
	LightList getLightList();
	Integrator makeIntegrator();
	Sampler makeSampler(int n);	
}
