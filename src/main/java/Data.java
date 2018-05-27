import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;



public class Data {
    private ArrayList<Pair<Integer, Double>> specificHeat = new ArrayList<>();
    private ArrayList<Pair<Double, Double>> enthalpy = new ArrayList<>();

    public Data(File inputFile) throws FileNotFoundException {
        // Temperatures in input csv should be integer
        Scanner scanner = new Scanner(inputFile);

        while(scanner.hasNext()){ //TODO: should check if it has two in row
            Double nextTemp = Double.parseDouble(scanner.next());
            Double nextCp = Double.parseDouble(scanner.next());
            if (!specificHeat.isEmpty()){
                Pair<Integer, Double> startPair = specificHeat.get(specificHeat.size() - 1);
//                Double b = (dCp - previousPair.getValue())/(dTemp - previousPair.getKey());
//                Double a = (dCp - b)/(dTemp); //y = ax + b => y - b / x
                Integer distanceBeteewnTemp = nextTemp.intValue() - startPair.getKey();
                Double specificHeatDelta = nextCp - startPair.getValue();
                for (int i = startPair.getKey() + 1; i < nextTemp; i++) {
//                    specificHeat.add(new Pair<>(i, (a * (i) + b)));
                    Pair<Integer, Double> previousPair = specificHeat.get(specificHeat.size() - 1);
                    Double calculatedSpecificHeat = previousPair.getValue() + (specificHeatDelta / distanceBeteewnTemp);
                    specificHeat.add(new Pair<>(i,calculatedSpecificHeat));

//                    Take previous and current value, calculate delta, then distribute it evenly on bewtween previous and current temperature
                }
            }
            specificHeat.add(new Pair<>(nextTemp.intValue(), nextCp));
        }
        scanner.close();

        //TODO: normalize data => temperature gap should be 1 deg

    }

    private Double calculateDeltaH(int index)
    {
        return  enthalpy.get(index - 1).getValue() + (specificHeat.get(index).getValue()
                * (specificHeat.get(index).getKey() - specificHeat.get(index - 1).getKey()));
    }

    private Double calculateDeltaCp(int index)
    {
        return (enthalpy.get(index).getKey() - enthalpy.get(index - 1).getKey()) / 2.0;
    }

    public void calculateEntalphy() {
        enthalpy.add(new Pair<>(specificHeat.get(0).getKey().doubleValue(),
                (specificHeat.get(0).getKey() * specificHeat.get(0).getValue())));
        for (int i = 1; i < specificHeat.size()-1; i++){
            enthalpy.add(new Pair<>(calculateDeltaCp(i), calculateDeltaH(i)));
        }

    }

    public void saveEntalphyToFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("calculatedEntalphy.csv", "UTF-8");
        for (Pair<Double, Double> pair: enthalpy) {
            writer.println(pair.getKey() + " " + pair.getValue());
        }
        writer.close();
    }

    public ArrayList<Pair<Integer, Double>> getSpecificHeat() {
        return specificHeat;
    }

    public ArrayList<Pair<Double, Double>> getEnthalpy() {
        return enthalpy;
    }
}