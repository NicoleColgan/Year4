import javax.naming.AuthenticationException;
import java.rmi.*;
import java.util.List;
import java.util.Map;

public interface ShareServer extends Remote{

    long getTradingBalance(Long authToken)throws RemoteException, AuthenticationException;

    /**
     * Remote methods
     * @param username of client
     * @param password of client
     * @return 64 bit token that will remain valid on the server for 5 minutes
     *
     * This token is to be passed as the first param to all other methods (other methods check if token
     * is still valid and thorw AuthenticationFailed exception if the token isnt valid or it timed out
     */
    Long login(String user, String pass)throws RemoteException, AuthenticationException;

    /**
     * Method to download shares that are available to purchase on server
     *
     * @param authToken
     * @return list of available shares
     */
    List<Share> shareObjectsAvailableToPurchase(Long authToken)throws RemoteException, AuthenticationException;

    /**
     * put funds into account for trading
     *
     * @param ammount
     * @param authToken
     * @return
     */
    String depositFunds(Long authToken, double ammount)throws RemoteException, AuthenticationException;

    /**
     * take money out of account
     *
     * @param ammount
     * @param authToken
     * @return
     */
    String withdrawFunds(Long authToken, float ammount)throws RemoteException, AuthenticationException;


    /**
     * Allows you to purchase certain ammount of a share and taje that money away from your balance
     *
     * @param shareToBuy
     * @param ammount
     * @param authToken
     * @return
     */
    String purchaseShare(Long authToken, int shareToBuyIdx, float ammount)throws RemoteException, AuthenticationException;

    /**
     * method to sell a certain ammount of a share and put the money you make in your account
     * @param shareToSell
     * @param ammount
     * @param authToken
     */
    String sellShares(Long authToken, int shareToSellIdx, float ammount)throws RemoteException, AuthenticationException;

    /**
     * get a list of share objects and their values
     * @param authToken
     * @return share objects and their values
     */
    String shareHoldingsAndValues(Long authToken)throws RemoteException, AuthenticationException;

    /**
     * Add dummy shares
     */
    void initialiseShares() throws RemoteException;
}
