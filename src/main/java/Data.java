import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;



public class Data {
    private ArrayList<Pair<Double, Double>> specificHeat = new ArrayList<>();
    private ArrayList<Pair<Double, Double>> enthalpy = new ArrayList<>();

    public Data(File inputFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(inputFile);

        while(scanner.hasNext()){ //TODO: should check if it has two in row
            Double nextTemp = Double.parseDouble(scanner.next());
            Double nextCp = Double.parseDouble(scanner.next());
            if (!specificHeat.isEmpty()){
                Pair<Double, Double> startPair = specificHeat.get(specificHeat.size() - 1);
                Double distanceBeteewnTemp = nextTemp - startPair.getKey();
                Double specificHeatDelta = nextCp - startPair.getValue();
                for (Integer i = startPair.getKey().intValue() + 1; i < nextTemp; i++) {
                    Pair<Double, Double> previousPair = specificHeat.get(specificHeat.size() - 1);
                    Double calculatedSpecificHeat = previousPair.getValue() + (specificHeatDelta / distanceBeteewnTemp);
                    specificHeat.add(new Pair<>(i.doubleValue(),calculatedSpecificHeat));
//                  Take previous and current value, calculate delta, then distribute it evenly on bewtween previous and current temperature
                }
            }
            specificHeat.add(new Pair<>(nextTemp, nextCp));
        }
        scanner.close();
    }

    private double calculateDeltaH(int index)
    {
        return  enthalpy.get(index - 1).getValue() + (specificHeat.get(index).getValue()
                * (specificHeat.get(index).getKey() - specificHeat.get(index - 1).getKey()));
    }

    public void calculateEntalphy() {
        enthalpy.add(new Pair<>(specificHeat.get(0).getKey(),
                (specificHeat.get(0).getKey() * specificHeat.get(0).getValue())));
        for (int i = 1; i < specificHeat.size(); i++){
            enthalpy.add(new Pair<>(specificHeat.get(i).getKey(), calculateDeltaH(i)));
        }

    }

    public void saveEntalphyToFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("calculatedEntalphy.csv", "UTF-8");
        for (Pair<Double, Double> pair: enthalpy) {
            writer.println(pair.getKey() + " " + pair.getValue());
        }
        writer.close();
    }

    public ArrayList<Pair<Double, Double>> getSpecificHeat() {
        return specificHeat;
    }

    public ArrayList<Pair<Double, Double>> getEnthalpy() {
        return enthalpy;
    }
}