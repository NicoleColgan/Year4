/**
 * Interface for encapsulating summary information about a particular share
 */
public interface Share {

    public String getShareName();
    public void setShareName(String shareName);
    public double getVolumeOfSharesAvailable();
    public void setVolumeOfSharesAvailable(double volumeOfSharesAvailable);
    public double getCurrentPrice();
    public void setCurrentPrice(double currentPrice);
    public long getTimeLeftUntilPriceChange();
    public void setTimeLeftUntilPriceChange(long timeLeftUntilPriceChange);


}
