
public interface TcpClientListener
    extends TcpConnectionListener
{
    public void opened(TcpClient client);
    public void openFailed(String reason, TcpClient client);
    public void closed(String reason, TcpClient client);
    public void transmitted(TcpClient client);
    public void transmitFailed(String reason, TcpClient client);
    public void receive(byte[] data, TcpClient client);
}
