import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class SobelFilterTiming {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat img = Imgcodecs.imread("input.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        if (img.empty()) {
            System.out.println("Ne mogu učitati sliku! Provjeri putanju.");
            return;
        }

        Mat sobelX = new Mat();
        Mat sobelY = new Mat();
        Mat sobelCombined = new Mat();

        long startTime = System.nanoTime();

        Imgproc.Sobel(img, sobelX, CvType.CV_64F, 1, 0);
        Imgproc.Sobel(img, sobelY, CvType.CV_64F, 0, 1);
        Core.magnitude(sobelX, sobelY, sobelCombined);

        long endTime = System.nanoTime();

        Mat sobel8U = new Mat();
        Core.normalize(sobelCombined, sobel8U, 0, 255, Core.NORM_MINMAX);
        sobel8U.convertTo(sobel8U, CvType.CV_8U);

        Imgcodecs.imwrite("sobel_output.jpg", sobel8U);

        long durationNs = endTime - startTime;
        double durationMs = durationNs / 1_000_000.0;
        System.out.printf("Vrijeme izvršavanja Sobel filtera: %d nanosekundi (%.3f milisekundi)\n", durationNs, durationMs);

        // Prikaz slika
        SwingUtilities.invokeLater(() -> {
            showImage(matToBufferedImage(img), "Originalna slika");
            showImage(matToBufferedImage(sobel8U), "Sobel filter rezultat");
        });
    }

    // Metoda za konverziju Mat u BufferedImage
    private static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] b = new byte[bufferSize];
        mat.get(0, 0, b); // kopira pixel podatke
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    // Metoda za prikaz slike u JFrame prozoru
    private static void showImage(BufferedImage img, String title) {
        ImageIcon icon = new ImageIcon(img);
        JFrame frame = new JFrame(title);
        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth() + 50, img.getHeight() + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
