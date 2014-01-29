package rt;

import java.awt.image.*;

public interface Tonemapper {

	BufferedImage process(Film film);
}
