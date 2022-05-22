public class Client2 {
    public static void main(String[] args) {

        try {
            // new Client(port, ip).start();
            new Client(8090, "127.0.0.1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
