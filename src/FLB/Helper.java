package FLB;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Helper
{
	/** 
	 * 
	 * Hàm tạo font
	 * 
	 * @param path đường dẫn của Font (e.g "/font/Minecraft.ttf")
	 * @param size kích cỡ của Font
	 * @return
	 * 
	 */
	public static Font loadCustomFont(String path, float size) 
	{
		Font newFont;
		try {
			FileInputStream fileIS = new FileInputStream(new File(path)); // e.g. "/FLB/myfont.ttf"
			newFont = Font.createFont(Font.TRUETYPE_FONT, fileIS).deriveFont(size);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(newFont);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace(System.err);
			return new Font("Arial", Font.PLAIN, (int) size); // Fallback
		}
		return newFont;
	}

	/** 
	 * 
	 * Hàm tùy chỉnh kích thước Image
	 * 
	 * @param Image ảnh muốn chỉnh sửa
	 * @param width chiều rộng mới của ảnh
	 * @param height chiều cao mới của ảnh
	 * @return
	 * 
	 */
	public static BufferedImage resizeImage(Image originalImage, int width, int height) 
	{
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(originalImage, 0, 0, width, height, null);
		g2d.dispose();
		return resized;
	}

	public static boolean isNonAccented(String input, boolean isPassword) {
		if (isPassword) {
			for (int i = 0; i < input.length(); i++) {
				char c = input.charAt(i);
				if (!(c >= 33 && c <= 126))
					return false;
			}
		} else {
			for (int i = 0; i < input.length(); i++) {
				char c = input.charAt(i);
				if ((c < 35) || (38 < c && c < 48) || (57 < c && c < 63) || (90 < c && c < 97) || c > 122) {
					return false;
				}
			}
		}
		return true;
	}
}