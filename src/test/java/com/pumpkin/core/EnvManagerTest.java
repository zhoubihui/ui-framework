package com.pumpkin.core;

import com.pumpkin.model.Model;
import com.pumpkin.model.cases.CaseModel;
import com.pumpkin.runner.CaseParse;
import com.pumpkin.utils.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

class EnvManagerTest {

    @Test
    void getCaps() {
        CaseModel caseModel = Model.getModel("order-case", CaseModel.class);
        CaseParse.parseCase("order-case", caseModel);
        EnvManager manager = EnvManager.getInstance();
    }

    @Test
    void parentDirectory() {
        String caseFileName = FileUtils.getFilePathFromDirectory("case/search", "search-case");
        String parentDirectory = FilenameUtils.getPathNoEndSeparator(caseFileName);
        System.out.println(parentDirectory);
        parentDirectory = FilenameUtils.getPathNoEndSeparator(parentDirectory);
        System.out.println(parentDirectory);
        parentDirectory = FilenameUtils.getPathNoEndSeparator(parentDirectory);
        if (parentDirectory.isBlank()) {
            System.out.println("最底层目录");
        }
        System.out.println(parentDirectory);

        System.out.println(FileUtils.getResource(""));
    }

    @Test
    void findFile() {

    }
}