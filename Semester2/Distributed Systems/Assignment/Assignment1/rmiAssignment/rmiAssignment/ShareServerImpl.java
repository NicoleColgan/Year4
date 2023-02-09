import javax.naming.AuthenticationException;
import java.io.File;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ShareServerImpl implements ShareServer{
    static List<Share> shares= new ArrayList<>();
    long fiveminutes=5*60*1000;
    long tradingBalance=0;
    static List <ShareHolding> shareHoldings = new ArrayList<>();
    ShareServerImpl() throws RemoteException {
        super();
        //use threads to change price
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(60_000);
                        for (Share share : shares) {
                            double changedPrice = share.getCurrentPrice() + ThreadLocalRandom.current().nextDouble(-10.0, 10.0);
                            ((ShareImpl) share).setCurrentPrice(changedPrice);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    //main method is required
    public static void main(String args[]){
        try{
            //reset security manager
            if(System.getSecurityManager()==null){

                //create instance of local object
                //have to read contents of file before we reset security manager or else access to file will be denied
                ShareServer shareServer = new ShareServerImpl();
                shareServer.initialiseShares();
                System.out.println("list of shares initialised");

                System.out.println("Instance of share server initialised");

                System.setSecurityManager(new SecurityManager());
                System.out.println("Security manager set");

                //creating a remote object that exists while the server is alive
                ShareServer skeleton = (ShareServer) UnicastRemoteObject.exportObject(shareServer,0);

                //put the server object into the registry
                Registry registry = LocateRegistry.getRegistry();
                registry.rebind("shares",skeleton);
                System.out.println("Name rebind completed");
                System.out.println("Server ready for requests!");
            }
        } catch (Exception e) {
            System.out.println("Error in server main - "+e.toString());
        }
    }

    @Override
    public long getTradingBalance(Long authToken)throws RemoteException, AuthenticationException{
        if(!tokenExpired(authToken)){
            return tradingBalance;
        }
        else{
            throw new AuthenticationException("Incorrect username or password");
        }
    }
    @Override
    public Long login(String username, String password) throws RemoteException, AuthenticationException {
        if(username.equals("user") && password.equals("pass")){
            /*
            Start timer
            use time as a token and compare start time to time now to determine is 5 minutes are up and the token is no longer valid
             */
            return System.currentTimeMillis();
        }
        else{
            throw new AuthenticationException("Incorrect username or password");
        }
    }

    @Override
    public List<Share> shareObjectsAvailableToPurchase(Long authToken) throws RemoteException, AuthenticationException {
        //check if token is expired
        if(!tokenExpired(authToken)){
            return shares;
        }
        else{
            throw new AuthenticationException("Token expired - Please restart session");
        }
    }

    private boolean tokenExpired(Long authToken) {
        long timeElapsed=System.currentTimeMillis()-authToken;
        System.out.println("time elapsed since session started: "+ timeElapsed);
        System.out.println("Is "+timeElapsed+" > "+ fiveminutes+"? "+(timeElapsed >fiveminutes));
        return (timeElapsed >fiveminutes);
    }

    @Override
    public String depositFunds(Long authToken, double ammount) throws RemoteException, AuthenticationException {
        //check if token is expired
        if(!tokenExpired(authToken) && ammount>0){
            tradingBalance+=ammount;
            return "Top up by "+ammount+" successful - new balance is "+tradingBalance;
        }
        else{
            throw new AuthenticationException("Token expired - Please restart session");
        }
    }

    @Override
    public String withdrawFunds(Long authToken, float ammount) throws RemoteException, AuthenticationException {
        //check if token is expired
        if(!tokenExpired(authToken)){
            if(tradingBalance-ammount>=0) {
                tradingBalance -= ammount;
                return "Withdrawal by " + ammount + " successful - new balance is " + tradingBalance;
            }
            else{
                return "Trying to withdraw too much - select print balance option to view funds";
            }
        }
        else{
            throw new AuthenticationException("Token expired - Please restart session");
        }
    }

    @Override
    public String purchaseShare(Long authToken, int shareToBuyIdx, float ammount) throws RemoteException, AuthenticationException {
        //check if token is expired
        if(tokenExpired(authToken)) {
            throw new AuthenticationException("Token Expired");
        }

        Share shareToBuy= shares.get(shareToBuyIdx);
        if(shareToBuy.getVolumeOfSharesAvailable() < ammount){
            return "not enough volume of this share to make purchase";
        }

        if(tradingBalance<ammount){
            return "not enough funds to make purchase";
        }

        //check if use already owns share
        ShareHolding shareHolding = null;
        for(ShareHolding sh: shareHoldings){
            if(sh.getNameOfShare().equals(shareToBuy.getShareName())){
                shareHolding = sh;
                break;
            }
        }

        //if they dont own the share, add to share holdings
        if(shareHolding==null){
            shareHoldings.add(new ShareHoldingImpl(shareToBuy.getShareName(),ammount,shareToBuy.getCurrentPrice()));
        }

        shareToBuy.setVolumeOfSharesAvailable(shareToBuy.getVolumeOfSharesAvailable() - ammount);
        withdrawFunds(authToken, (float) (ammount*shareToBuy.getCurrentPrice()));
        return "shares purchased";
    }

    @Override
    public String sellShares(Long authToken, int shareToSellIdx, float ammount) throws RemoteException, AuthenticationException {
        if(tokenExpired(authToken)){
            throw new AuthenticationException("Session Expired");
        }

        ShareHolding shareToSell = shareHoldings.get(shareToSellIdx);
        if(shareToSell.getAmmountOwned() >= ammount) {   //can purchase
            Share shareOnServer = null;
            for (Share s : shares) {
                if (s.getShareName().equals(shareToSell.getNameOfShare())) {
                    shareOnServer = s;
                    break;
                }
            }

            if (shareOnServer != null) {
                shareToSell.setAmmountOwned(shareToSell.getAmmountOwned() - ammount);
                shareOnServer.setVolumeOfSharesAvailable(shareOnServer.getVolumeOfSharesAvailable() + ammount);
                depositFunds(authToken, ammount * shareOnServer.getCurrentPrice());
                return "Shares bought";
            }
            else {
                return "No shares on server";
            }
        }
        else{
            return "Not enough shares to sell that ammount";
        }
    }

    @Override
    public String shareHoldingsAndValues(Long authToken) throws RemoteException, AuthenticationException {
        if(shareHoldings.size()>0) {
            String returnString="";
            for (int i = 0; i < shareHoldings.size(); i++) {
                returnString += i+ ". Name: " + shareHoldings.get(i).getNameOfShare() +
                        "\tCurrent price: " + shareHoldings.get(i).getCurrentPricceOfShare() +
                        "\t Ammount owned: " + shareHoldings.get(i).getAmmountOwned()+"\n";
            }
            return returnString;
        }
        else return "Currently no share holdings";
    }

    @Override
    public void initialiseShares() throws RemoteException{
        String line="";
        String shareItem[];

        try{
            File file = new File("C:\\Users\\nicole\\Desktop\\DistributedSystemsAssignment1\\rmiAssignment\\shareSamples.csv");
            Scanner sc = new Scanner(file);
            sc.useDelimiter(",");

            while(sc.hasNext()){
                line=sc.nextLine(); //read one line at a time
                shareItem=line.split(",");  //seperate into tokens
                shares.add(new ShareImpl(shareItem[0],Double.parseDouble(shareItem[1]),Double.parseDouble(shareItem[2])));
            }
            sc.close();
        } catch (IOException e){
            System.out.println("Issue initialising shares");
            e.printStackTrace();
        }
    }
}
