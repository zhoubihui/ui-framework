package com.pumpkin.core;

import com.pumpkin.model.Model;
import com.pumpkin.model.cases.CaseModel;
import com.pumpkin.runner.CaseParse;
import com.pumpkin.runner.CaseRunnable;
import com.pumpkin.runner.structure.Env;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvManagerTest {

    @Test
    void getCaps() {
        CaseModel caseModel = Model.getModel("order-case", CaseModel.class);
        CaseParse.parseCase("order-case", caseModel);
        EnvManager manager = EnvManager.getInstance();
    }
}