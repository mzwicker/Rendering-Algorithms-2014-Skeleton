package rt;

import java.awt.image.BufferedImage;

/**
 * Tone maps a film by clamping to range [0,1].
 */
public class ClampTonemapper implements Tonemapper {

	public BufferedImage process(Film film)
	{
		BufferedImage img = new BufferedImage(film.getWidth(), film.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		
		for(int i=0; i<film.getWidth(); i++)
		{
			for(int j=0; j<film.getHeight(); j++)
			{
				Spectrum s = film.getImage()[i][j];
				if(s.r<0) s.r=0.f;
				if(s.r>1) s.r=1.f;
				if(s.g<0) s.g=0.f;
				if(s.g>1) s.g=1.f;
				if(s.b<0) s.b=0.f;
				if(s.b>1) s.b=1.f;
				img.setRGB(i, film.getHeight()-1-j, ((int)(255.f*s.r) << 16) | ((int)(255.f*s.g) << 8) | ((int)(255.f*s.b)));
			}
		}
		return img;
	}
}
