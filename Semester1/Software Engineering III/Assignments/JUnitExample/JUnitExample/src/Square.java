public class Square {

    private int side, circumpherance,area;
    private double crossSection;

    //constructor
    public Square(int side){
        this.side=side;
    }

    public int getArea(){
        area=side*side;
        System.out.println("Area is: "+area);
        return area;
    }

    public int getCircumpherance(){
        circumpherance=4*side;
        System.out.println("Circumpherance is: "+circumpherance);
        return circumpherance;
    }

    public double getCrossSection(){
        int temp = ((side*side) + (side*side));
        double hypotenuse = Math.sqrt(temp);
        crossSection=(side+side)*hypotenuse;
        return crossSection;
    }

}
