import me.breidenbach.rabbitex.RabbitEx;
import me.breidenbach.rabbitex.connection.RabbitConnectionException;
import me.breidenbach.rabbitex.connection.RabbitConnectionFactory;

import java.io.IOException;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/3/13
 * Time: 1:22 PM
 * © 2013 Kevin E. Breidenbach
 */
public class PublishTest {
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 5672;
    private static final String EXCHANGE = "my-exchange";
    private static final String SUBJECT = "my-subject";
    private static final String MESSAGE = "Hello World!";

    public void sendMessage(String message) throws RabbitConnectionException, IOException {
        RabbitEx rabbitEx = new RabbitConnectionFactory().rabbitConnection(HOSTNAME, PORT);
        rabbitEx.publish(EXCHANGE, SUBJECT, message, null);
        rabbitEx.close();
    }

    public static void main(String...args) {
        PublishTest test = new PublishTest();
        try {
            test.sendMessage(MESSAGE);
        } catch (RabbitConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
