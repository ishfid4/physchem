import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class WindowController {
    @FXML
    TextField countField;
    @FXML
    AnchorPane rightAnchorPane;
    private boolean entalphyCalculated = false;
    private Data data;
    ArrayList<TextField> startTempLabelList = new ArrayList<>();
    ArrayList<TextField> endTempLabelList = new ArrayList<>();
    ArrayList<TextField> extraHeatLabelList = new ArrayList<>();
    ArrayList<ChoiceBox> choiceBoxes = new ArrayList<>();

    @FXML public void initialize() throws FileNotFoundException {
        initData();
    }

    private void initData() throws FileNotFoundException {
        File file = new File(Main.class.getResource("Specific_Heat.txt").getFile());
        data = new Data(file);
    }

    @FXML public void setCountOfTransitions() throws FileNotFoundException {
        if (!countField.getText().isEmpty()) {
            if (entalphyCalculated) {
                initData();
                entalphyCalculated = false;
            }
            int countOfNeededFields = Integer.parseInt(countField.getText());
            startTempLabelList = new ArrayList<>();
            endTempLabelList = new ArrayList<>();
            extraHeatLabelList = new ArrayList<>();
            choiceBoxes = new ArrayList<>();
            for (int i = 0; i < countOfNeededFields; i++) {
                final int index = i;
                Platform.runLater(() -> {
                    startTempLabelList.add(new TextField());
                    startTempLabelList.get(index).setMaxWidth(100);
                    startTempLabelList.get(index).setLayoutX(15);
                    startTempLabelList.get(index).setLayoutY(index*30 + 40);

                    endTempLabelList.add(new TextField());
                    endTempLabelList.get(index).setMaxWidth(100);
                    endTempLabelList.get(index).setLayoutX(115);
                    endTempLabelList.get(index).setLayoutY(index*30 + 40);

                    extraHeatLabelList.add(new TextField());
                    extraHeatLabelList.get(index).setMaxWidth(100);
                    extraHeatLabelList.get(index).setLayoutX(215);
                    extraHeatLabelList.get(index).setLayoutY(index*30 + 40);

                    choiceBoxes.add(new ChoiceBox(FXCollections.observableArrayList(
                            "Sigmoid", "Sin", "Cos","Fun1", "Fun2")));
                    choiceBoxes.get(index).setMaxWidth(100);
                    choiceBoxes.get(index).setLayoutX(315);
                    choiceBoxes.get(index).setLayoutY(index*30 + 40);

                    rightAnchorPane.getChildren().add(startTempLabelList.get(index));
                    rightAnchorPane.getChildren().add(endTempLabelList.get(index));
                    rightAnchorPane.getChildren().add(extraHeatLabelList.get(index));
                    rightAnchorPane.getChildren().add(choiceBoxes.get(index));
                });
            }
        }
    }

    @FXML public void saveToFile() throws FileNotFoundException, UnsupportedEncodingException {
        if (!entalphyCalculated) {
            data.calculateEntalphy();
            entalphyCalculated = true;
        }
        for (int i = 0; i < Integer.parseInt(countField.getText()); i++) {
            data.addEntalphyOfTrans(Double.parseDouble(startTempLabelList.get(i).getText())
                    ,Double.parseDouble(endTempLabelList.get(i).getText())
                    ,Double.parseDouble(extraHeatLabelList.get(i).getText()), choiceBoxes.get(i).valueProperty());
        }
        data.saveEntalphyToFile();
    }

    @FXML public void showCalculatedCharts()
    {
        if (!entalphyCalculated) {
            data.calculateEntalphy();
            entalphyCalculated = true;
        }
        if (!countField.getText().equals("")) {
            for (int i = 0; i < Integer.parseInt(countField.getText()); i++) {
                data.addEntalphyOfTrans(Double.parseDouble(startTempLabelList.get(i).getText())
                        , Double.parseDouble(endTempLabelList.get(i).getText())
                        , Double.parseDouble(extraHeatLabelList.get(i).getText()), choiceBoxes.get(i).valueProperty());
            }
        }
        showCharts();
    }

    private void showCharts()
    {
        Stage stage = new Stage();

        VBox box = new VBox();
        box.setPadding(new Insets(10));

        LineChart specificHeatChart = createLineChart("Specific Heat", "Specific heat [J/(g*C)]", "Temp [deg. C]", data.getSpecificHeat());
        box.getChildren().add(specificHeatChart);

        LineChart entalphyChart = createLineChart("Entalphy", "Enthalpy [J/g]", "Temp [deg. C]", data.getEnthalpy());
        box.getChildren().add(entalphyChart);

        Scene scene = new Scene(box, 450, 350);
        stage.setScene(scene);
        stage.show();
    }

    private LineChart createLineChart(String chartTitle, String yAxisLabel, String xAxisLabel, ArrayList<Pair<Double, Double>> data) {
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);

        lineChart.setTitle(chartTitle);
        LineChart.Series series = new LineChart.Series();
        for (Pair<Double,Double> pair: data)
            series.getData().add(new LineChart.Data(pair.getKey(), pair.getValue()));

        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.getData().add(series);
        return lineChart;
    }
}
