package V1;

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
import javafx.scene.shape.*;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GeneratedImagePane extends Pane {
    private PixelReader pixelReaderOriginalImage;
    private int imageWidth;
    private int imageHeight;
    private int numberOfPixels;
    private double previousAverageDifference = 3; // R, G & B each have a value between 0 and 1. Therefore the max difference is 3. This can be tested by comparing black (R, G & B = 0) and white (R, G & B = 1)
    private Timeline timeline;
    private final Rectangle clip;
    private final Random random = new Random();
    private int tries, improvements;
    private byte shapeId;

    public GeneratedImagePane(String imageFile) {
        loadImageAndPixelReader(imageFile);
        clip = new Rectangle(imageWidth, imageHeight);
        setPrefWidth(imageWidth);
        setPrefHeight(imageHeight);

        timeline = new Timeline(new KeyFrame(Duration.millis(5), event -> attemptToImprove()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setShapeId(byte shapeId) {
        this.shapeId = shapeId;
    }

    public void loadImageAndPixelReader(String imageFile) {
        Image image = new Image(imageFile);
        imageHeight = (int) image.getHeight();
        imageWidth = (int) image.getWidth();
        numberOfPixels = imageHeight * imageWidth;
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

        timeline = new Timeline(new KeyFrame(Duration.millis(speed), event -> attemptToImprove()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        if (wasRunning)
            timeline.play();
    }

//    public void addShape() {
//        addCircle();
////        addEllipse();
////        addRectangle();
////        addPolygon(3);
////        addPolygon(random.nextInt(10));
////        addLine();
//
//        setClip(clip); // Make sure the shape doesn't stick out of the pane
//    }

    public void addShape(int shapeId) {
        switch (shapeId) {
            case 0:
                addCircle();
                break;
            case 1:
                addEllipse();
                break;
            case 2:
                addRectangle();
                break;
            case 3:
                addPolygon(3);
                break;
            case 4:
                addLine();
                break;
            case 5:
                addShape(random.nextInt(5));
                return;
        }

        setClip(clip); // Make sure the shape doesn't stick out of the pane
    }

    public void addCircle() {
        Color color = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        double x = random.nextDouble() * imageWidth;
        double y = random.nextDouble() * imageHeight;
        double radius = random.nextDouble() * Math.max(imageHeight, imageWidth);
        getChildren().add(new Circle(x, y, radius, color));
    }

    public void addEllipse() {
        Color color = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        double x = random.nextDouble() * imageWidth;
        double y = random.nextDouble() * imageHeight;
        double radiusX = random.nextDouble() * imageWidth;
        double radiusY = random.nextDouble() * imageHeight;
        Ellipse ellipse = new Ellipse(x, y, radiusX, radiusY);
        ellipse.setFill(color);
        getChildren().add(ellipse);
    }

    public void addRectangle() {
        Color color = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        double x = random.nextDouble() * imageWidth;
        double y = random.nextDouble() * imageHeight;
        double width = random.nextDouble() * imageWidth;
        double height = random.nextDouble() * imageHeight;
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(color);
        getChildren().add(rectangle);
    }

    public void addPolygon(int sides) {
        Polygon polygon = new Polygon();
        ObservableList<Double> polygonPoints = polygon.getPoints();

        for (int i = 0; i < sides; i++) {
            double x = random.nextDouble() * imageWidth;
            double y = random.nextDouble() * imageHeight;
            polygonPoints.add(x);
            polygonPoints.add(y);
        }

        Color color = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        polygon.setFill(color);

        getChildren().add(polygon);
    }

    public void addLine() {
        double x1 = random.nextDouble() * imageWidth;
        double y1 = random.nextDouble() * imageHeight;
        double x2 = random.nextDouble() * imageWidth;
        double y2 = random.nextDouble() * imageHeight;

        Line line = new Line(x1, y1, x2, y2);

        Color color = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        line.setStroke(color);

        getChildren().add(line);
    }

    public void removeShape() {
        getChildren().remove(getChildren().size() - 1);
    }

    public void attemptToImprove() {
        addShape(shapeId);

        double averageDifference = calculateAverageDifference();

        if (previousAverageDifference > averageDifference) {
            previousAverageDifference = averageDifference;
            Main.labelImprovementsNumber.setText(String.valueOf(++improvements));
        } else {
            removeShape();
        }

        Main.labelTriesNumber.setText(String.valueOf(++tries));
    }

    public double calculateAverageDifference() {
        WritableImage snapshot = snapshot(null, null);
        PixelReader pixelReaderGeneratedImage = snapshot.getPixelReader();

        double totalDifference = 0;

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
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
            File file = new File("file-v1.png");
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot(null, null), null);
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
