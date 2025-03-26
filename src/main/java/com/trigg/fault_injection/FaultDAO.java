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
    Fault selectFaultByName(String faultName);
    void insertFault(Fault fault);

}
