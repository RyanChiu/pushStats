package com.pushstats.producerfactory;

import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.producer.AbstractProducer;
import com.zendesk.maxwell.producer.ProducerFactory;

public class CustomProducerFactory implements ProducerFactory{

    @Override
    public AbstractProducer createProducer(MaxwellContext context) {
        return new CustomProducer(context);
    }
}
