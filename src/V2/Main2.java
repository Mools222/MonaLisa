package V2;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main2 extends Application {
    public static Label labelTriesNumber, labelImprovementsNumber;

    @Override
    public void start(Stage primaryStage) {
        String imageFile = "file:mona.png";
        GeneratedImagePane2 generatedImagePane = new GeneratedImagePane2(imageFile);
        Image image = new Image(imageFile);
        ImageView imageView = new ImageView(image);
        HBox hBoxImages = new HBox(generatedImagePane, imageView);

        Slider slider = new Slider();
//        slider.setOrientation(Orientation.VERTICAL);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setMin(1);
        slider.setMax(10);
        slider.setValue(5);
        slider.setSnapToTicks(true);

        HBox hBoxSlider = new HBox(10, new Label("Rate in ms"), slider);
        HBox.setHgrow(slider, Priority.ALWAYS);

        TextField textFieldPolygons = new TextField("50");
//        textFieldPolygons.setPrefColumnCount(27);
        TextField textFieldSides = new TextField("6");
//        textFieldSides.setPrefColumnCount(27);

        GridPane gridPaneSettings = new GridPane();
        gridPaneSettings.addRow(0, new Label("Polygons"), textFieldPolygons);
        gridPaneSettings.addRow(1, new Label("Sides"), textFieldSides);
//        gridPaneSettings.setPadding(new Insets(5));
        gridPaneSettings.setHgap(5);

        Button buttonStart = new Button("Start");
        Button buttonStop = new Button("Stop");
        Button buttonSave = new Button("Save generated image");
        HBox hBoxButtons = new HBox(10, buttonStart, buttonStop, buttonSave);
        hBoxButtons.setAlignment(Pos.CENTER);

        Label labelTries = new Label("Attemps:");
        labelTriesNumber = new Label("0");
        Label labelImprovements = new Label("Beneficial mutations:");
        labelImprovementsNumber = new Label("0");
        GridPane gridPaneLabels = new GridPane();
        gridPaneLabels.addRow(0, labelTries, labelTriesNumber);
        gridPaneLabels.addRow(1, labelImprovements, labelImprovementsNumber);
        gridPaneLabels.setHgap(5);

        VBox vBox = new VBox(10, hBoxImages, gridPaneLabels, hBoxSlider, gridPaneSettings, hBoxButtons);

        Scene scene = new Scene(vBox);
        primaryStage.setTitle("Image Generator V2");
        primaryStage.setScene(scene);
        primaryStage.show();

        buttonStart.requestFocus();

        textFieldPolygons.setOnKeyReleased(event -> {
            try {
                int polygons = Integer.parseInt(textFieldPolygons.getText());
                generatedImagePane.setNumberOfPolygons(polygons);
            } catch (Exception e) {
                generatedImagePane.setNumberOfPolygons(50);
                textFieldPolygons.setText("50");
            }
        });

        textFieldSides.setOnKeyReleased(event -> {
            try {
                int sides = Integer.parseInt(textFieldSides.getText());
                generatedImagePane.setPolygonSides(sides);
            } catch (Exception e) {
                generatedImagePane.setPolygonSides(6);
                textFieldSides.setText("6");
            }
        });

        slider.setOnMouseReleased(event -> generatedImagePane.setSpeed((int) slider.getValue()));

        buttonStart.setOnAction(event -> {
            generatedImagePane.start();
            buttonStop.requestFocus();
        });

        buttonStop.setOnAction(event -> {
            generatedImagePane.stop();
            buttonStart.requestFocus();
        });

        buttonSave.setOnAction(event -> generatedImagePane.savePaneToFile());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
