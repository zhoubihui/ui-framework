package com.pumpkin.runner;

import com.pumpkin.model.Model;
import com.pumpkin.model.UrlConfigModel;
import com.pumpkin.model.cases.CaseModel;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

class CaseParseTest {

    @Test
    void parseCase() {
        CaseModel caseModel = Model.getModel("case/search/search-case.yaml", CaseModel.class);
        CaseRunnable caseRunnable = CaseParse.parseCase("case/search/search-case.yaml", caseModel);
        System.out.println(caseRunnable);
    }

    @Test
    void findCaseDependFile() {
        UrlConfigModel configModel = new UrlConfigModel().setPageUrl("page/search");
        String caseDependFile = CaseParse.findCaseDependFile(configModel, "case/search/search-case.yaml", CaseParse.CaseDependFile.PAGE);
        System.out.println(caseDependFile);
    }
}