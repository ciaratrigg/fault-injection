package com.trigg.fault_injection.Database;

import com.trigg.fault_injection.Model.CpuStressSidecar;
import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Model.NodeCrash;
import com.trigg.fault_injection.Model.NodeRestart;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public interface FaultDAO {
    void setDataSource(DataSource dataSource);
    List<Fault> selectAllFaults();
    int insertNodeCrash(NodeCrash nc);
    int insertNodeRestart(NodeRestart nr);
    int insertCpuStressSidecar(CpuStressSidecar css);
    Fault selectFaultByName(String faultName);
    NodeCrash selectNodeCrash(String name);
    NodeRestart selectNodeRestart(String name);
    CpuStressSidecar selectCpuStressSidecar(String name);
}
