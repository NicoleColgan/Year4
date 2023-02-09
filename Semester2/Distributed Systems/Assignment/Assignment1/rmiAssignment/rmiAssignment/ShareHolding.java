/**
 * Encapsulates information about a share
 */
public interface ShareHolding {
    public String getNameOfShare();
    public void setNameOfShare(String nameOfShare);
    public double getAmmountOwned();
    public void setAmmountOwned(double ammountOwned);
    public double getCurrentPricceOfShare();
    public void setCurrentPricceOfShare(double currentPricceOfShare);
}
