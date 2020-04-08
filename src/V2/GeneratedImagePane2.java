package V2;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GeneratedImagePane2 extends Pane {
    private PixelReader pixelReaderOriginalImage;
    private int numberOfPixels = 200 * 200; // mona.png is 200 * 200 pixels
    private double previousAverageDifference = 3; // R, G & B each have a value between 0 and 1. Therefore the max difference is 3. This can be tested by comparing black (R, G & B = 0) and white (R, G & B = 1)
    private Timeline timeline;
    private Random random = new Random();
    private int tries, improvements;
    private ObservableList<Node> children = getChildren();
    private int numberOfPolygons = 50; // Total number of polygons
    private int polygonSides = 6; // How many sides each polygon will have
    private int polygonTotalPoints = polygonSides * 2; // How many points a polygon is made up of

    public GeneratedImagePane2() {
        setPrefWidth(200);
        setPrefHeight(200);

        loadPixelReader();
        addPolygons();

        timeline = new Timeline(new KeyFrame(Duration.millis(5), event -> mutate()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setNumberOfPolygons(int numberOfPolygons) {
        this.numberOfPolygons = numberOfPolygons;
    }

    public void setPolygonSides(int polygonSides) {
        this.polygonSides = polygonSides;
        polygonTotalPoints = polygonSides * 2;
    }

    public void loadPixelReader() {
        Image image = new Image("file:mona.png");
        pixelReaderOriginalImage = image.getPixelReader();
    }

    public void start() {
        timeline.play();
        System.out.println("Started");
    }

    public void stop() {
        timeline.stop();
        System.out.println("Stopped");
    }

    public void setSpeed(int speed) {
        boolean wasRunning = false;

        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
            wasRunning = true;
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(speed), event -> mutate()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        if (wasRunning)
            timeline.play();
    }

    public void addPolygons() {
        for (int i = 0; i < numberOfPolygons; i++)
            children.add(getPolygon());
    }

    public Polygon getPolygon() {
        Polygon polygon = new Polygon();
        ObservableList<Double> polygonPoints = polygon.getPoints();

        for (int i = 0; i < polygonSides; i++) {
            double x = random.nextDouble() * 201;
            double y = random.nextDouble() * 201;
            polygonPoints.add(x);
            polygonPoints.add(y);
        }

        polygon.setFill(Color.TRANSPARENT);

        return polygon;
    }

    public void mutate() {
        Polygon randomPolygon = (Polygon) children.get(random.nextInt(numberOfPolygons));
        ObservableList<Double> originalPoints = null;

        Double[] originalPointsCopy = null;
        Color originalColorCopy = null;

        int randomMutationRoll = random.nextInt(2);

        if (randomMutationRoll == 0) { // Mutate side
            originalPoints = randomPolygon.getPoints();
            originalPointsCopy = copyPoints(originalPoints);
            mutatePoint(originalPoints);
        } else { // Mutate color
            Color originalColor = (Color) randomPolygon.getFill();
            originalColorCopy = copyColor(originalColor);
            mutateColor(randomPolygon, originalColor);
        }

        double averageDifference = calculateAverageDifference();

        if (previousAverageDifference > averageDifference) {
            previousAverageDifference = averageDifference;
            Main2.labelImprovementsNumber.setText(String.valueOf(++improvements));
        } else {
            if (randomMutationRoll == 0) {
                originalPoints.clear();
                originalPoints.addAll(originalPointsCopy);
            } else {
                randomPolygon.setFill(originalColorCopy);
            }
        }

        Main2.labelTriesNumber.setText(String.valueOf(++tries));
    }

    public Double[] copyPoints(ObservableList<Double> originalPoints) {
        return originalPoints.toArray(new Double[0]);
    }

    public Color copyColor(Color originalColor) {
        return new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), originalColor.getOpacity());
    }

    public void mutatePoint(ObservableList<Double> originalPoints) {
        int randomPointIndex = random.nextInt(polygonTotalPoints);
        originalPoints.remove(randomPointIndex);
        originalPoints.add(randomPointIndex, random.nextDouble() * 201);
    }

    public void mutateColor(Polygon randomPolygon, Color originalColor) {
        int randomMutationRoll = random.nextInt(4);

        Color mutatedColor;
        switch (randomMutationRoll) {
            case 0:
                mutatedColor = new Color(Math.random(), originalColor.getGreen(), originalColor.getBlue(), originalColor.getOpacity()); // Mutate red
                break;
            case 1:
                mutatedColor = new Color(originalColor.getRed(), Math.random(), originalColor.getBlue(), originalColor.getOpacity()); // Mutate green
                break;
            case 2:
                mutatedColor = new Color(originalColor.getRed(), originalColor.getGreen(), Math.random(), originalColor.getOpacity()); // Mutate blue
                break;
            default:
                mutatedColor = new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), Math.random()); // Mutate opacity
                break;
        }

        randomPolygon.setFill(mutatedColor);
    }

    public double calculateAverageDifference() {
        WritableImage snapshot = snapshot(null, null);
        PixelReader pixelReaderGeneratedImage = snapshot.getPixelReader();

        double totalDifference = 0;

        for (int y = 0; y < 200; y++) {
            for (int x = 0; x < 200; x++) {
                Color color1 = pixelReaderOriginalImage.getColor(x, y);
                Color color2 = pixelReaderGeneratedImage.getColor(x, y);

                // https://en.wikipedia.org/wiki/Color_difference
                double difference = Math.pow(color2.getRed() - color1.getRed(), 2) + Math.pow(color2.getGreen() - color1.getGreen(), 2) + Math.pow(color2.getBlue() - color1.getBlue(), 2);
                totalDifference += difference;
            }
        }

        return totalDifference / numberOfPixels;
    }

    // https://stackoverflow.com/questions/38028825/javafx-save-view-of-pane-to-image/38028893
    public void savePaneToFile() {
        try {
            File file = new File("file-v2.png");
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot(null, null), null);
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
