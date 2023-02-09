import java.net.MalformedURLException;
import java.rmi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    static ShareServer shareServer;
    static Long token;
    static Scanner sc= new Scanner(System.in);
    public static void main(String args[]){
        try {
            //get remote reference to object
            shareServer = (ShareServer) Naming.lookup("//localhost/shares");
            System.out.println("logging you in");
            token = shareServer.login("user", "pass");
        } catch(Exception e) {
            System.out.println("Error during initial log in -");
            e.printStackTrace();
        }

        while(true) {    //continue to accept inputs until token expires
            System.out.println("Enter a number for one of the following commands \n" +
            "1. Print all available shares\n" +
                    "2. Deposit funds\n" +
                    "3. Withdraw funds\n" +
                    "4. Check balance\n" +
                    "5. Purchase share\n" +
                    "6. Sell share\n" +
                    "7. View share holdings\n");
            int Request = sc.nextInt();
            try {

                switch(Request){
                    case 1:
                        //download and print summary of all the shares available on the system
                        List<Share> listOfShares = new ArrayList<>();
                        listOfShares = shareServer.shareObjectsAvailableToPurchase(token);
                        System.out.println("list of shares: ");
                        for (int i = 0; i < listOfShares.size(); i++) {
                            System.out.println(listOfShares.get(i).getShareName());
                            System.out.println(listOfShares.get(i).getCurrentPrice());
                        }
                        break;

                    case 2:
                        //deposit funds
                        System.out.println("Enter ammount to deposit");
                        double ammountToDeposit = sc.nextDouble();
                        System.out.println(shareServer.depositFunds(token, ammountToDeposit));
                        break;

                    case 3:
                        //withdraw funds
                        System.out.println("Enter ammount to withdraw");
                        float ammountToWithDraw = sc.nextFloat();
                        System.out.println(shareServer.withdrawFunds(token, ammountToWithDraw));
                        break;

                    case 4:
                        //check balanc
                        Long tradingBalance = shareServer.getTradingBalance(token);
                        System.out.println("balance: "+tradingBalance);
                        break;

                    case 5:
                        //purchase share
                        List<Share> listOfSharesToBuy = new ArrayList<>();
                        listOfSharesToBuy = shareServer.shareObjectsAvailableToPurchase(token);
                        System.out.println("Enter the number representing the share name you want to buy: ");
                        for (int i = 0; i < listOfSharesToBuy.size(); i++) {
                            System.out.println((i)+ ". Name: "+listOfSharesToBuy.get(i).getShareName()+
                                    "\tCurrent price: "+listOfSharesToBuy.get(i).getCurrentPrice()+
                                    "\t Volume available: "+listOfSharesToBuy.get(i).getVolumeOfSharesAvailable());
                        }
                        int shareToBuyIdx=sc.nextInt();
                        System.out.println("Enter ammount to purchase: ");
                        float ammountToBuy=sc.nextFloat();
                        String purchaseSharesString = shareServer.purchaseShare(token,shareToBuyIdx,ammountToBuy);
                        break;

                    case 6:
                        //sell a share
                        System.out.println("Select an index of shares to sell");
                        String shareHolidingString = shareServer.shareHoldingsAndValues(token);
                        System.out.println(shareHolidingString);
                        int index =sc.nextInt();
                        System.out.println("Select an ammount of that to sell");
                        float ammountToSell =sc.nextInt();
                        String sellSharesString =shareServer.sellShares(token, index, ammountToSell);
                        System.out.println(sellSharesString);
                        break;

                    case 7:
                        //view share holdings
                        String viewHoldinds = shareServer.shareHoldingsAndValues(token);
                        System.out.println(viewHoldinds);
                        break;

                    default:
                        System.out.println("invalid entry");

                }
                //withdrawfunds
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
