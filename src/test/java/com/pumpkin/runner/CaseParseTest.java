package com.pumpkin.runner;

import com.pumpkin.model.Model;
import com.pumpkin.model.cases.CaseModel;
import org.junit.jupiter.api.Test;

class CaseParseTest {

    @Test
    void parseCase() {
        CaseModel caseModel = Model.getModel("search-case", CaseModel.class);
        CaseRunnable caseRunnable = CaseParse.parseCase("search-case", caseModel);
        System.out.println(caseRunnable);
    }
}