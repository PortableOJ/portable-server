package com.portable.server.util;

import com.Ostermiller.util.CircularByteBuffer;
import com.portable.server.constant.Constant;
import com.portable.server.exception.PortableException;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author shiroha
 */
public class ImageUtils {

    /**
     * 验证码的字符库
     */
    private static final String CAPTCHA_CHAR;

    private static final Font ARIAL_FONT;

    private static final Random RANDOM;

    private static final Integer CAPTCHA_WIDTH = 150;
    private static final Integer CAPTCHA_HEIGHT = 40;
    private static final Integer CAPTCHA_CHAR_ROTATE = 5;
    private static final Integer CAPTCHA_LINE_NUM = 5;
    private static final Integer CAPTCHA_CHAR_NUM = 4;
    private static final String CAPTCHA_CONTENT_TYPE = "PNG";

    static {
        //noinspection SpellCheckingInspection
        CAPTCHA_CHAR = "abcdefghkmnpqrstuvwxyzABCDEFGHGKMNOPQRSTUVWXYZ23456789";
        ARIAL_FONT = new Font(Constant.ARIAL, Font.PLAIN, 20);
        RANDOM = new Random();
    }

    public static InputStream cut(InputStream inputStream, Integer left, Integer top, Integer width, Integer height) throws PortableException {
        CircularByteBuffer circularByteBuffer = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
        try {
            Thumbnails.of(inputStream)
                    .sourceRegion(left, top, width, height)
                    .size(width, height)
                    .keepAspectRatio(false)
                    .toOutputStream(circularByteBuffer.getOutputStream());
        } catch (IOException e) {
            throw PortableException.of("B-01-001");
        }
        try {
            circularByteBuffer.getOutputStream().close();
        } catch (IOException e) {
            throw PortableException.of("B-01-001");
        }

        return circularByteBuffer.getInputStream();
    }

    public static String createCaptcha(OutputStream outputStream) throws PortableException {
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        for (int i = 0; i < CAPTCHA_LINE_NUM; i++) {
            drawRandomLine(g);
        }
        for (int i = 0; i < CAPTCHA_LINE_NUM; i++) {
            drawLeftToRightLine(g);
        }
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < CAPTCHA_CHAR_NUM; i++) {
            ans.append(drawString(g, i + 1));
        }
        try {
            ImageIO.write(image, CAPTCHA_CONTENT_TYPE, outputStream);
        } catch (IOException e) {
            throw PortableException.of("B-01-002");
        }
        return ans.toString();
    }

    private static void drawRandomLine(Graphics2D g) {
        int xs = RANDOM.nextInt(CAPTCHA_WIDTH);
        int xe = RANDOM.nextInt(CAPTCHA_WIDTH);
        int ys = RANDOM.nextInt(CAPTCHA_HEIGHT);
        int ye = RANDOM.nextInt(CAPTCHA_HEIGHT);
        g.setFont(ARIAL_FONT);
        g.setColor(getRandomColor());
        g.drawLine(xs, ys, xe, ye);
    }

    private static void drawLeftToRightLine(Graphics2D g) {
        int xs = RANDOM.nextInt(CAPTCHA_WIDTH / 2);
        int xe = CAPTCHA_WIDTH / 2 + RANDOM.nextInt(CAPTCHA_WIDTH / 2);
        int ys = RANDOM.nextInt(CAPTCHA_HEIGHT / 2);
        int ye = CAPTCHA_HEIGHT / 2 + RANDOM.nextInt(CAPTCHA_HEIGHT / 2);
        g.setFont(ARIAL_FONT);
        g.setColor(getRandomColor());
        g.drawLine(xs, ys, xe, ye);
    }

    private static String drawString(Graphics2D g, Integer num) {
        // 保证左侧和右侧不要贴边，总共留出一个字符的空间
        int baseX = (int) (CAPTCHA_WIDTH * 1.0 / (ImageUtils.CAPTCHA_CHAR_NUM + 1) * num);
        AffineTransform old = g.getTransform();
        String c = getRandomChar();

        g.setFont(ARIAL_FONT);
        g.setColor(getRandomColor());
        g.rotate(Math.toRadians(RANDOM.nextInt(CAPTCHA_CHAR_ROTATE * 2) - CAPTCHA_CHAR_ROTATE));
        g.drawString(c, baseX, CAPTCHA_HEIGHT / 3 * 2);
        g.setTransform(old);

        return c;
    }

    private static Color getRandomColor() {
        int upper = 128;
        int lower = 0;
        int r = lower + RANDOM.nextInt(upper);
        int g = lower + RANDOM.nextInt(upper);
        int b = lower + RANDOM.nextInt(upper);
        return new Color(r, g, b);
    }

    private static String getRandomChar() {
        return String.valueOf(CAPTCHA_CHAR.charAt(RANDOM.nextInt(CAPTCHA_CHAR.length())));
    }
}
