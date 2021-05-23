package com.pumpkin.runner;

import com.pumpkin.model.Model;
import com.pumpkin.model.cases.CaseModel;
import com.pumpkin.model.page.ElementModel;
import com.pumpkin.runner.structure.ElementStructure;
import com.pumpkin.runner.structure.PageObjectStructure;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class CaseParseTest {

    @Test
    void transformPOStep() {
        ElementModel elementModel = new ElementModel().setSelector("${search-selector.search}").setAction("input").
                setData(Arrays.asList("${keyword}", "${replace}"));
        CaseParse caseParse = CaseParse.builder().build();
        ElementStructure elementStructure = caseParse.transformPOStep(elementModel);
        System.out.println(elementStructure);
    }

    @Test
    void transformCaseStep() {
        String step = "${message-page.to-search}";
        CaseParse caseParse = CaseParse.builder().build();
        PageObjectStructure pageObjectStructure = caseParse.transformCaseStep(step);
        System.out.println(pageObjectStructure);
    }

    @Test
    void parseCase() {
        CaseModel caseModel = Model.getModel("search-case", CaseModel.class);
        CaseParse caseParse = CaseParse.builder().caseFileName("search-case").build();
        CaseRunnable caseRunnable = caseParse.parseCase(caseModel);
        System.out.println(caseRunnable);
    }
}