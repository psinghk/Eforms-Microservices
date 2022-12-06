package in.nic.ashwini.eForms.services;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

	public String generateCaptchaString() {

		Color textColor = new Color(0x47, 0x6D, 0xAB);
		// Font textFont = new Font("Verdana", Font.PLAIN, 24);
		Font textFont = new Font("Arial", Font.PLAIN, 24);
		int charsToPrint = 6;
		int width = 150;
		int height = 50;
		float horizMargin = 30.0f;
		//float imageQuality = 1.0f; // max is 1.0 (this is for jpeg)
		double rotationRange = 1.1; // this is radians

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		GradientPaint gp = new GradientPaint(10, 10, Color.WHITE, 20, 20, Color.WHITE, true);
		g.setPaint(gp);

		// Draw an oval
		// g.setColor(new Color(0xD8, 0xD5, 0x88));
		g.fillRect(0, 0, width, height);
		g.setColor(textColor);
		g.setFont(textFont);

		FontMetrics fontMetrics = g.getFontMetrics();
		int maxAdvance = fontMetrics.getMaxAdvance();
		int fontHeight = fontMetrics.getHeight();
		String elegibleChars = "ABCDEFGHJKLMPQRSTUVWXYabcdefhjkmnpqrstuvwxy23456789";
		char[] chars = elegibleChars.toCharArray();
		float spaceForLetters = -horizMargin * 2 + width;
		float spacePerChar = spaceForLetters / (charsToPrint - 1.0f);

		StringBuffer finalString = new StringBuffer();

		for (int i = 0; i < charsToPrint; i++) {
			double randomValue = Math.random();
			int randomIndex = (int) Math.round(randomValue * (chars.length - 1));
			char characterToShow = chars[randomIndex];
			finalString.append(characterToShow);

			// this is a separate canvas used for the character so that
			// we can rotate it independently
			int charWidth = fontMetrics.charWidth(characterToShow);
			int charDim = Math.max(maxAdvance, fontHeight);
			int halfCharDim = (int) (charDim / 2);
			BufferedImage charImage = new BufferedImage(charDim, charDim, BufferedImage.TYPE_INT_ARGB);
			Graphics2D charGraphics = charImage.createGraphics();
			charGraphics.translate(halfCharDim, halfCharDim);
			double angle = (Math.random() - 0.5) * rotationRange;
			charGraphics.transform(AffineTransform.getRotateInstance(angle));
			charGraphics.translate(-halfCharDim, -halfCharDim);
			charGraphics.setColor(textColor);
			charGraphics.setFont(textFont);
			int charX = (int) (0.5 * charDim - 0.5 * charWidth);
			charGraphics.drawString("" + characterToShow, charX,
					(int) ((charDim - fontMetrics.getAscent()) / 2 + fontMetrics.getAscent()));
			float x = horizMargin + spacePerChar * (i) - charDim / 2.0f;
			int y = (int) ((height - charDim) / 2);
			g.drawImage(charImage, (int) x, y, charDim, charDim, null, null);
			charGraphics.dispose();
		}
		
//		Iterator iter = ImageIO.getImageWritersByFormatName("JPG");
//
//        if (iter.hasNext()) {
//            ImageWriter writer = (ImageWriter) iter.next();
//            ImageWriteParam iwp = writer.getDefaultWriteParam();
//            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//            iwp.setCompressionQuality(imageQuality);
//            writer.setOutput(ImageIO.createImageOutputStream(res.getOutputStream()));
//            IIOImage imageIO = new IIOImage(bufferedImage, null, null);
//            writer.write(null, imageIO, iwp);
//        } else {
//            throw new RuntimeException("no encoder found for jsp");
//        }
        g.dispose();
		return finalString.toString();
	}
}