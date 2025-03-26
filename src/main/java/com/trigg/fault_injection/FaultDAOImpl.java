package com.trigg.fault_injection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.xml.transform.Result;
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

        String selectAllFaults = "SELECT * FROM fault_scenario";
        faults.addAll(jdbcTemplate.query(selectAllFaults, new FaultMapper()));

        return faults;
    }



    @Override
    public int insertNodeCrash(NodeCrash nc) {
        logger.info("Inserting node crash fault with details: " + nc);

        String insertFault = "INSERT INTO fault_scenario (username, name, duration, scheduled_for, fault_type) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING f_id";
        Integer faultId = jdbcTemplate.queryForObject(insertFault, Integer.class,
                "admin", nc.getName(), nc.getDuration(), 12, nc.getFault_type());

        String insertNodeCrash = "INSERT INTO node_crash (f_id, num_nodes) VALUES (?, ?)";
        jdbcTemplate.update(insertNodeCrash, faultId, 3);

        return faultId;
    }

    @Override
    public int insertNodeRestart(NodeRestart nr) {
        logger.info("Inserting node restart fault with details: " + nr);

        String insertFault = "INSERT INTO fault_scenario (username, name, duration, scheduled_for, fault_type) " +
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
}


