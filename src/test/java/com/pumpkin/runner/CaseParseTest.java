package com.pumpkin.runner;

import com.pumpkin.model.ICase;
import com.pumpkin.model.IModel;
import org.junit.jupiter.api.Test;

class CaseParseTest {

    @Test
    void parseCase() {
        ICase.CaseModel caseModel = IModel.getModel("case/search-case.yaml", ICase.CaseModel.class);
        ICaseRunnable.CaseRunnable caseRunnable = CaseParse.parseCase("case/search-case.yaml", caseModel);
        CaseRunner.runCase(caseRunnable);
    }

    @Test
    void scroll() {
        String caseFileName = "case/address-case.yaml";
        ICase.CaseModel caseModel = IModel.getModel(caseFileName, ICase.CaseModel.class);
        ICaseRunnable.CaseRunnable caseRunnable = CaseParse.parseCase(caseFileName, caseModel);
        CaseRunner.runCase(caseRunnable);
    }
}