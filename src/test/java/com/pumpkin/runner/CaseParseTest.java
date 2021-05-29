package com.pumpkin.runner;

import com.pumpkin.model.ICase;
import com.pumpkin.model.IModel;
import org.junit.jupiter.api.Test;

class CaseParseTest {

    @Test
    void parseCase() {
        ICase.CaseModel caseModel = IModel.getModel("case/search/search-case.yaml", ICase.CaseModel.class);
        CaseRunnable caseRunnable = CaseParse.parseCase("case/search/search-case.yaml", caseModel);
        System.out.println(caseRunnable);
    }
}