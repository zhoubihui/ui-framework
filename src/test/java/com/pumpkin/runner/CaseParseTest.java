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
        CaseModel caseModel = Model.getModel("search-case", CaseModel.class);
        CaseRunnable caseRunnable = CaseParse.parseCase("search-case", caseModel);
        System.out.println(caseRunnable);
    }

    @Test
    void findCaseDependFile() {
        UrlConfigModel configModel = new UrlConfigModel().setPageUrl("page/search");
        String caseDependFile = CaseParse.findCaseDependFile(configModel, "case/search/search-case.yaml", CaseParse.CaseDependFile.PAGE);
        System.out.println(caseDependFile);
    }

    @Test
    void file() {
        String caseFileName = "case/search/search-case";
        File file = new File(caseFileName);
        System.out.println(file.getName());

        Path path = Paths.get(caseFileName);
        System.out.println(path.getFileName().toString());

        System.out.println(FilenameUtils.getBaseName(caseFileName));
    }
}