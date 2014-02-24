package rt.testscenes;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import rt.*;
import rt.cameras.PinholeCamera;
import rt.films.BoxFilterFilm;
import rt.integrators.WhittedIntegratorFactory;
import rt.intersectables.*;
import rt.lightsources.*;
import rt.materials.*;
import rt.materials.XYZGrid;
import rt.samplers.*;
import rt.tonemappers.ClampTonemapper;

public class CSGScene extends Scene {
	
	public CSGScene()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/CSGScene");
		
		// Image width and height in pixels
		width = 640;
		height = 360;
		
		// Number of samples per pixel
		SPP = 4;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(0.f, 0.f, 5.f);
		Vector3f lookAt = new Vector3f(0.f, -.5f, 0.f);
		Vector3f up = new Vector3f(0.f, 1.f, 0.f);
		float fov = 60.f;
		float aspect = 16.f/9.f;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new WhittedIntegratorFactory();
		samplerFactory = new UniformSamplerFactory();		
		
		Material refractive = new Refractive(1.3f);
		
		// Make a conical "bowl" by subtracting cross-sections of two cones
		CSGSolid outerCone = coneCrossSection(60.f, refractive);
		// Make an inner cone and subtract it
		Matrix4f trafo = new Matrix4f();
		trafo.setIdentity();
		trafo.setTranslation(new Vector3f(0.f, 0.f, 0.25f));
		CSGInstance innerCone = new CSGInstance(outerCone, trafo);		
		CSGSolid doubleCone = new CSGNode(outerCone, innerCone, CSGNode.OperationType.SUBTRACT);
		
		// Place it in the scene
		Matrix4f rot = new Matrix4f();
		rot.setIdentity();
		rot.rotX(-(float)Math.PI/2.f);
		Matrix4f trans = new Matrix4f();
		trans.setIdentity();
		trans.setTranslation(new Vector3f(-1.5f, -1.5f, 0.f));
		trans.mul(rot);		
		doubleCone = new CSGInstance(doubleCone, trans);
		
		// Something like a"soap bar"
		Material yellow = new Diffuse(new Spectrum(1.f, 0.8f, 0.2f));
		CSGSolid soap = new CSGUnitCylinder(yellow);
		CSGSolid cap = new CSGTwoSidedInfiniteCone(yellow);
		// Smoothen the edges
		trans.setIdentity();
		trans.m23 = -0.8f;
		CSGSolid cap1 = new CSGInstance(cap, trans); 
		soap = new CSGNode(soap, cap1, CSGNode.OperationType.INTERSECT);
		trans.m23 = 1.8f;
		CSGSolid cap2 = new CSGInstance(cap, trans); 
		soap = new CSGNode(soap, cap2, CSGNode.OperationType.INTERSECT);
		
		// Transform it and place it in the scene
		Matrix4f scale = new Matrix4f();
		// Make it elliptical and rotate a bit around the cylinder axis
		scale.setIdentity();
		scale.m11 = 0.5f;
		scale.m22 = 0.5f;
		trafo = new Matrix4f();
		trafo.rotZ((float)Math.toRadians(-20));
		trafo.mul(scale);
		// Rotate it "up"
		rot = new Matrix4f();
		rot.setIdentity();
		rot.rotX(-(float)Math.PI/2.f);		
		rot.mul(trafo);
		// Place in scene by translating
		trans = new Matrix4f();
		trans.setIdentity();
		trans.setTranslation(new Vector3f(1.5f, -1.5f, 1.f));
		trans.mul(rot);
		soap = new CSGInstance(soap, trans);
		
		// Ground and back plane
		XYZGrid grid = new XYZGrid(new Spectrum(0.2f, 0.f, 0.f), new Spectrum(1.f, 1.f, 1.f), 0.1f, new Vector3f(0.f, 0.3f, 0.f));
		Plane groundPlane = new Plane(new Vector3f(0.f, 1.f, 0.f), 1.5f);
		groundPlane.material = grid;
		Plane backPlane = new Plane(new Vector3f(0.f, 0.f, 1.f), 3.15f);
		backPlane.material = grid;		
		
		// Collect objects in intersectable list
		IntersectableList intersectableList = new IntersectableList();
		intersectableList.add(doubleCone);	
		intersectableList.add(soap);
		intersectableList.add(groundPlane);
		intersectableList.add(backPlane);
		
		// Set the root node for the scene
		root = intersectableList;
		
		// Light sources
		Vector3f lightPos = new Vector3f(eye);
		lightPos.add(new Vector3f(-1.f, 0.f, 0.f));
		LightGeometry pointLight1 = new PointLight(lightPos, new Spectrum(14.f, 14.f, 14.f));
		lightPos.add(new Vector3f(2.f, 0.f, 0.f));
		LightGeometry pointLight2 = new PointLight(lightPos, new Spectrum(14.f, 14.f, 14.f));
		LightGeometry pointLight3 = new PointLight(new Vector3f(0.f, 5.f, 1.f), new Spectrum(24.f, 24.f, 24.f));
		lightList = new LightList();
		lightList.add(pointLight1);
		lightList.add(pointLight2);
		lightList.add(pointLight3);
	}
	
	/**
	 * Make a "horizontal" cross section through a cone with apex angle {@param a}.
	 * The bottom plane is at z=0, the top at z=1. The radius of the bottom circle 
	 * in the cross section is one (the top circle is bigger depending on the apex angle).
	 * @param a apex angle for the cone
	 */
	private CSGSolid coneCrossSection(float a, Material material)
	{
		// Makes a two-sided infinite cone with apex angle 90 degrees
		CSGTwoSidedInfiniteCone doubleCone = new CSGTwoSidedInfiniteCone(material);
		// Scaling factor along the cone axis corresponding to apex angle
		float s = (float)Math.tan((90-a/2)/180.f*(float)Math.PI);
		
		// Scale and translate cone
		Matrix4f scale = new Matrix4f();
		scale.setIdentity();
		scale.m22 = s;
		Matrix4f trans = new Matrix4f();
		trans.setIdentity();
		trans.setTranslation(new Vector3f(0.f, 0.f, -s));
		trans.mul(scale);
		CSGInstance scaledCone = new CSGInstance(doubleCone, trans);
		
		// Cut off at z=0 and z=1
		CSGNode out = new CSGNode(scaledCone, new CSGPlane(new Vector3f(0.f, 0.f, -1.f), 0.f, material), CSGNode.OperationType.INTERSECT);
		out = new CSGNode(out, new CSGPlane(new Vector3f(0.f, 0.f, 1.f), -1.f, material), CSGNode.OperationType.INTERSECT);
		
		return out;
	}
}
