package rt;

import java.awt.image.*;

public class Tonemapper {

	public static BufferedImage clamp(Film film)
	{
		BufferedImage img = new BufferedImage(film.width, film.height, BufferedImage.TYPE_3BYTE_BGR);
		
		for(int i=0; i<film.width; i++)
		{
			for(int j=0; j<film.height; j++)
			{
				Spectrum s = film.image[i][j];
				if(s.r<0) s.r=0.f;
				if(s.r>1) s.r=1.f;
				if(s.g<0) s.g=0.f;
				if(s.g>1) s.g=1.f;
				if(s.b<0) s.b=0.f;
				if(s.b>1) s.b=1.f;
				img.setRGB(i, film.height-1-j, ((int)(255.f*s.r) << 16) | ((int)(255.f*s.g) << 8) | ((int)(255.f*s.b)));
			}
		}
		return img;
	}
}
