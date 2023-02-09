import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JUnitPractiseTest {

    int side=5;
    Square square= new Square(side);

    @Test
    public void testArea(){
        //checking if the two strings are equal
        assertEquals("Area of square of length 5 is 5","Area of square of length 5 is "+square.getArea());
    }

    @Test
    public void testCircumpherance(){
        assertEquals("Circumpherance of a square of length 5 is 20","Circumpherance of a square of length 5 is "+square.getCircumpherance());
    }
}
