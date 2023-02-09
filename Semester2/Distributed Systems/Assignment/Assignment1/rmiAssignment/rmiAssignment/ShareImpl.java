import java.io.Serializable;

public class ShareImpl implements Share, Serializable {
    //this class has to be serialisable to ensure server and client are using the same class
    private static final long serialVersionUID = 227L;
    public ShareImpl(String shareName, double volumeOfSharesAvailable, double currentPrice, long timeLeftUntilPriceChange) {
        super();
        this.shareName = shareName;
        this.volumeOfSharesAvailable = volumeOfSharesAvailable;
        this.currentPrice = currentPrice;
        this.timeLeftUntilPriceChange = timeLeftUntilPriceChange;
    }
    public ShareImpl(String shareName,  double currentPrice, double volumeOfSharesAvailable) {
        super();
        this.shareName = shareName;
        this.volumeOfSharesAvailable = volumeOfSharesAvailable;
        this.currentPrice = currentPrice;
    }

    public ShareImpl(){
        super();
    }


    String shareName="";
    double volumeOfSharesAvailable=0;
    double currentPrice=0;
    long timeLeftUntilPriceChange=0;

    @Override
    public String getShareName() {
        return shareName;
    }

    @Override
    public void setShareName(String shareName) {
        this.shareName=shareName;
    }

    @Override
    public double getVolumeOfSharesAvailable() {
        return volumeOfSharesAvailable;
    }

    @Override
    public void setVolumeOfSharesAvailable(double volumeOfSharesAvailable) {
        this.volumeOfSharesAvailable=volumeOfSharesAvailable;
    }

    @Override
    public double getCurrentPrice() {
        return currentPrice;
    }

    @Override
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice=currentPrice;
    }

    @Override
    public long getTimeLeftUntilPriceChange() {
        return timeLeftUntilPriceChange;
    }

    @Override
    public void setTimeLeftUntilPriceChange(long timeLeftUntilPriceChange) {
        this.timeLeftUntilPriceChange=timeLeftUntilPriceChange;
    }
}
