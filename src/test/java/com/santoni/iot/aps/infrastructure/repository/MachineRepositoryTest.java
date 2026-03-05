package com.santoni.iot.aps.infrastructure.repository;

import com.santoni.iot.aps.domain.resource.repository.MachineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql({"/ddl.sql"})
public class MachineRepositoryTest {

    @Autowired
    private MachineRepository machineRepository;

    @Test
    public void testSaveMachine() {

    }

    @Test
    public void testFindByDeviceId() {

    }

    @Test
    public void testListById() {

    }
}
