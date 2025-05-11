package com.trigg.fault_injection.Database;

import com.trigg.fault_injection.Model.*;
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
    int insertNetworkDelay(NetworkDelay nd);
    int insertBandwidthThrottle(BandwidthThrottle bt);

}
