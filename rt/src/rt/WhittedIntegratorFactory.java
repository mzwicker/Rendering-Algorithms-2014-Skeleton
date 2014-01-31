package rt;

public class WhittedIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene) {
		return new WhittedIntegrator(scene);
	}

	public void prepareScene(Scene scene) {
		// TODO Auto-generated method stub
	}

}
