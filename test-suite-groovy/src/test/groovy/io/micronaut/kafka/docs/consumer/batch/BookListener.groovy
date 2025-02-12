package io.micronaut.kafka.docs.consumer.batch

import groovy.util.logging.Slf4j

// tag::imports[]
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.context.annotation.Requires
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import reactor.core.publisher.Flux
// end::imports[]

@Requires(property = 'spec.name', value = 'BookListenerTest')
// tag::clazz[]
@KafkaListener(batch = true) // <1>
@Slf4j
class BookListener {
// end::clazz[]

    // tag::method[]
    @Topic("all-the-books")
    void receiveList(List<Book> books) { // <2>
        for (Book book : books) {
            log.info("Got Book = {}", book.title) // <3>
        }
    }
    // end::method[]

    // tag::reactive[]
    @Topic("all-the-books")
    Flux<Book> receiveFlux(Flux<Book> books) {
        books.doOnNext(book ->
            log.info("Got Book = {}", book.title)
        )
    }
    // end::reactive[]

    // tag::manual[]
    @Topic("all-the-books")
    void receive(List<ConsumerRecord<String, Book>> records, Consumer kafkaConsumer) { // <1>

        for (int i = 0; i < records.size(); i++) {
            ConsumerRecord<String, Book> record = records.get(i) // <2>

            // process the book
            Book book = record.value()

            // commit offsets
            String topic = record.topic()
            int partition = record.partition()
            long offset = record.offset() // <3>

            kafkaConsumer.commitSync(Collections.singletonMap( // <4>
                    new TopicPartition(topic, partition),
                    new OffsetAndMetadata(offset + 1, "my metadata")
            ))

        }
    }
    // end::manual[]
//tag::endclazz[]
}
//end::endclazz[]
