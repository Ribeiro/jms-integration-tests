package br.com.gigio.jms_integration_tests;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.ClassRule;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class IntegrationTest {
	
	private static final String ASSASSIN_HBEU = "ASSASSIN/HBEU";
	
	@ClassRule
	public static ActiveMQBrokerRule brokerRule = new ActiveMQBrokerRule();
	
	@Test
	public void shouldConsumeMessageFromTopic() throws JMSException{
		 ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
         Connection connection = connectionFactory.createConnection(); // exception happens here...
         connection.start();
         
         //connection.setExceptionListener(this);
         Session session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
         Destination destination = session.createTopic(ASSASSIN_HBEU);
         
         //Due to the fact we are dealing with a TOPIC the consumer must be registered BEFORE sending a new message!
         MessageConsumer consumer = session.createConsumer(destination);
         
         
         MessageProducer producer = session.createProducer(destination);
         producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
         
         String sendingText = generateXmlForMessage();
         TextMessage textMessage = session.createTextMessage(sendingText);
         
         producer.send(textMessage);
         
         Message message = consumer.receive(3000);
         
         assertThat(message instanceof TextMessage, is(equalTo(true)));
         TextMessage receivedMessage = (TextMessage) message;
         assertThat(receivedMessage.getText(), is(equalTo(sendingText)));
         
         consumer.close();
         producer.close();
         session.close();
         connection.close();
		
	}
	
	private String generateXmlForMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("<breakfast_menu>");
		sb.append("<food>");
		sb.append("<name>Belgian Waffles</name>");
		sb.append("<price>$5.95</price>");
		sb.append("<description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>");
		sb.append("<calories>650</calories>");
		sb.append("</food>");
		return sb.toString();
		
	}

}