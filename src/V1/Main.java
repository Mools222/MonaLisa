package V1;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    public static Label labelTriesNumber, labelImprovementsNumber;
//    public static Slider slider;

    @Override
    public void start(Stage primaryStage) {
        GeneratedImagePane generatedImagePane = new GeneratedImagePane();
        Image image = new Image("file:mona.png");
//        Image image = new Image("file:rygb - Copy.png");
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

        RadioButton rb1 = new RadioButton("Circle");
        rb1.setSelected(true);
        RadioButton rb2 = new RadioButton("Ellipse");
        RadioButton rb3 = new RadioButton("Rectangle");
        RadioButton rb4 = new RadioButton("Polygon (3)");
        RadioButton rb5 = new RadioButton("Line");
        RadioButton rb6 = new RadioButton("Random");
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(rb1, rb2, rb3, rb4, rb5, rb6);

        HBox hBoxRadioButtons1 = new HBox(20, rb1, rb2, rb3);
        hBoxRadioButtons1.setAlignment(Pos.CENTER);
        HBox hBoxRadioButtons2 = new HBox(20, rb4, rb5, rb6);
        hBoxRadioButtons2.setAlignment(Pos.CENTER);

        Button buttonStart = new Button("Start");
        Button buttonStop = new Button("Stop");
        Button buttonSave = new Button("Save generated image");
        HBox hBoxButtons = new HBox(10, buttonStart, buttonStop, buttonSave);
        hBoxButtons.setAlignment(Pos.CENTER);

        Label labelTries = new Label("Attemps:");
        labelTriesNumber = new Label("0");
        Label labelImprovements = new Label("Shapes added:");
        labelImprovementsNumber = new Label("0");
        GridPane gridPaneLabels = new GridPane();
        gridPaneLabels.addRow(0, labelTries, labelTriesNumber);
        gridPaneLabels.addRow(1, labelImprovements, labelImprovementsNumber);
        gridPaneLabels.setHgap(5);

        VBox vBox = new VBox(10, hBoxImages, gridPaneLabels, hBoxSlider, hBoxRadioButtons1, hBoxRadioButtons2, hBoxButtons);

        Scene scene = new Scene(vBox);
        primaryStage.setTitle("Image Generator V1");
        primaryStage.setScene(scene);
        primaryStage.show();

        buttonStart.requestFocus();

        slider.setOnMouseReleased(event -> generatedImagePane.setSpeed((int) slider.getValue()));

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton rb = (RadioButton) toggleGroup.getSelectedToggle();

            switch (rb.getText()) {
                case "Circle":
                    generatedImagePane.setShapeId((byte) 0);
                    break;
                case "Ellipse":
                    generatedImagePane.setShapeId((byte) 1);
                    break;
                case "Rectangle":
                    generatedImagePane.setShapeId((byte) 2);
                    break;
                case "Polygon (3)":
                    generatedImagePane.setShapeId((byte) 3);
                    break;
                case "Line":
                    generatedImagePane.setShapeId((byte) 4);
                    break;
                case "Random":
                    generatedImagePane.setShapeId((byte) 5);
            }
        });

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
