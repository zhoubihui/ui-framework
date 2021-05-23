package com.pumpkin.model;

import com.pumpkin.model.cases.CaseModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void getModel() {
        CaseModel model = Model.getModel("search-case", CaseModel.class);
        System.out.println(model);
    }
}