package V2;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
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
    private int imageWidth;
    private int imageHeight;
    private int numberOfPixels;
    private double previousAverageDifference = 3; // R, G & B each have a value between 0 and 1. Therefore the max difference is 3. This can be tested by comparing black (R, G & B = 0) and white (R, G & B = 1)
    private Timeline timeline;
    private final Random random = new Random();
    private int tries, improvements;
    private int numberOfPolygons = 50; // Total number of polygons
    private int polygonSides = 6; // How many sides each polygon will have
    private int polygonTotalPoints = polygonSides * 2; // How many points a polygon is made up of

    public GeneratedImagePane2(String imageFile) {
        loadImageAndPixelReader(imageFile);
        setPrefWidth(imageWidth);
        setPrefHeight(imageHeight);
        runSetup();

        timeline = new Timeline(new KeyFrame(Duration.millis(5), event -> mutate()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setNumberOfPolygons(int numberOfPolygons) {
        this.numberOfPolygons = numberOfPolygons;
        runSetup();
    }

    public void setPolygonSides(int polygonSides) {
        this.polygonSides = polygonSides;
        polygonTotalPoints = polygonSides * 2;
        runSetup();
    }

    public void loadImageAndPixelReader(String imageFile) {
        Image image = new Image(imageFile);
        imageHeight = (int) image.getHeight();
        imageWidth = (int) image.getWidth();
        numberOfPixels = imageHeight * imageWidth;
        pixelReaderOriginalImage = image.getPixelReader();
    }

    public void runSetup() {
        previousAverageDifference = 3;
        addPolygons();
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
        getChildren().clear();
        for (int i = 0; i < numberOfPolygons; i++)
            getChildren().add(getPolygon());
        System.out.println("Added " + numberOfPolygons + " polygons");
    }

    public Polygon getPolygon() {
        Polygon polygon = new Polygon();
        ObservableList<Double> polygonPoints = polygon.getPoints();

        for (int i = 0; i < polygonSides; i++) {
            double x = random.nextDouble() * imageWidth;
            double y = random.nextDouble() * imageHeight;
            polygonPoints.add(x);
            polygonPoints.add(y);
        }

        polygon.setFill(Color.TRANSPARENT);

        return polygon;
    }

    public void mutate() {
        Polygon randomPolygon = (Polygon) getChildren().get(random.nextInt(numberOfPolygons));
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
        double randomPoint = randomPointIndex % 2 == 0 ? random.nextDouble() * imageWidth : random.nextDouble() * imageHeight; // If the coordinate index is divisible by 2 it must be an x coordinate. Otherwise, it is a y coordinate.
        originalPoints.add(randomPointIndex, randomPoint);
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

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                Color color1 = pixelReaderOriginalImage.getColor(x, y);
                Color color2 = pixelReaderGeneratedImage.getColor(x, y);
                double difference = Math.pow(color2.getRed() - color1.getRed(), 2) + Math.pow(color2.getGreen() - color1.getGreen(), 2) + Math.pow(color2.getBlue() - color1.getBlue(), 2); // https://en.wikipedia.org/wiki/Color_difference
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
