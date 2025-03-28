package com.trigg.fault_injection.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
        List<Fault> faults = new ArrayList<>();
        logger.info("Retrieved all faults");

        String selectAllFaults = "SELECT * FROM fault";
        faults.addAll(jdbcTemplate.query(selectAllFaults, new FaultMapper()));

        return faults;
    }

    @Override
    public int insertNodeCrash(NodeCrash nc) {
        logger.info("Inserting node crash fault with details: " + nc);

        String insertFault = "INSERT INTO fault(username, name, duration, scheduled_for, fault_type) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING f_id";
        Integer faultId = jdbcTemplate.queryForObject(insertFault, Integer.class,
                "admin", nc.getName(), nc.getDuration(), 12, nc.getFault_type());

        String insertNodeCrash = "INSERT INTO node_crash (f_id, num_nodes) VALUES (?, ?)";
        jdbcTemplate.update(insertNodeCrash, faultId, 1);

        return faultId;
    }

    @Override
    public int insertNodeRestart(NodeRestart nr) {
        logger.info("Inserting node restart fault with details: " + nr);

        String insertFault = "INSERT INTO fault(username, name, duration, scheduled_for, fault_type) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING f_id";
        Integer faultId = jdbcTemplate.queryForObject(insertFault, Integer.class,
                nr.getUsername(), nr.getName(), nr.getDuration(), nr.getScheduled_for(), nr.getFault_type());

        String insertNodeRestart = "INSERT INTO node_crash (f_id, num_nodes, frequency) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertNodeRestart, faultId, nr.getNum_nodes(), nr.getFrequency());

        return faultId;
    }

    @Override
    public Fault selectFaultByName(String faultName) {
        logger.info("Retrieved fault with name " + faultName);
        String selectFault = "SELECT * FROM fault WHERE name = ?";
        return jdbcTemplate.queryForObject(selectFault, new Object[]{faultName}, new FaultMapper());
    }

    @Override
    public void insertFault(Fault fault) {
        if(fault instanceof NodeCrash){
            insertNodeCrash((NodeCrash) fault);
        }
        else if(fault instanceof NodeRestart){
            insertNodeRestart((NodeRestart) fault);
        }
        else{
            logger.info("Could not insert fault");
        }

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
                "FROM fault JOIN node_restart on fault.f_id = node_crash.f_id WHERE name = ?";
        return jdbcTemplate.queryForObject(selectNodeRestart, new Object[]{name}, new NodeRestartMapper());
    }

    class FaultMapper implements RowMapper<Fault> {
        public Fault mapRow(ResultSet rs, int rownNum) throws SQLException{
            Fault fault = new Fault(
                    rs.getInt("f_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getInt("duration"),
                    rs.getInt("scheduled_for"),
                    rs.getString("fault_type")
            );
            return fault;
        }
    }

    class NodeCrashMapper implements RowMapper<NodeCrash> {
        public NodeCrash mapRow(ResultSet rs, int rownNum) throws SQLException{
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
        public NodeRestart mapRow(ResultSet rs, int rownNum) throws SQLException{
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
}


