package com.adclick.flink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/** JDBC upsert sink into click_aggregates (Postgres ON CONFLICT). */
public class PostgresUpsertSink extends RichSinkFunction<AggregateRecord> {

    private final String jdbcUrl;
    private final String user;
    private final String password;

    private transient Connection conn;
    private transient PreparedStatement ps;

    public PostgresUpsertSink(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        conn = DriverManager.getConnection(jdbcUrl, user, password);
        conn.setAutoCommit(true);
        ps = conn.prepareStatement(AggregationBuilder.upsertSql());
    }

    @Override
    public void invoke(AggregateRecord r, Context context) throws Exception {
        ps.setString(1, r.getGranularity());
        ps.setString(2, r.getDimensionType());
        ps.setString(3, r.getDimensionId());
        ps.setString(4, r.getOrgId());
        ps.setTimestamp(5, new Timestamp(r.getWindowStartMillis()));
        ps.setTimestamp(6, new Timestamp(r.getWindowEndMillis()));
        ps.setLong(7, r.getCount());
        ps.executeUpdate();
    }

    @Override
    public void close() throws Exception {
        if (ps != null) ps.close();
        if (conn != null) conn.close();
    }
}
