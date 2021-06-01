package com.pumpkin.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

/**
 * @className: IElementHelper
 * @description: TODO 类描述
 * @author: pumpkin
 * @date: 2021/6/1
 * @version: 1.0
 **/
public interface IElementHelper {
    List<WebElement> findElements(By by, boolean multiple, int index);

    Object operateElement(String pageFileName, List<WebElement> elements, String action, List<String> data,
                          Map<String, Object> poTrueData, boolean multiple, int index);
}
