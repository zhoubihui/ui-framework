package com.pumpkin.core;

import com.pumpkin.model.ICase;
import com.pumpkin.model.IModel;
import com.pumpkin.runner.CaseParse;
import com.pumpkin.utils.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

class EnvManagerTest {

    @Test
    void getCaps() {
        ICase.CaseModel caseModel = IModel.getModel("order-case", ICase.CaseModel.class);
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