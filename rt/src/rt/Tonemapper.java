package rt;

import java.awt.image.*;

/**
 * Compresses a raw rendered {@link Film} to an image that can be displayed on typical 8-bit displays.
 */
public interface Tonemapper {

	BufferedImage process(Film film);
}
