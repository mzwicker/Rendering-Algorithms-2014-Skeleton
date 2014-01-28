package rt;

import javax.vecmath.Vector3f;
import java.awt.image.Raster;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import ca.forklabs.media.jai.codec.HDRCodec;

public class EnvironmentMap {

	Spectrum envMap[];
	int width, height;
	
	public EnvironmentMap(String fileName)
	{
		HDRCodec.register();
		PlanarImage img = JAI.create("fileload",fileName);
    
	    width = img.getWidth();
	    height = img.getHeight();
	    Raster imgData = img.getData();
	    float pixel[] = new float[3];
	    envMap = new Spectrum[width*height];
	    
	    for(int i=0; i<width; i++) 
	    {
	    	for(int j=0; j<height; j++)
	    	{
	    		pixel = imgData.getPixel(i, height-j-1, pixel);
	    		envMap[i+j*width] = new Spectrum();
	    		envMap[i+j*width].r = pixel[0];
	    		envMap[i+j*width].g = pixel[1];
	    		envMap[i+j*width].b = pixel[2];
	    	}
	    }
	}
	
	public Spectrum lookUp(Vector3f direction)
	{
		// Map direction to environment map coordinates. 
		// See http://www.debevec.org/probes/
		direction.normalize();
		float r = (float)((1/Math.PI)*Math.acos(direction.z)/Math.sqrt(direction.x*direction.x + direction.y*direction.y));
		float u = (direction.x*r + 1.f)*.5f;;
		float v = (direction.y*r + 1.f)*.5f;		
		
		Spectrum s = new Spectrum(envMap[(int)(u*width)+(int)(v*height)*width]);
		return s;
	}
}
