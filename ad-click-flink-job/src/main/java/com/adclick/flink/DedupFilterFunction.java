package com.adclick.flink;

import com.adclick.common.AdClickEvent;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * Keyed on impressionId. First event per impression passes through,
 * later duplicates within 48h TTL state are dropped (in-memory/RocksDB state).
 */
public class DedupFilterFunction extends KeyedProcessFunction<String, AdClickEvent, AdClickEvent> {

    private transient ValueState<Boolean> seen;

    @Override
    public void open(Configuration parameters) {
        StateTtlConfig ttl = StateTtlConfig.newBuilder(Time.hours(48))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();
        ValueStateDescriptor<Boolean> desc = new ValueStateDescriptor<>("seen-impression", Boolean.class);
        desc.enableTimeToLive(ttl);
        seen = getRuntimeContext().getState(desc);
    }

    @Override
    public void processElement(AdClickEvent event, Context ctx, Collector<AdClickEvent> out) throws Exception {
        if (seen.value() == null) {
            seen.update(Boolean.TRUE);
            out.collect(event);
        }
        // else: duplicate -> drop
    }
}
