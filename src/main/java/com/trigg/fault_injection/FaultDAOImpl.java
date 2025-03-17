package com.trigg.fault_injection;

import javax.sql.DataSource;
import java.util.List;

public class FaultDAOImpl implements FaultDAO {
    @Override
    public void setDataSource(DataSource dataSource) {

    }

    @Override
    public List<Fault> selectAllFaults() {
        return List.of();
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
}
