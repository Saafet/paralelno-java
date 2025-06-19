import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class SobelFilterTiming {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        // Učitaj sliku u grayscale
        Mat img = Imgcodecs.imread("input.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        if (img.empty()) {
            System.out.println("Ne mogu učitati sliku!");
            return;
        }

        Mat sobelX = new Mat();
        Mat sobelY = new Mat();
        Mat sobelCombined = new Mat();

        long startTime = System.nanoTime();

        // Primjena Sobel filtera u X i Y smjeru
        Imgproc.Sobel(img, sobelX, CvType.CV_64F, 1, 0, 3);
        Imgproc.Sobel(img, sobelY, CvType.CV_64F, 0, 1, 3);

        // Računanje magnitude (kombinacija X i Y)
        Core.magnitude(sobelX, sobelY, sobelCombined);

        long endTime = System.nanoTime();

        double duration = (endTime - startTime) / 1e9; // u sekundama
        System.out.println("Vrijeme obrade Sobel filtera: " + duration + " sekundi");

        // Skaliranje magnituda u 8-bitni format da se mogu spremiti i prikazati kao slika
        Mat sobel8U = new Mat();
        Core.normalize(sobelCombined, sobel8U, 0, 255, Core.NORM_MINMAX);
        sobel8U.convertTo(sobel8U, CvType.CV_8U);

        // Spremi rezultat u datoteku
        Imgcodecs.imwrite("sobel_output.jpg", sobel8U);

        System.out.println("Obrađena slika je spremljena kao sobel_output.jpg");
    }
}