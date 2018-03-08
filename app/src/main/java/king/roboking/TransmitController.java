package king.roboking;

/**
 * Created by King on 2017/8/7.
 */

public interface TransmitController {
    public void send(byte[] data);
    public void sendDelayed(byte[] data,long time);
}
