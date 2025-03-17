package com.trigg.fault_injection;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public interface FaultDAO {
    void setDataSource(DataSource dataSource);
    List<Fault> selectAllFaults();
    int insertNodeCrash(NodeCrash nc);
    int insertNodeRestart(NodeRestart nr);

    //column for fault type in main fault table to do the join?
    Fault selectFaultByName(String faultName);

}
