
public interface TcpServerListener
    extends TcpConnectionListener
{
    public void opened(TcpServer server);
    public void openFailed(String reason, TcpServer server);
    public void closed(String reason, TcpServer server);
    public void accepted(TcpClient client, TcpServer server);
}
