import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class WindowController {
    @FXML
    TextField countField;
    @FXML
    AnchorPane rightAnchorPane;
    private Data data;
    ArrayList<TextField> startTempLabelList = new ArrayList<>();
    ArrayList<TextField> endTempLabelList = new ArrayList<>();
    ArrayList<TextField> extraHeatLabelList = new ArrayList<>();

    @FXML public void initialize() throws FileNotFoundException {
        File file = new File(Main.class.getResource("Specific_Heat.txt").getFile());
        data = new Data(file);
    }


    @FXML public void setCountOfTransitions() {
        if (!countField.getText().isEmpty()) {
            int countOfNeededFields = Integer.parseInt(countField.getText());
            for (int i = 0; i < countOfNeededFields; i++) {
                final int dupa = i;
                Platform.runLater(() -> {
                    startTempLabelList.add(new TextField());
                    startTempLabelList.get(dupa).setMaxWidth(100);
                    startTempLabelList.get(dupa).setLayoutX(15);
                    startTempLabelList.get(dupa).setLayoutY(dupa*30 + 40);

                    endTempLabelList.add(new TextField());
                    endTempLabelList.get(dupa).setMaxWidth(100);
                    endTempLabelList.get(dupa).setLayoutX(115);
                    endTempLabelList.get(dupa).setLayoutY(dupa*30 + 40);

                    extraHeatLabelList.add(new TextField());
                    extraHeatLabelList.get(dupa).setMaxWidth(100);
                    extraHeatLabelList.get(dupa).setLayoutX(215);
                    extraHeatLabelList.get(dupa).setLayoutY(dupa*30 + 40);

                    rightAnchorPane.getChildren().add(startTempLabelList.get(dupa));
                    rightAnchorPane.getChildren().add(endTempLabelList.get(dupa));
                    rightAnchorPane.getChildren().add(extraHeatLabelList.get(dupa));
                });
            }
        }
    }

    @FXML public void saveToFile() throws FileNotFoundException, UnsupportedEncodingException {
        data.calculateEntalphy();
        data.saveEntalphyToFile();
    }

    @FXML public void showCalculatedCharts()
    {
        data.calculateEntalphy();
        showCharts();
    }

    private void showCharts()
    {
        Stage stage = new Stage();

        VBox box = new VBox();
        box.setPadding(new Insets(10));

        LineChart specificHeatChart = createLineChart("Specific Heat", "Y", "X", data.getSpecificHeat());
        box.getChildren().add(specificHeatChart);

        LineChart entalphyChart = createLineChart("Entalphy", "Y", "X", data.getEnthalpy());
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
