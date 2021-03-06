package io.github.flooq.docs;

import com.rabbitmq.client.Delivery;
import io.github.flooq.Flooq;
import io.github.flooq.FlooqBuilder;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.OutboundMessage;

import java.util.function.Function;

public class FlooqApi {

    public void overview() {
        // tag::overview[]
        Flooq flooq = new FlooqBuilder()
                .fromTopic(consumer -> consumer
                        .inputExchange("my.exchange")       // <1>
                        .queue("my.queue")                  // <2>
                        .routingKey("#")                    // <3>
                        .consumeNoAck(messages -> messages  // <4>
                                .map(this::toUppercase)
                        )
                )
                .build();

        Disposable disposable = flooq.start();               // <5>
        // end::overview[]
    }

    public void topic() {
        // tag::topic[]
        String myExchange = "myExchange";

        Flooq flooq = new FlooqBuilder()
                .topic(exchange -> exchange   // <1>
                        .exchange(myExchange)  // <2>
                        .publisher(publisher()) // <3>
                        .atMostOnePublisher(true) // <4>
                )
                .build();

        Disposable disposable = flooq.start(); // <5>
        // end::topic[]
    }

    public void topicPartition() {
        // tag::topic-partition[]
        String myExchange = "myExchange";

        Flooq flooq = new FlooqBuilder()
                .topicPartition(exchange -> exchange   // <1>
                        .exchange(myExchange)          // <2>
                        .publisher(publisher())        // <3>
                        .atMostOnePublisher(true)      // <4>
                )
                .build();

        Disposable disposable = flooq.start();          // <5>
        // end::topic-partition[]
    }

    public void topicWithConsumer() {
        // tag::topic-with-consumer[]
        String myExchange = "myExchange";
        String myQueue = "myQueue";

        Flooq flooq = new FlooqBuilder()
                .topic(exchange -> exchange.exchange(myExchange)) // <1>
                .fromTopic(consumer -> consumer
                        .inputExchange(myExchange)
                        .routingKey("#")
                        .queue(myQueue)          // <2>
                        .consumeNoAck(consume()) // <3>
                )
                .build();

        Disposable disposable = flooq.start();
        // end::topic-with-consumer[]
    }

    public void topicWithVirtualConsumer() {
        // tag::topic-with-virtual-consumer[]
        String myExchange = "myExchange";
        String myQueue = "myQueue";
        int partitions = 5;

        Flooq flooq = new FlooqBuilder()
                .topicPartition(exchange -> exchange.exchange(myExchange))
                .fromTopicPartition(consumer -> consumer
                        .inputExchange(myExchange)
                        .queue(myQueue, partitions) // <1>
                        .consumeNoAck(consume())    // <2>
                )
                .build();

        Disposable disposable = flooq.start();
        // end::topic-with-virtual-consumer[]
    }

    private Function<Flux<Delivery>, Flux<Delivery>> consume() {
        return messages -> messages;
    }

    private Flux<OutboundMessage> publisher() {
        return Flux.empty();
    }

    private Delivery toUppercase(Delivery delivery) {
        return delivery;
    }

}
