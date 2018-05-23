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
            specificHeat.add(new Pair<>(Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next())));
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