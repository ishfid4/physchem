import javafx.beans.property.ObjectProperty;
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
    ArrayList<Pair<Double, Double>> extraEntalphyList = new ArrayList<>();

    public Data(File inputFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(inputFile);

        while(scanner.hasNext()){
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

        for (Pair<Double, Double> pair: specificHeat) {
            extraEntalphyList.add(new Pair<Double, Double>(pair.getKey(),0.));
        }

        scanner.close();
    }

    private double calculateDeltaH(int index)
    {
        return  enthalpy.get(index - 1).getValue() + (specificHeat.get(index).getValue()
                * (specificHeat.get(index).getKey() - specificHeat.get(index - 1).getKey()));
    }

    public void calculateEntalphy(ArrayList<Pair<Double, Double>> extEntalphy) {
        enthalpy = new ArrayList<>();
        enthalpy.add(new Pair<>(specificHeat.get(0).getKey(),
                (specificHeat.get(0).getKey() * specificHeat.get(0).getValue()) + extEntalphy.get(0).getValue()));
        for (int i = 1; i < specificHeat.size(); i++){
            enthalpy.add(new Pair<>(specificHeat.get(i).getKey(), calculateDeltaH(i) + extEntalphy.get(i).getValue()));
        }
    }

    public void calculateEntalphy() {
        enthalpy = new ArrayList<>();
        enthalpy.add(new Pair<>(specificHeat.get(0).getKey(),
                (specificHeat.get(0).getKey() * specificHeat.get(0).getValue())));
        for (int i = 1; i < specificHeat.size(); i++){
            enthalpy.add(new Pair<>(specificHeat.get(i).getKey(), calculateDeltaH(i)));
        }
    }

    public void addEntalphyOfTrans(Double temp1, Double temp2, Double extraEntalphy, ObjectProperty objectProperty) {
        Double enthFunc[] = new Double[0];
        Double dist = temp2-temp1;
        if (objectProperty.getValue() == "Sigmoid")
            enthFunc = sigmoid(dist.intValue());
        if (objectProperty.getValue() == "Sin")
            enthFunc = sin(dist.intValue());
        if (objectProperty.getValue() == "Cos")
            enthFunc = cos(dist.intValue());
        if (objectProperty.getValue() == "Fun1")
            enthFunc = row1(dist.intValue());
        if (objectProperty.getValue() == "Fun2")
            enthFunc = row2(dist.intValue());
        Integer enthFunIndex = 0;
        // init helper pair list

        for (Double i = temp1; i < temp2; i++) {
            for (int j = 0; j < extraEntalphyList.size(); j++) {
                if (extraEntalphyList.get(j).getKey().equals(i)) {
                    extraEntalphyList.set(j,new Pair<Double, Double>(extraEntalphyList.get(j).getKey(),
                            extraEntalphyList.get(j).getValue() + (enthFunc[enthFunIndex] * extraEntalphy)));
                    enthFunIndex++;
                }
            }
        }

        calculateEntalphy(extraEntalphyList);
    }

    public Double[] sigmoid(Integer distance){
        Double function[] = new Double[distance+1];
        Double funSum = 0.0;
        Double deltaMaxX = 10.0 / distance.doubleValue();
        for (Integer i = 0; i <= distance; i++) {
            function[i] = Math.abs(1 / (1 + Math.pow(Math.E, (i * deltaMaxX) - 4))); // 0 to 10
            funSum += function[i];
        }
        for (Integer i = 0; i <= distance; i++) {
            function[i] = function[i] / funSum;
        }
        return function;
    }

    public Double[] sin(Integer distance){
        Double function[] = new Double[distance+1];
        Double funSum = 0.0;
        Double deltaMaxX = 3.15 / distance.doubleValue();
        for (Integer i = 0; i <= distance; i++) {
            function[i] = Math.sin(i * deltaMaxX); // 0 to 3.15
            funSum += function[i];
        }
        for (Integer i = 0; i <= distance; i++) {
            function[i] = function[i] / funSum;
        }
        return function;
    }

    public Double[] cos(Integer distance){
        Double function[] = new Double[distance+1];
        Double funSum = 0.0;
        Double deltaMaxX = 1.6 / distance.doubleValue();
        for (Integer i = 0; i <= distance; i++) {
            function[i] = Math.cos(i * deltaMaxX); // 0 to 1.6
            funSum += function[i];
        }
        for (Integer i = 0; i < distance; i++) {
            function[i] = function[i] / funSum;
        }
        return function;
    }

    public Double[] row1(Integer distance){
        Double function[] = new Double[distance+1];
        Double funSum = 0.0;
        Double deltaMaxX = 2.0 / distance.doubleValue();
        for (Integer i = 0; i <= distance; i++) {
            function[i] = Math.pow(Math.E,(i * deltaMaxX)*(5.0*Math.sin(5.0*(i * deltaMaxX)))+5.0*Math.cos(5.0*(i * deltaMaxX)))+100.0; // 0 to 2
            funSum += function[i];
        }
        for (Integer i = 0; i <= distance; i++) {
            function[i] = function[i] / funSum;
        }
        return function;
    }

    public Double[] row2(Integer distance){
        Double function[] = new Double[distance+1];
        Double funSum = 0.0;
        Double deltaMaxX = 10.0 / distance.doubleValue();
        for (Integer i = 0; i <= distance; i++) {
            function[i] = 2.0*Math.sqrt((i * deltaMaxX)/Math.PI); // 0 to 10
            funSum += function[i];
        }
        for (Integer i = 0; i <= distance; i++) {
            function[i] = function[i] / funSum;
        }
        return function;
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