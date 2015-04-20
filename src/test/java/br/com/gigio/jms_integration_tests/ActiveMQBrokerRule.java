package br.com.gigio.jms_integration_tests;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.usage.SystemUsage;
import org.junit.rules.ExternalResource;


public class ActiveMQBrokerRule extends ExternalResource {
	private static final int _8 = 8;

	private static final int _1024 = 1024;

	private static final String VM_LOCALHOST = "vm://localhost";

	private static final int STORAGE_LIMIT = _1024 * _1024 * _8; // 8mb

	private BrokerService broker;

	@Override
	protected void before() throws Throwable {
		broker = new BrokerService();
		broker.addConnector(connector());
		broker.setUseJmx(true);
		broker.setPersistent(false);
		configureStorage();
		broker.start();
		
	}

	@Override
	protected void after() {
		if (broker != null) {
			try {
				broker.stop();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	private TransportConnector connector() throws URISyntaxException {
		TransportConnector connector = new TransportConnector();
		connector.setUri(new URI(VM_LOCALHOST)); // or tcp://localhost:0
		return connector;
	}
	
	private void configureStorage() {
		SystemUsage systemUsage = broker.getSystemUsage();
		systemUsage.getStoreUsage().setLimit(STORAGE_LIMIT);
		systemUsage.getTempUsage().setLimit(STORAGE_LIMIT);
		
	}

}