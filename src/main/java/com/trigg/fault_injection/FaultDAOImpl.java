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
        return 0;
    }

    @Override
    public int insertNodeRestart(NodeRestart nr) {
        return 0;
    }

    @Override
    public Fault selectFaultByName(String faultName) {
        return null;
    }

    class FaultMapper implements RowMapper<Fault> {
        public Fault mapRow(ResultSet rs, int rownNum) throws SQLException{
            Fault fault = new Fault(
                    rs.getInt("f_id"),
                    rs.getInt("u_id"),
                    rs.getString("name"),
                    rs.getInt("duration"),
                    rs.getInt("scheduled_for"),
                    rs.getString("fault_type")
            );
            return fault;
        }
    }
}


