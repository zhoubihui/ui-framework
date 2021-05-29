package com.pumpkin.model;

import org.junit.jupiter.api.Test;

class ModelTest {

    @Test
    void getModel() {
        ICase.CaseModel model = IModel.getModel("search-case", ICase.CaseModel.class);
        System.out.println(model);
    }
}