
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements AutoCloseable {

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String client;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        output = new ObjectOutputStream(this.socket.getOutputStream());
        input = new ObjectInputStream(this.socket.getInputStream());
    }

    public String getClient() {
        return client;
    }

    public void sendMessage(SimpleMessage message) throws IOException {
        message.setDateTime();
        output.writeObject(message);
        output.flush();
    }

    public SimpleMessage readMessage() throws IOException, ClassNotFoundException {
       // client=readMessage().getSender();
       // return (SimpleMessage) input.readObject();
        SimpleMessage message = (SimpleMessage) input.readObject();
        client = message.getSender();
        return message;
    }

    @Override
    public void close() throws Exception {
        input.close();
        output.close();
        socket.close();
    }
}