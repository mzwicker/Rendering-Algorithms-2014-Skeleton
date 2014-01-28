package rt;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class Texture {

	Spectrum texels[][];
	int width, height;
	
	public Texture(String fileName) throws IOException
	{
		BufferedImage img;

		img = ImageIO.read(new File(fileName));
				
		width = img.getWidth();
		height = img.getHeight();
		
		texels = new Spectrum[width][];
		for(int x=0; x<width; x++)
		{
			texels[x] = new Spectrum[height];
			for(int y=0; y<height; y++)
			{
				texels[x][y] = new Spectrum();
				int rgb = img.getRGB(x, y);
				texels[x][y].b = (float)(rgb & 0x000000ff) / 255.f;
				texels[x][y].g = (float)((rgb & 0x0000ff00) >> 8) / 255.f;
				texels[x][y].r = (float)((rgb & 0x00ff0000) >> 16) / 255.f;
			}
		}						
	}
	
	Spectrum bilinearLookup(float u, float v)
	{
		if(u>=0 && u<=1 && v>=0 && v<=1)
		{
			u = u*((float)width-1);
			v = v*((float)height-1);
			
			int u0, u1, v0, v1;
			u0 = (int)Math.floor(u);
			u1 = (int)Math.ceil(u);
			v0 = (int)Math.floor(v);
			v1 = (int)Math.ceil(v);
			
			float wu, wv;
			wu = u-u0;
			wv = v-v0;
			
			Spectrum s = new Spectrum();
			s.r = (texels[u0][v0].r * (1-wu) + texels[u1][v0].r * wu) * (1-wv) + (texels[u0][v1].r * (1-wu) + texels[u1][v1].r * wu) * wv;
			s.g = (texels[u0][v0].g * (1-wu) + texels[u1][v0].g * wu) * (1-wv) + (texels[u0][v1].g * (1-wu) + texels[u1][v1].g * wu) * wv;
			s.b = (texels[u0][v0].b * (1-wu) + texels[u1][v0].b * wu) * (1-wv) + (texels[u0][v1].b * (1-wu) + texels[u1][v1].b * wu) * wv;
			
			return s;
		} else
		{
			return new Spectrum();
		}		
	}
}
