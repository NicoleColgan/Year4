import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TargetStringWithLargerAlphabet extends JPanel {
    public static String targetString = "165375927372645573838274647383";
    public static int[] coordinates={100,20};
    public static int mar=50;
    public static int stringLength=30;
    public static int popSize=100;
    public static int numGenerations=100;
    public static Random r = new Random();
    public static List<IndividualInPop> population;
    public static List<Integer> averageFitness=new ArrayList<>();
    static int numGens;

    public static void main(String[] args) {

        //System.out.println("str len: "+targetString.length());
        //create initial population
        population = new ArrayList<>();
        generatePop();  //build random population
        //-------------------------TESTING------------------------------------------------------------------
//        System.out.println("All pop");
//        for (int k=0;k< population.size(); k++){
//            System.out.println("val: "+population.get(k).getValue()+"  fitness: "+population.get(k).getFitness());
//        }
        //---------------------------------------------------------------------------------------------------
        boolean doneLooping;
        for (numGens = 0; numGens < numGenerations; numGens++) {

            //check if we can stop looping
            doneLooping=false;
            if(population.get(0).getFitness()==30){
                System.out.println("Solution found on the "+numGens+"th iteration of the loop (there are "+numGenerations+" loops in total)");
                System.out.println("value of string: "+population.get(0).getValue()+" Fitness: "+population.get(0).getFitness());
                plotAverageFitness();
                doneLooping=true;
            }

            if(doneLooping)
                break;

            //to find out the fittest individuals in this population
            Collections.sort(population, (individual1, individual2) -> individual2.getFitness() - individual1.getFitness());
            //take the half of this population size with the best fitness
            population = population.subList(0, population.size() / 2);

            //-------------------------TESTING------------------------------------------------------------------
//
//        System.out.println("Top half in pop");
//        for (int k=0;k< population.size(); k++){
//            System.out.println("val: "+population.get(k).getValue()+"  fitness: "+population.get(k).getFitness());
//        }
            //---------------------------------------------------------------------------------------------------


            crossOver();

//            //-------------------------TESTING------------------------------------------------------------------
            //print out fittest (should eventually be all 30 1's)
            if (numGens==numGenerations-1 && numGens!=0) {
                System.out.println("best one -val: " + population.get(0).getValue() + "  fitness: " + population.get(0).getFitness());
                plotAverageFitness();
            }
//            //---------------------------------------------------------------------------------------------------

            mutate();
            //-------------------------TESTING------------------------------------------------------------------
//                    System.out.println("After mutation");
//                    for (int k=0;k< population.size(); k++){
//                        System.out.println("val: "+population.get(k).getValue()+"  fitness: "+population.get(k).getFitness());
//                    }
            //---------------------------------------------------------------------------------------------------

            setAverageFitness();

        }

    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g1 = (Graphics2D) g;
        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        //draw lines
        g1.draw(new Line2D.Double(mar, mar, mar, height - mar));
        g1.draw(new Line2D.Double(mar, height - mar, width - mar, height - mar));

        //scale according to how many generations it took to find solution
        double x = (double) (width - 2 * mar) / (numGens);
        double scale = (double) (height - 2 * mar) / getMax();
        g1.setPaint(Color.BLUE);
        for (int i = 0; i < averageFitness.size(); i++) {
            double x1 = mar + i * x;
            double y1 = height - mar - scale * averageFitness.get(i);
            g1.fill(new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4));
        }
    }
    private int getMax(){
        return 30;
    }
    private static void plotAverageFitness(){
        JFrame frame = new JFrame();
        frame.setContentPane(new TargetStringWithLargerAlphabet());
        frame.setSize(600, 400);
        frame.setVisible(true);

    }


    private static void setAverageFitness() {
        int avg=0;
        for(int i=0; i<population.size(); i++){
            avg+=population.get(i).getFitness();
        }
        averageFitness.add(avg/population.size());
    }
    private static void generatePop(){
        String item = "";
        for (int j = 0; j < popSize; j++) {
            item = "";
            for (int i = 0; i < stringLength; i++) {
                item += r.nextInt(10);    //random value in range 0-1
            }
            IndividualInPop individualInPop = new IndividualInPop();
            individualInPop.setValue(item);
            //find fitness of each individual and store value
            individualInPop.setFitness(findFitness(individualInPop));
            population.add(individualInPop);
        }
    }

    /**
     * Fitness function finds out how many characters in this string match the target string
     * Higher fitness is a better solution
     * @param individualInPop
     * @return
     */
    private static int findFitness(IndividualInPop individualInPop) {
        int fitness=0;
        for (int i=0;  i<30; i++){
            if(individualInPop.getValue().charAt(i)==targetString.charAt(i))
                fitness++;
        }
        return fitness;
    }
    /**
     * crossover - single point crossover (chose an index in string to crossover)
     * cross these two strings to produce a child where the child string consists of parent 1's
     * string up to the crossover point and parent 2's string after the crossover point
     */
    private static void crossOver() {
        List<IndividualInPop> crossedPop = new ArrayList<>();

        int crossOverIdx = r.nextInt(stringLength);
        for (int i = 0; i < population.size(); i++) {
            IndividualInPop x = population.get(r.nextInt(population.size()));
            IndividualInPop y = population.get(r.nextInt(population.size()));
            IndividualInPop new1 = new IndividualInPop();
            String val=x.getValue().substring(0, crossOverIdx) +y.getValue().substring(crossOverIdx, stringLength);
            new1.setValue(val);
            new1.setFitness(findFitness(new1));

            IndividualInPop new2 = new IndividualInPop();
            val=y.getValue().substring(0, crossOverIdx) +x.getValue().substring(crossOverIdx, stringLength);
            new2.setValue(val);
            new2.setFitness(findFitness(new2));

            crossedPop.add(new1);
            crossedPop.add(new2);
        }
        //repopulate population with crossed individuals
        population.clear();
        population=crossedPop;
        Collections.sort(crossedPop, (individual1, individual2) ->  individual2.getFitness() - individual1.getFitness());
    }
    /**
     * Mutation is a change to the individuals genetic information (a bit in our string)
     * Mutation rate should be low (0.01), so we're not changing the whole string
     * Mutation rate in this case is the probability that a bit will change in a string
     */
    private static void mutate() {
        double mutationRate = 0.01;
        for (int j = 0; j < population.size(); j++) {
            for (int i = 0; i < stringLength; i++) {
                if (r.nextDouble(2) <= mutationRate) {
                    String temp = population.get(j).getValue().substring(0, i);
                    temp += r.nextInt(10);
                    temp += population.get(j).getValue().substring(i + 1, 30);
                    population.get(j).setValue(temp);
                }
            }
        }
    }

}

