import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static javafx.application.Application.launch;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/window.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Fizyko Chemia");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

//    public static void main(String[] args) throws FileNotFoundException {
//        // write your code here
//        File file = new File(Main.class.getResource("Specific_Heat.txt").getFile());
//        Data data = new Data(file);
//        data.calculateEntalphy();
//
//
//
//
//        double[] xData = data.getEnthalpyAsArrays().getKey();
//        double[] yData = data.getEnthalpyAsArrays().getValue();
//
//        // Create Chart
//        XYChart chart1 = QuickChart.getChart("Entalphy Chart", "Temperature [st C]", "Entalphy",
//                " ",data.getEnthalpyAsArrays().getKey(), data.getEnthalpyAsArrays().getValue());
//        // Show it
//        new SwingWrapper(chart1).displayChart();
//
//        XYChart chart2 = QuickChart.getChart("Specific Heat Chart", "Temperature [st C]", "Specific Heat"
//                , " ",data.getSpecificHeatAsArrays().getKey(), data.getSpecificHeatAsArrays().getValue());
//        new SwingWrapper(chart2).displayChart();
//    }
}