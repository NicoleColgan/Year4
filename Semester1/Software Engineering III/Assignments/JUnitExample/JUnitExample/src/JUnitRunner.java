import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JUnitRunner {


    public static void main(String args[]){

        //run every test case in this class
        //result is an object that can be returned from a particular class
        //result of a test is pass or fail
        Result result = JUnitCore.runClasses(JUnitPractiseTest.class);

        //only print message if it fails
        for(Failure failure: result.getFailures()){
            System.out.println(failure.toString());
        }
        //printtrue or false depending on whether or not its succesful
        System.out.println(result.wasSuccessful());
    }
}
