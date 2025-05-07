package com.trigg.fault_injection.Utilities;

import com.trigg.fault_injection.Model.FaultEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class FaultLog {
    private static List<FaultEvent> faultEvents = new CopyOnWriteArrayList<>();

    public void addEvent(String faultType, String container) {
        faultEvents.add(new FaultEvent(faultType, container, ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime()));
    }

    public List<FaultEvent> getEvents() {
        return faultEvents;
    }
}

