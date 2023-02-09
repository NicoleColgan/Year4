import java.io.Serializable;

public class ShareHoldingImpl implements ShareHolding,  Serializable {
    private static final long serialVersionUID = 228L;
    String nameOfShare="";
    double ammountOwned=0;
    double currentPricceOfShare=0;

    public ShareHoldingImpl(String nameOfShare, double ammountOwned, double currentPricceOfShare) {
        this.nameOfShare = nameOfShare;
        this.ammountOwned = ammountOwned;
        this.currentPricceOfShare = currentPricceOfShare;
    }

    @Override
    public String getNameOfShare() {
        return nameOfShare;
    }

    @Override
    public void setNameOfShare(String nameOfShare) {
        this.nameOfShare = nameOfShare;
    }

    @Override
    public double getAmmountOwned() {
        return ammountOwned;
    }

    @Override
    public void setAmmountOwned(double ammountOwned) {
        this.ammountOwned = ammountOwned;
    }

    @Override
    public double getCurrentPricceOfShare() {
        return currentPricceOfShare;
    }

    @Override
    public void setCurrentPricceOfShare(double currentPricceOfShare) {
        this.currentPricceOfShare = currentPricceOfShare;
    }



}
