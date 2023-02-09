package AssignmentSoln;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.List;


public class ResearchGeneticAlgorithm extends JPanel {
    static int[] tempCapacities;
    public static int mar=50;
    static int[][] tempPreferences;
    static int numLecturers;
    static int numStudents;
    static int numGenerations=100;
    static int bestFitness=Integer.MAX_VALUE;
    static int[] globalBestSoln;
    static int globalWorstFitness=Integer.MIN_VALUE;
    static int[] fitnessToPlot=new int[numGenerations];

    public static void main(String args[]){

        readFromCSV();
        numLecturers=tempCapacities.length;
        numStudents=tempPreferences.length;
        
        //generate initial random solution
        //soln is the index of the lecturer - we want to make sure they all get a lecturer as close as possible to the top of their list
        int[] bestSolution = randomSolution();
        //check fitness of solution
        bestFitness = findFitness(bestSolution);
        fitnessToPlot[0]=bestFitness;

        for(int i=0; i<numGenerations; i++){
            int[] newSolution = mutate(bestSolution);
            int newFitess= findFitness(newSolution);

            fitnessToPlot[i]=newFitess;
            //may need to reassign best solution
            if(newFitess< bestFitness){
                bestFitness=newFitess;
                bestSolution=newSolution;
            }
            if(newFitess>globalWorstFitness){
                globalWorstFitness=newFitess;
            }
        }
        System.out.println("Best solution: ");
        for(int i=0; i<bestSolution.length; i++){
            System.out.println(bestSolution[i]);
        }

        System.out.println("Best fitness: "+ bestFitness);
        System.out.println("Worst fitness: "+ globalWorstFitness);
        System.out.println("Average fitness of the solution: "+(bestFitness/numStudents));
        plotFitness();

    }
    private static void plotFitness(){
        JFrame frame =new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ResearchGeneticAlgorithm());
        frame.setSize(800,800);
        frame.setLocation(200,200);
        frame.setVisible(true);
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
        double x = (double) (width - 2 * mar) / (numGenerations);
        double scale = (double) (height - 2 * mar) / globalWorstFitness;
        g1.setPaint(Color.BLUE);
        boolean maxFound=false;
        for (int i = 0; i < fitnessToPlot.length; i++) {

            if(fitnessToPlot[i]==bestFitness && !maxFound){
                //highlight best solution in red
                g1.setPaint(Color.RED);
                double x1 = 1000 + i * x;
                double y1 = height - mar - scale * fitnessToPlot[i];
                g1.fill(new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4));
                maxFound=true;
            }
            else{
                g1.setPaint(Color.BLUE);
                double x1 = mar + i * x;
                double y1 = height - mar - scale * fitnessToPlot[i];
                g1.fill(new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4));
            }
        }
    }

    private static int[] mutate(int[] bestSolution) {
        //we have assigned lecturers according to their capacity, so easiest way to mutate is to swap assignment
        Random r = new Random();
        int s1 = r.nextInt(numStudents);
        int s2 = r.nextInt(numStudents);

        int temp = bestSolution[s1];
        bestSolution[s1]=bestSolution[s2];
        bestSolution[s2]=temp;

        return bestSolution;
    }

    private static int findFitness(int[] bestSolution) {
        int fitness=0;
        //to find fitness, consider preference that student had for that lecturer (i.e. index of the lecturer in their preference array)
        for(int i=0; i<numStudents; i++){
            int assignedLecturer=bestSolution[i];
            //find the index of the assignedLecturerin their preference list
            int idx;
            for (idx=0; idx<tempPreferences[i].length; idx++) {
                //find where this lecture is in their preference list
                if (tempPreferences[i][idx] == assignedLecturer) {
                    fitness += idx+1;
                    break;
                }
            }
        }
        return fitness;
    }

    private static int[] randomSolution() {
        int[] sln = new int[numStudents];

        //if a lecturere reaches their capacity, theyll be removed from this list
        List<Integer> availableLecturers = new ArrayList<>();
        //intially, none have reached capacity
        for(int i=0; i<numLecturers; i++){
            availableLecturers.add(i);
        }

        Random r = new Random();
        for(int i=0; i<numStudents; i++){
            //Assign a random lecturer (index) from the list of available lecturers
            int lecturerIdx=r.nextInt(availableLecturers.size());
            int randomLecturer = availableLecturers.get(lecturerIdx);
            sln[i]=randomLecturer;

            //they can now take one less student
            tempCapacities[randomLecturer]--;

            //check if they have reached their capacity
            if(tempCapacities[randomLecturer]==0){
                //remove from available lecturers
                availableLecturers.remove(lecturerIdx);
            }

        }
        return sln;
    }

    private static void readFromCSV() {
        //read in data from csv file
        String line="";
        String items[];

        File file = new File("C:\\Users\\nicole\\OneDrive - National University of Ireland, Galway\\college\\year 4\\Semester2\\Artificial Intelligence\\Assignments\\Assignment 2\\AssignmentSoln\\Supervisors.csv");
        file.length();
        try{
            tempCapacities = new int[rowCounter(file)];
            Scanner sc = new Scanner(file);
            sc.useDelimiter(",");

            //read in capacities
            //assumes students and lecturers are in order in csv file (e.g. student 1, student2,..., student n)
            int idx=0;
            while(sc.hasNext()){
                line= sc.nextLine();
                items=line.split(",");
                //go through capacities and add to capcities array
                //by splitting the line into tokens, we can ignore the first token because its the lectur index
                tempCapacities[idx]=Integer.parseInt(items[1]);
                idx++;
            }

            //System.out.println("temp capacities\n"+tempCapacities);

            //read in prefereences
            idx=0;
            String line2="";
            File file2 = new File("C:\\Users\\nicole\\OneDrive - National University of Ireland, Galway\\college\\year 4\\Semester2\\Artificial Intelligence\\Assignments\\Assignment 2\\AssignmentSoln\\Student-choices.csv");
            Scanner sc2= new Scanner(file2);
            sc2.useDelimiter(",");
            tempPreferences = new int[rowCounter(file2)][columnCounter(file2)];
            while(sc2.hasNext()){
                line2=sc2.nextLine();
                items=line2.split(",");
                for(int i=0; i<columnCounter(file2); i++){
                    tempPreferences[idx][i] = Integer.parseInt(items[i+1]);
                }
                idx++;
            }
            //System.out.println("preferences: "+columnCounter(file2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int columnCounter(File file) {
        int numCols = 0;
        String line = "";
        String[] tokens;

        try {
            Scanner sc3 = new Scanner(file);
            sc3.useDelimiter(",");

            //Assumes all have same number of columns
            line = sc3.nextLine();
            tokens=line.split(",");
            sc3.close();
            return tokens.length-1;
        } catch (IOException e) {
            System.out.println("problem reading in file");
            e.printStackTrace();
        }
        return 0;
    }
    private static int rowCounter(File file){
        int numRows = 0;
        String line="";
        try {
            Scanner sc1 = new Scanner(file);
            sc1.useDelimiter(",");

            //assumes students and lecturers are in order in csv file (e.g. student 1, student2,..., student n)
            while (sc1.hasNext()) {
                line=sc1.nextLine();
                numRows++;
            }
            sc1.close();
        } catch (IOException e){
            System.out.println("problem reading in file");
            e.printStackTrace();
        }
        return numRows;
    }
}
