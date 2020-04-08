package V1;

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
import javafx.scene.shape.*;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GeneratedImagePane extends Pane {
    private PixelReader pixelReaderOriginalImage;
    private int numberOfPixels = 200 * 200; // mona.png is 200 * 200 pixels
    private double previousAverageDifference = 3; // R, G & B each have a value between 0 and 1. Therefore the max difference is 3. This can be tested by comparing black (R, G & B = 0) and white (R, G & B = 1)
    private Timeline timeline;
    private Rectangle clip = new Rectangle(200, 200);
    private Random random = new Random();
    private int tries, improvements;
    private ObservableList<Node> children = getChildren();
    private byte shapeId;

    public GeneratedImagePane() {
        setPrefWidth(200);
        setPrefHeight(200);

        loadPixelReader();

        timeline = new Timeline(new KeyFrame(Duration.millis(5), event -> attemptToImprove()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setShapeId(byte shapeId) {
        this.shapeId = shapeId;
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
        double x = random.nextDouble() * 201;
        double y = random.nextDouble() * 201;
        double radius = random.nextDouble() * 21;
        children.add(new Circle(x, y, radius, color));
    }

    public void addEllipse() {
        Color color = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        double x = random.nextDouble() * 201;
        double y = random.nextDouble() * 201;
        double radiusX = random.nextDouble() * 201;
        double radiusY = random.nextDouble() * 201;
        Ellipse ellipse = new Ellipse(x, y, radiusX, radiusY);
        ellipse.setFill(color);
        children.add(ellipse);
    }

    public void addRectangle() {
        Color color = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        double x = random.nextDouble() * 201;
        double y = random.nextDouble() * 201;
        double width = random.nextDouble() * 201;
        double height = random.nextDouble() * 201;
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(color);
        children.add(rectangle);
    }

    public void addPolygon(int sides) {
        Polygon polygon = new Polygon();
        ObservableList<Double> polygonPoints = polygon.getPoints();

        for (int i = 0; i < sides; i++) {
            double x = random.nextDouble() * 201;
            double y = random.nextDouble() * 201;
            polygonPoints.add(x);
            polygonPoints.add(y);
        }

        Color color = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        polygon.setFill(color);

        children.add(polygon);
    }

    public void addLine() {
        double x1 = random.nextDouble() * 201;
        double y1 = random.nextDouble() * 201;
        double x2 = random.nextDouble() * 201;
        double y2 = random.nextDouble() * 201;

        Line line = new Line(x1, y1, x2, y2);

        Color color = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        line.setStroke(color);

        children.add(line);
    }

    public void removeShape() {
        children.remove(children.size() - 1);
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
            File file = new File("file-v1.png");
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot(null, null), null);
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
