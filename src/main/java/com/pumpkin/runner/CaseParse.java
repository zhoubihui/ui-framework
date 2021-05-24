package com.pumpkin.runner;

import com.pumpkin.core.CallPOMethodException;
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
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final static Object PRESENT = new Object();

    private String caseFileName;

    /**
     * 解析CaseModel，转成CaseRunnable格式
     * @param caseModel
     * @return
     */
    public CaseRunnable parseCase(String caseFileName, CaseModel caseModel) {
        /**
         * 1、遍历caseModel.cases
         */
        List<CaseStructure> caseStructures = caseModel.getCases().stream().map(c -> transformCase(caseFileName, c)).
                collect(Collectors.toList());

        return CaseRunnable.builder().caseFileName(caseFileName).cases(caseStructures).build();
    }

    /**
     * 处理xxx-case.yaml中的methods下的每个method
     * 注意: case是关键字，所以这里变量改名为testCase
     * @param testCase
     */
    public CaseStructure transformCase(String caseFileName, Map<String, CaseMethodModel> testCase) {
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
        CaseMethod caseMethod = transformCaseMethod(caseFileName, methodName, entry.getValue());
        caseMethod.setName(methodName); //存储case方法名

        //3、处理参数,从xxx-data中读取参数来生成完整的测试用例
        //3-1、处理用例的参数
        List<CaseMethod> caseMethods = replaceCaseParam(caseFileName, caseMethod);
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
    public CaseMethod transformCaseMethod(String caseFileName, String caseName, CaseMethodModel caseMethodModel) {
        List<String> params = caseMethodModel.getParams();
        List<String> caseSteps = caseMethodModel.getSteps();
        List<CaseAssertModel> asserts = caseMethodModel.getAsserts();
        //获取steps中全部引用的参数,在后续参数替换时需要使用
        Set<String> paramsSet = caseSteps.stream().flatMap(caseStep -> splitMethodParam(caseStep).stream()).collect(Collectors.toSet());

        //每个steps就是一个Case
        List<PageObjectStructure> pageObjectStructures = caseSteps.stream().
                map(caseStep -> transformCaseStep(caseFileName, caseName, caseStep)).
                collect(Collectors.toList());

        //全部的断言
        List<Assert> assertList = asserts.stream().map(this::transformCaseAssert).collect(Collectors.toList());

        //将case的参数（即params部分）组装到Map中
        CaseInsensitiveMap<String, Object> trueData = new CaseInsensitiveMap<>();
        params.forEach(p -> trueData.put(p, PRESENT));

        return CaseMethod.builder().caseParams(paramsSet).trueData(trueData).caseSteps(pageObjectStructures).
                asserts(assertList).build();
    }

    /**
     * 传入的每个step都是调用PO的方法，暂时不支持不写前缀
     * 注意：传入的格式有以下形式
     * ${message-page.to-search}
     * ${search(${keyword})}
     * ${search(${keyword}, ${replace})}
     * @param step
     */
    public PageObjectStructure transformCaseStep(String caseFileName, String caseName, String step) {
        /**
         * 1、替换step中调用的PO方法
         */
        List<String> poMethods = splitFileAndMethod(step);
        String poFileName = poMethods.get(0);
        String poMethod = poMethods.get(1);
        List<String> caseToPOData = splitMethodParam(step); //case传给po的参数

        /**
         * 先从缓存PageCache中找，找不到再读取文件
         */
        PageModel pageModel = Model.getModel(poFileName, PageModel.class);
        MethodModel methodModel = pageModel.getMethod(poMethod);
        //1、判断case传递的参数个数是否和PO定义的参数个数相同
        List<String> params = methodModel.getParams(); //po中定义的参数
        if (caseToPOData.size() != params.size())
            throw ExceptionUtils.throwAsUncheckedException(new CallPOMethodException(caseFileName, caseName, poFileName, poMethod));
        //2、方法体转ElementStructure
        List<ElementStructure> elementStructures = methodModel.getSteps().stream().map(this::transformPOStep).
                collect(Collectors.toList());
        return PageObjectStructure.builder().pageFileName(poFileName).name(poMethod).params(params).
                poSteps(elementStructures).build();
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
     * 将参数替换case、中的模板参数，有多组参数则生成多个CaseMethod返回
     * @return
     */
    private List<CaseMethod> replaceCaseParam(String caseFileName, CaseMethod caseMethod) {
        String dataFileName = findCaseDependFile(caseFileName, DATA);
        List<CaseMethod> caseMethods = new ArrayList<>();
        /**
         * 先从缓存DataCache中找，找不到再读取文件
         */
        DataModel dataModel = Model.getModel(dataFileName, DataModel.class);
        Map<String, List<Object>> methodData = dataModel.getMethodData(caseMethod.getName());

        Set<String> caseParams = caseMethod.getCaseParams();
        //case.steps传递给PO的多个参数长度需要相同，长度不同则取最短的,对断言的参数无要求
        /**
         * 1、获取case中需要的参数（排除断言的参数），xxx-data.yaml中定义的最短长度，用来最后生成List<CaseMethod>
         */
        int dataMinLength = caseParams.stream().map(p -> methodData.get(p).size()).sorted().findFirst().orElse(0);
        /**
         * 2、如果case中需要多个参数，且这多个参数在xxx-data.yaml中定义的实际值长度不同，为了保证生成的case参数正确，只会使用最短长度，举例：
         *  case: 需要参数keyword,replace
         *  data: 定义keyword=[123,456,789],replace=[true,false]，那么最后只会取前两组的参数值
         */
        Stream.iterate(0, index -> index + 1).limit(dataMinLength).forEach(
                index -> {
                    CaseMethod temp = SerializationUtils.clone(caseMethod);
                    CaseInsensitiveMap<String, Object> trueData = temp.getTrueData();
                    trueData.keySet().stream().forEach(
                         key -> {
                             Object obj = methodData.get(key).get(index);

                         }
                    );
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
    private String findCaseDependFile(String caseFileName, CaseDependFile caseDependFile) {
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
