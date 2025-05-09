package io.averkhoglyad.shortcut.message.core.output

import org.apache.kafka.clients.producer.ProducerRecord

interface ProducerRecordFactory<S, R: ProducerRecord<*, *>> {

    fun create(event: S): R

}