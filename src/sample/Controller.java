package sample;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;


public class Controller {

    @FXML
    private Label questionLabel;

    @FXML
    private TilePane tilePane;

    private String currentFlagDirectory = "D:/flag-resources/flags";
    private Desktop desktop = Desktop.getDesktop();
    private boolean endOfSession = false;


    @FXML
    public void initialize() {
        updateQuestion();
    }

    public void reset() {
        currentFlagDirectory = "D:/flag-resources/flags";
        endOfSession = false;
        updateQuestion();
    }

    public void answerYes() {
        if (!endOfSession) {
            currentFlagDirectory += "/tak";
            updateQuestion();
        }

    }

    public void answerNo() {
        if (!endOfSession) {
            currentFlagDirectory += "/nie";
            updateQuestion();
        }

    }

    private void addImage(Path path) {
        try {
            Image image1 = new Image(new FileInputStream(path.toString()));
            StackPane stackPane = new StackPane();
            ImageView imageView = new ImageView(image1);
            imageView.setFitHeight(30);
            imageView.setFitWidth(40);
            stackPane.getChildren().add(imageView);
            stackPane.setPadding(new Insets(3, 3, 3, 3));
            tilePane.getChildren().add(stackPane);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateFlagPane() {
        Path p = Paths.get(currentFlagDirectory);
        tilePane.getChildren().clear();

        try {
            Files.walk(p).filter(x -> x.toString().endsWith(".png")).forEach(this::addImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateQuestion() {

        updateFlagPane();
        File file = new File(currentFlagDirectory);
        File[] files = file.listFiles();
        if (files != null) {
            Optional<File> questionOptional = Arrays.stream(files)
                    .filter(x -> x.toString().endsWith(".txt")).findFirst();

            if (questionOptional.isPresent()) {
                try {
                    Optional<String> questionTextOptional = Files.lines(questionOptional.get().toPath()).findFirst();
                    questionTextOptional.ifPresent(x -> questionLabel.setText(x));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else {
                Optional<File> imageOptional = Arrays.stream(files)
                        .filter(x -> x.toString().endsWith(".png")).findFirst();
                if (imageOptional.isPresent()) {
                    endOfSession = true;
                    try {
                        String imagePath = currentFlagDirectory + "/" +
                                Arrays.stream(imageOptional.get().toString().split("\\\\"))
                                        .reduce((first, second) -> second).orElse(null);

                        desktop.open(new File(imagePath));
                        reset();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}