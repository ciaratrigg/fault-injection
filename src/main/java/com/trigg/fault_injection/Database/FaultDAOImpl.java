package com.trigg.fault_injection.Database;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.trigg.fault_injection.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FaultDAOImpl implements FaultDAO {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Override
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @Override
    public List<Fault> selectAllFaults() {
        String baseQuery = "SELECT * FROM fault";
        List<BaseFault> baseFaults = jdbcTemplate.query(baseQuery, new FaultMapper());
        List<Fault> faults = new ArrayList<>();

        for (BaseFault bf : baseFaults) {
            String faultType = bf.getFault_type().toLowerCase();

            switch (faultType) {
                case "node-crash":
                    faults.add(selectNodeCrashById(bf.getF_id()));
                    break;
                case "node-restart":
                    faults.add(selectNodeRestartById(bf.getF_id()));
                    break;
                case "cpu-stress-sc":
                    faults.add(selectCpuStressSidecarById(bf.getF_id()));
                    break;
                default:
                    logger.warn("Unknown fault type: " + faultType + " for fault id: " + bf.getF_id());
                    break;
            }
        }

        return faults;
    }

    @Override
    public int insertNodeCrash(NodeCrash nc) {
        logger.info("Inserting node crash fault with details: " + nc);

        String insertFault = "INSERT INTO fault(username, name, duration, scheduled_for, fault_type) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING f_id";
        Integer faultId = jdbcTemplate.queryForObject(insertFault, Integer.class,
                nc.getUsername(), nc.getName(), nc.getDuration(), nc.getScheduled_for(), nc.getFault_type());

        String insertNodeCrash = "INSERT INTO node_crash (f_id, num_nodes) VALUES (?, ?)";
        jdbcTemplate.update(insertNodeCrash, faultId, nc.getNum_nodes());

        return faultId;
    }

    @Override
    public int insertNodeRestart(NodeRestart nr) {
        logger.info("Inserting node restart fault with details: " + nr);

        String insertFault = "INSERT INTO fault(username, name, duration, scheduled_for, fault_type) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING f_id";
        Integer faultId = jdbcTemplate.queryForObject(insertFault, Integer.class,
                nr.getUsername(), nr.getName(), nr.getDuration(), nr.getScheduled_for(), nr.getFault_type());

        String insertNodeRestart = "INSERT INTO node_restart (f_id, num_nodes, frequency) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertNodeRestart, faultId, nr.getNum_nodes(), nr.getFrequency());

        return faultId;
    }

    @Override
    public int insertCpuStressSidecar(CpuStressSidecar css) {
        logger.info("Inserting cpu stress sidecar fault with details: " + css);

        String insertFault = "INSERT INTO fault(username, name, duration, scheduled_for, fault_type) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING f_id";
        Integer faultId = jdbcTemplate.queryForObject(insertFault, Integer.class,
                css.getUsername(), css.getName(), css.getDuration(), css.getScheduled_for(), css.getFault_type());

        String insertCpuStress = "INSERT INTO cpu_usage (f_id, num_threads) VALUES (?, ?)";
        jdbcTemplate.update(insertCpuStress, faultId, css.getNum_threads());

        return faultId;
    }

    @Override
    public Fault selectFaultByName(String name) {
        String selectFault = "SELECT * FROM fault WHERE name = ?";
        BaseFault baseFault = jdbcTemplate.queryForObject(selectFault, new Object[]{name}, new FaultMapper());

        if (baseFault == null) {
            throw new IllegalArgumentException("Fault with name " + name + " not found");
        }

        String faultType = baseFault.getFault_type().toLowerCase();

        switch (faultType) {
            case "node-crash":
                return selectNodeCrashById(baseFault.getF_id());

            case "node-restart":
                return selectNodeRestartById(baseFault.getF_id());

            case "cpu-stress-sc":
                return selectCpuStressSidecarById(baseFault.getF_id());

            default:
                throw new UnsupportedOperationException("Unknown fault type: " + faultType);
        }
    }

    private NodeCrash selectNodeCrashById(int faultId) {
        String sql = "SELECT fault.*, node_crash.num_nodes " +
                "FROM fault JOIN node_crash ON fault.f_id = node_crash.f_id " +
                "WHERE fault.f_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{faultId}, new NodeCrashMapper());
    }

    private NodeRestart selectNodeRestartById(int faultId) {
        String sql = "SELECT fault.*, node_restart.num_nodes, node_restart.frequency " +
                "FROM fault JOIN node_restart ON fault.f_id = node_restart.f_id " +
                "WHERE fault.f_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{faultId}, new NodeRestartMapper());
    }

    private CpuStressSidecar selectCpuStressSidecarById(int faultId) {
        String sql = "SELECT fault.*, cpu_usage.num_threads " +
                "FROM fault JOIN cpu_usage ON fault.f_id = cpu_usage.f_id " +
                "WHERE fault.f_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{faultId}, new CpuStressSidecarMapper());
    }

    @Override
    public NodeCrash selectNodeCrash(String name) {
        logger.info("Retrieved node crash fault with name " + name);
        String selectNodeCrash = "SELECT fault.f_id, fault.username, fault.name, fault.duration, " +
                "fault.scheduled_for, fault.fault_type, node_crash.num_nodes " +
                "FROM fault JOIN node_crash on fault.f_id = node_crash.f_id WHERE name = ?";
        return jdbcTemplate.queryForObject(selectNodeCrash, new Object[]{name}, new NodeCrashMapper());
    }

    @Override
    public NodeRestart selectNodeRestart(String name) {
        logger.info("Retrieved node restart fault with name " + name);
        String selectNodeRestart = "SELECT fault.f_id, fault.username, fault.name, fault.duration, " +
                "fault.scheduled_for, fault.fault_type, node_restart.num_nodes, node_restart.frequency " +
                "FROM fault JOIN node_restart on fault.f_id = node_restart.f_id WHERE name = ?";
        return jdbcTemplate.queryForObject(selectNodeRestart, new Object[]{name}, new NodeRestartMapper());
    }

    @Override
    public CpuStressSidecar selectCpuStressSidecar(String name) {
        logger.info("Retrieved cpu stress sidecar fault with name " + name);
        String selectCpuStressSidecar = "SELECT fault.f_id, fault.username, fault.name, fault.duration, " +
                "fault.scheduled_for, fault.fault_type, cpu_usage.num_threads " +
                "FROM fault JOIN cpu_usage on fault.f_id = cpu_usage.f_id WHERE name = ?";
        return jdbcTemplate.queryForObject(selectCpuStressSidecar, new Object[]{name}, new CpuStressSidecarMapper());
    }

    class FaultMapper implements RowMapper<BaseFault>{
        public BaseFault mapRow(ResultSet rs, int rowNum) throws SQLException{
            BaseFault bf = new BaseFault(
                    rs.getInt("f_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getInt("duration"),
                    rs.getInt("scheduled_for"),
                    rs.getString("fault_type")
            );
            return bf;
        }
    }

    class NodeCrashMapper implements RowMapper<NodeCrash> {
        public NodeCrash mapRow(ResultSet rs, int rowNum) throws SQLException{
            NodeCrash nc = new NodeCrash(
                    rs.getInt("f_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getInt("duration"),
                    rs.getInt("scheduled_for"),
                    rs.getString("fault_type"),
                    rs.getInt("num_nodes")
            );
            return nc;
        }
    }

    class NodeRestartMapper implements RowMapper<NodeRestart> {
        public NodeRestart mapRow(ResultSet rs, int rowNum) throws SQLException{
            NodeRestart nr = new NodeRestart(
                    rs.getInt("f_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getInt("duration"),
                    rs.getInt("scheduled_for"),
                    rs.getString("fault_type"),
                    rs.getInt("num_nodes"),
                    rs.getInt("frequency")
            );
            return nr;
        }
    }

    class CpuStressSidecarMapper implements RowMapper<CpuStressSidecar> {
        public CpuStressSidecar mapRow(ResultSet rs, int rowNum) throws SQLException{
            CpuStressSidecar css = new CpuStressSidecar(
                    rs.getInt("f_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getInt("duration"),
                    rs.getInt("scheduled_for"),
                    rs.getString("fault_type"),
                    rs.getInt("num_threads")
            );
            return css;
        }
    }
}


