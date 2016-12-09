package de.mobile.commercial.listing.service.kafka

import com.codahale.metrics.MetricRegistry
import com.google.gson.Gson
import de.mobile.commercial.listing.service.Status
import de.mobile.commercial.listing.service.monitoring.monitored
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.task
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class Publisher(val producer46: Producer<String,String>,
                val producer47: Producer<String,String>,
                val producerLocal: Producer<String,String>,
                val metricRegistry: MetricRegistry)  {

    val BATCH_PUBLISH_TIMEOUT = 30L
    val LISTING_STATUS_TOPIC = "xdc_listing-status-v2"
    val LISTING_EVENTS_TOPIC = "listing-events-v3"

    val log = LoggerFactory.getLogger(Publisher::class.java)
    val gson = Gson()

    fun publishToEventLog(event: Event) {
        publishToEventLog(listOf(event))
    }

    fun publishToEventLog(events: List<Event>) {
        val groups = events.groupBy({ it.id.toLong().mod(2) == 0L})

        // publish to both clusters in parallel
        combine(
            task { groups[true]?.let { evenGroup -> publishEventsOrFailOver(evenGroup, producer46, producer47) } },
            task { groups[false]?.let { oddGroup -> publishEventsOrFailOver(oddGroup, producer47, producer46) } }
        ).get()
    }

    private fun publishEventsOrFailOver(
            events: List<Event>,
            primaryProducer: Producer<String,String>,
            secondaryProducer: Producer<String,String>) {

        metricRegistry.histogram("outbound.publish-event-log-batch-size").update(events.size)
        if (events.isNotEmpty()) {
            try {
                monitored(metricRegistry, "outbound.publish-event-log-batch-primary") {
                    publishToEventLog(events, primaryProducer)
                }
            }
            catch (e: Exception) {
                log.warn("fail to publish events to primary cluster - fail over to secondary", e)
                monitored(metricRegistry, "outbound.publish-event-log-batch-secondary") {
                    publishToEventLog(events, secondaryProducer)
                }
            }
        }
    }


    private fun publishToEventLog(events: List<Event>, producer: Producer<String,String>)  {
        val latch = AbortableCountDownLatch(events.size)
        events.forEach {
            producer.send(ProducerRecord(LISTING_EVENTS_TOPIC, it.id, gson.toJson(it)), callback(latch))
        }
        val timedOut = !latch.await(BATCH_PUBLISH_TIMEOUT,TimeUnit.SECONDS)
        if(timedOut) {
            log.error("Publishing batch timed out")
            throw RuntimeException("Publishing batch timed out")
        }
    }

    fun publishStatus(statesAndAdIds: List<Pair<String, Status?>>) = monitored(metricRegistry, "outbound.publish-status") {
        metricRegistry.histogram("outbound.publish-status-batch-size").update(statesAndAdIds.size)
        val latch = AbortableCountDownLatch(statesAndAdIds.size)
        statesAndAdIds.forEach {
            val payload = toJsonOrNull(it.second)
            val adId = it.first
            producerLocal.send(ProducerRecord(LISTING_STATUS_TOPIC, adId, payload), callback(latch))
        }
        val timedOut = !latch.await(BATCH_PUBLISH_TIMEOUT,TimeUnit.SECONDS)
        if(timedOut) {
            log.error("Publishing batch timed out")
            throw RuntimeException("Publishing batch timed out")
        }
    }

    private fun callback(latch: AbortableCountDownLatch) = Callback {
        recordMetadata, e ->
            if (recordMetadata != null) {
                latch.countDown()
            } else {
                log.error("Publishing failed", e )
                latch.abort()
            }
    }

    private fun toJsonOrNull(status: Status?) : String? =
        status?.let{ gson.toJson(it)}
}


class AbortableCountDownLatch(count: Int) : CountDownLatch(count) {
   @Volatile private var aborted = false

    fun abort() {
        if (count === 0L)
            return
        this.aborted = true
        while (count > 0)
            countDown()
    }

    override  fun await(timeout: Long, unit: TimeUnit): Boolean {
        val finished = super.await(timeout, unit)
        if (aborted)
            throw InterruptedException()
        return finished
    }

    override fun await() {
        super.await()
        if (aborted)
            throw InterruptedException()
    }



}
