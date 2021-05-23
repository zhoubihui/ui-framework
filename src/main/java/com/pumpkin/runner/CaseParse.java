package com.pumpkin.runner;

import com.pumpkin.core.NotMatchParameterException;
import com.pumpkin.model.Model;
import com.pumpkin.model.cases.CaseAssertModel;
import com.pumpkin.model.cases.CaseMethodModel;
import com.pumpkin.model.cases.CaseModel;
import com.pumpkin.model.data.DataModel;
import com.pumpkin.model.page.ElementModel;
import com.pumpkin.model.page.MethodModel;
import com.pumpkin.model.page.PageModel;
import com.pumpkin.model.selector.ElementSelectorModel;
import com.pumpkin.model.selector.SelectorModel;
import com.pumpkin.runner.structure.*;
import com.pumpkin.utils.ExceptionUtils;
import com.pumpkin.utils.StringUtils;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.*;
import java.util.stream.Collectors;

import static com.pumpkin.runner.CaseParse.CaseDependFile.DATA;

/**
 * @className: CaseParse
 * @description: 解析CaseModel,并处理内部结构
 * @author: pumpkin
 * @date: 2021/5/22 11:06 上午
 * @version: 1.0
 **/
@Data
@Builder
public class CaseParse {
    private final String PARAM_REGEX = "\\((.+?)\\)";
    private final String PARAM_SPLIT_REGEX = "\\$\\{(.+?)\\}";

    private String caseFileName;

    /**
     * 解析CaseModel，转成CaseRunnable格式
     * @param caseModel
     * @return
     */
    public CaseRunnable parseCase(CaseModel caseModel) {
        /**
         * 1、遍历caseModel.cases
         */
        List<CaseStructure> caseStructures = caseModel.getCases().stream().map(this::transformCase).
                collect(Collectors.toList());

        return CaseRunnable.builder().caseFileName(caseFileName).cases(caseStructures).build();
    }

    /**
     * 处理xxx-case.yaml中的methods下的每个method
     * 注意: case是关键字，所以这里变量改名为testCase
     * @param testCase
     */
    public CaseStructure transformCase(Map<String, CaseMethodModel> testCase) {
        /**
         * 注意：
         * 1、key是方法名
         * 2、Map的长度只会是1，并且也只会处理取出来的第一个元素
         */
        Map.Entry<String, CaseMethodModel> entry = testCase.entrySet().iterator().next();
        //1、存入方法名
        String methodName = entry.getKey();

        //2、处理CaseMethodModel,CaseStructure.cases
        CaseMethodModel caseMethodModel = entry.getValue();
        if (!isTrueParams(caseMethodModel.getParams(), caseMethodModel.getSteps()))
            ExceptionUtils.throwAsUncheckedException(
                    new NotMatchParameterException(entry.getKey(), caseMethodModel.getParams().toString())
            );
        CaseMethod cases = transformCaseMethod(entry.getValue());
        cases.setName(methodName); //存储case方法名

        //3、处理参数,从xxx-data中读取参数来生成完整的测试用例
        //3-1、处理用例的参数
        List<CaseMethod> caseMethods = replaceParam(findCaseDependFile(DATA), cases);
        /**
         * 1、xxx-case的方法params在xxx-data中获取，steps和assert中引用的参数都需要在params中定义，
         *      反之params中定义的参数在steps，asserts中不一定会使用
         * 2、xxx-page的po方法params在xxx-case.steps中调用该po方法时传入，params定义的参数都需要从case中传入，
         */

        return CaseStructure.builder().cases(caseMethods).build();
    }

    /**
     * 解析CaseMethodModel
     * @param caseMethodModel
     */
    public CaseMethod transformCaseMethod(CaseMethodModel caseMethodModel) {
        List<String> params = caseMethodModel.getParams();
        List<String> steps = caseMethodModel.getSteps();
        List<CaseAssertModel> asserts = caseMethodModel.getAsserts();
        //每个steps就是一个Case
        List<PageObjectStructure> pageObjectStructures = steps.stream().map(this::transformCaseStep).
                collect(Collectors.toList());

        //全部的断言
        List<Assert> assertList = asserts.stream().map(this::transformCaseAssert).collect(Collectors.toList());

        return CaseMethod.builder().params(params).steps(pageObjectStructures).asserts(assertList).build();
    }

    /**
     * 传入的每个step都是调用PO的方法，暂时不支持不写前缀
     * 注意：传入的格式有以下形式
     * ${message-page.to-search}
     * ${search(${keyword})}
     * ${search(${keyword}, ${replace})}
     * @param step
     */
    public PageObjectStructure transformCaseStep(String step) {
        /**
         * 1、替换step中调用的PO方法
         */
        List<String> poMethods = splitFileAndMethod(step);
        String filePath = poMethods.get(0);
        String methodName = poMethods.get(1);

        /**
         * 先从缓存PageCache中找，找不到再读取文件
         */
        PageModel pageModel = Model.getModel(filePath, PageModel.class);
        MethodModel methodModel = pageModel.getMethod(methodName);
        //1、切割参数
        List<String> params = methodModel.getParams();
        //2、方法体转ElementStructure
        List<ElementStructure> elementStructures = methodModel.getSteps().stream().map(this::transformPOStep).
                collect(Collectors.toList());
        return PageObjectStructure.builder().pageFileName(filePath).name(methodName).params(params).
                steps(elementStructures).build();
    }

    /**
     * 将PO方法中每一个元素定位和操作转成ElementStructure
     * @param poStep
     * @return
     */
    public ElementStructure transformPOStep(ElementModel poStep) {
        //1、处理selector
        CaseInsensitiveMap<String, ElementSelector> elementSelectorMap = transformSelector(poStep.getSelector());
        //2、处理action
        String action = poStep.getAction();
        //3、处理data
        List<String> data = poStep.getData();
        List<String> dataTemp = null;
        if (Objects.nonNull(data))
            dataTemp = data.stream().map(this::splitParam).collect(Collectors.toList());
        else
            dataTemp = Collections.emptyList();
        return ElementStructure.builder().selectors(elementSelectorMap).action(action).data(dataTemp).build();
    }

    /**
     * 加载xxx-page.yaml中指定的定位符
     * @param selector
     * @return
     */
    public CaseInsensitiveMap<String, ElementSelector> transformSelector(String selector) {
        CaseInsensitiveMap<String, ElementSelector> elementSelectorMap = new CaseInsensitiveMap<>();
        List<String> poMethods = splitFileAndMethod(selector);
        String filePath = poMethods.get(0);
        String selectorName = poMethods.get(1);
        /**
         * 先从缓存SelectorCache中找，找不到再读取文件
         */
        SelectorModel selectorModel = Model.getModel(filePath, SelectorModel.class);
        Map<String, ElementSelectorModel> elementSelectorModel = selectorModel.getSelector(selectorName);

        /**
         * 把多个平台的定位符，都存起来，到真正运行case时再根据driver获取对应平台的定位符
         */
        elementSelectorModel.forEach((key, temp) -> {
            ElementSelector elementSelector = ElementSelector.builder().
                    strategy(temp.getStrategy()).selector(temp.getSelector()).
                    index(temp.getIndex()).multiple(temp.isMultiple()).
                    build();
            elementSelectorMap.put(key, elementSelector);
        });
        return elementSelectorMap;
    }

    public Assert transformCaseAssert(CaseAssertModel caseAssertModel) {
        return null;
    }

    /**
     * 将参数替换case、page中的模板参数，有多组参数则生成多个case返回
     * @return
     */
    private List<CaseMethod> replaceParam(String dataFileName, CaseMethod caseMethod) {
        List<CaseMethod> caseMethods = new ArrayList<>();
        /**
         * 先从缓存DataCache中找，找不到再读取文件
         */
        DataModel dataModel = Model.getModel(dataFileName, DataModel.class);
        Map<String, List<Object>> methodData = dataModel.getMethodData(caseMethod.getName());
        List<String> params = caseMethod.getParams();

        //避免参数长度不等的问题，根据case中需要的参数选取定义参数值长度最短的
        int len = params.stream().map(p -> methodData.get(p).size()).sorted().findFirst().orElse(0);
        params.forEach(
                p -> {
                    List<Object> paramData = methodData.get(p);
                    List<PageObjectStructure> steps = caseMethod.getSteps();

                }
        );
        return caseMethods;
    }

    /**
     * 切割文件名和方法名
     * ${message-page.to-search}
     * ${search(${keyword})}
     * ${search(${keyword}, ${replace})}
     * @param input
     * @return
     */
    private List<String> splitFileAndMethod(String input) {
        int startIndex = input.indexOf("{");
        int endIndex = input.indexOf("(");
        if (endIndex == -1)
            endIndex = input.indexOf("}");
        String str = input.substring(startIndex + 1, endIndex);
        return Arrays.stream(str.split("\\.")).collect(Collectors.toList());
    }

    /**
     * 切割参数名，page中的data部分，或case中的方法参数经过切割后可以通过这个方法进一步切割
     * ${keyword}
     * @param input
     * @return
     */
    private String splitParam(String input) {
        return StringUtils.matcher(input, PARAM_SPLIT_REGEX);
    }

    /**
     * 切割参数名
     * ${message-page.to-search}
     * ${search(${keyword}, ${replace})}
     * ${message-page.to-search()}
     * @param input
     * @return
     */
    private List<String> splitMethodParam(String input) {
        String params = StringUtils.matcher(input, PARAM_REGEX);
        if (params.isBlank())
            return Collections.emptyList();
        return Arrays.stream(params.split(",\\s*")).map(
                this::splitParam
        ).collect(Collectors.toList());
    }

    /**
     * 判断case步骤中引用的参数在params是否有定义
     * @param params
     * @param steps
     * @return
     */
    private boolean isTrueParams(List<String> params, List<String> steps) {
        return steps.stream().allMatch(
                s -> {
                    List<String> methodParam = splitMethodParam(s);
                    return params.containsAll(methodParam);
                }
        );
    }

    /**
     * 转换case依赖的xxx-data、xxx-page、xxx-selector的文件名
     * @return
     */
    private String findCaseDependFile(CaseDependFile caseDependFile) {
        int endIndex = caseFileName.indexOf("-");
        String prefix = caseFileName.substring(0, endIndex + 1);
        return prefix + caseDependFile.getSuffix();
    }

    enum CaseDependFile {
        DATA("data"),
        PAGE("page"),
        SELECTOR("selector"),
        ;
        String alias;
        CaseDependFile(String alias) {
            this.alias = alias;
        }

        public String getSuffix() {
            return alias;
        }
    }
}
