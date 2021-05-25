package com.pumpkin.runner;

import com.pumpkin.model.Model;
import com.pumpkin.model.cases.CaseModel;
import com.pumpkin.model.page.ElementModel;
import com.pumpkin.runner.structure.ElementStructure;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class CaseParseTest {

    @Test
    void transformPOStep() {
        ElementModel elementModel = new ElementModel().setSelector("${search-selector.search}").setAction("input").
                setData(Arrays.asList("${keyword}", "${replace}"));
        CaseParse caseParse = new CaseParse();
        ElementStructure elementStructure = caseParse.transformPOStep(elementModel);
        System.out.println(elementStructure);
    }

    @Test
    void parseCase() {
        CaseModel caseModel = Model.getModel("search-case", CaseModel.class);
        CaseParse caseParse = new CaseParse();
        CaseRunnable caseRunnable = caseParse.parseCase("search-case", caseModel);
        System.out.println(caseRunnable);
    }
}