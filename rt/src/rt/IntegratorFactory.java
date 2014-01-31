package rt;

/**
 * Makes an {@link Integrator}.
 */
public interface IntegratorFactory {

	public Integrator make(Scene scene);
	public void prepareScene(Scene scene);
}
