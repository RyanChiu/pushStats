package com.pushstats.producerfactory;

import com.pushstats.utils.HttpUtil;
import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.producer.AbstractProducer;
import com.zendesk.maxwell.producer.MaxwellOutputConfig;
import com.zendesk.maxwell.row.RowMap;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Custom {@link AbstractProducer} example that collects all the rows for a transaction and writes them to standard out.
 * To ensure atomicity, the producer saves its position in the binlog only after writing a full transaction to stdout.
 *
 * When writing a producer, bear in mind the following ordering guarantees provided by maxwell and the binlog (in row 
 * mode, with no non-transactional tables involved). For every call to push(RowMap) in your producer, the following is 
 * guaranteed:
 *
 *  - The binlog position is monotonically (but not strictly) increasing.
 *  - Transaction IDs are not guaranteed to be monotonically increasing.
 *  - Each transaction is stored in one chunk and delivered in order - no interleaving of transactions ever occur.
 *  - Rolled back transactions are not stored on the binlog and hence never delivered.
 */
public class CustomProducer extends AbstractProducer {
	//private final String headerFormat;
	private final String[] duPairs;
	private final String[] fieldsExcluded;
	private final Collection<RowMap> txRows = new ArrayList<>();
	private final HttpUtil httpUtil=new HttpUtil();
	private static MaxwellOutputConfig config=new MaxwellOutputConfig();
	private String server_id="0";

	public CustomProducer(MaxwellContext context) {
		super(context);
		// this property would be 'custom_producer.header_format' in config.properties
		//headerFormat = context.getConfig().customProducerProperties.getProperty("header_format", "Transaction: %xid% >>>\n");
		// this property would be 'custom_producer.destination_URL' in config.properties
		duPairs = context.getConfig().customProducerProperties.getProperty("DB_URL_pairs").split(",");
		fieldsExcluded = context.getConfig().customProducerProperties.getProperty("fieldsExcluded").split(",");
		server_id=context.getConfig().customProducerProperties.getProperty("server_id");
		
		config.includesServerId=true;
	}

	@Override
	public void push(RowMap r) throws Exception
	{
		// filtering out DDL and heartbeat rows
		if(!r.shouldOutput(outputConfig)) {
			// though not strictly necessary (as skipping has no side effects), we store our position,
			// so maxwell won't have to "re-skip" this position if crashing and restarting.
			context.setPosition(r.getPosition());
			return;
		}
		
		r.setServerId(Long.parseLong(server_id));

		// store uncommitted row in buffer
		txRows.add(r);
		
		if(r.isTXCommit()) {
			// This row is the final and closing row of a transaction. Stream all rows of buffered 
			// transaction to stdout
			//System.out.print(headerFormat.replace("%xid%", r.getXid().toString()));
			/*
			 * try to implement posting
			 */
			for (String pair: duPairs) {
				httpUtil.doPost(pair.split("\\|"), r, fieldsExcluded);
			}
			
			//System.out.println(r.getRowType() + ":" + r.getData().toString());
			//System.out.println(getDesURL());
			txRows.stream()
				.map(CustomProducer::toJSON)
				.forEach(System.out::println);
			txRows.clear();
			
			// Only now, after finally having "persisted" all buffered rows to stdout is it safe to 
			// store the producers position.
			context.setPosition(r.getPosition());
		}		
	}
	
	public String[] getDUPairs() {
		return duPairs;
	}
	
	private static String toJSON(RowMap row) {
		try {
			return row.toJSON();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String[] getFieldsExcluded() {
		return fieldsExcluded;
	}
}
