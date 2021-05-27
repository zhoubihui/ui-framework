package com.pumpkin.runner;

import com.pumpkin.exception.CallCaseMethodException;
import com.pumpkin.exception.CallPOMethodException;
import com.pumpkin.exception.NotMatchParameterException;
import com.pumpkin.model.EnvModel;
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
import com.pumpkin.utils.FileUtils;
import com.pumpkin.utils.ReflectUtils;
import com.pumpkin.utils.StringUtils;
import com.rits.cloning.Cloner;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
public class CaseParse {
    private final static String PARAM_REGEX = "\\((.+?)\\)";
    private final static String PARAM_SPLIT_REGEX = "\\$\\{(.+?)\\}";
    private final static Object PRESENT = new Object();
    private final static Cloner CLONER = new Cloner();

    /**
     * 解析CaseModel，转成CaseRunnable格式
     * @param caseModel
     * @return
     */
    public static CaseRunnable parseCase(String caseFileName, CaseModel caseModel) {
        /**
         * 1、遍历@BeforeAll方法
         * 2、遍历@BeforeEach方法
         * 3、遍历CaseModel.cases下多个case
         * 4、遍历@AfterEach方法
         * 5、遍历@AfterAll方法
         * 注意：@BeforeEach和@BeforeAll是放在CaseStructure结构中，一个CaseStructure代表一个xxx-case.yaml中定义的case，
         *  1) case的参数只有一组，那么CaseStructure.cases.size()==1
         *  2) case的参数有多组，那么CaseStructure.cases.size()>1
         *  将来还可以做某个case忽略指定的生命周期方法
         * 6、处理env环境配置问题
         */
        List<CaseInsensitiveMap<String, CaseMethodModel>> cases = caseModel.getCases();
        EnvModel envModel = caseModel.getEnv();

        List<CaseStructure> caseStructures = cases.stream().map(c -> transformCase(caseFileName, c)).
                collect(Collectors.toList());

        Env env = null;
        if (Objects.nonNull(envModel))
            env = Env.builder().platform(envModel.getPlatform()).targetApp(envModel.getTargetApp()).build();

        return CaseRunnable.builder().
                caseFileName(caseFileName).
                cases(caseStructures).
                env(env).
                build();
    }

    /**
     * 处理xxx-case.yaml中的methods下的每个method
     * 注意: case是关键字，所以这里变量改名为testCase
     * @param testCase
     */
    private static CaseStructure transformCase(String caseFileName, Map<String, CaseMethodModel> testCase) {
        /**
         * 处理CaseModel.cases下的case
         * 1、校验case.steps中引用的参数是否在case.params中定义
         * 2、校验case.asserts.expected中引用的参数是否在case.params中定义
         * 3、处理case，转成CaseMethod结构
         * 4、处理参数，如果有多组参数，转成多个CaseMethod
         *  注意:
         *      1) key是方法名
         *      2) Map的长度只会是1，并且也只会处理取出来的第一个元素
         *      3) xxx-case的方法params在xxx-data中获取，steps和assert中引用的参数都需要在params中定义,反之params中定义的参数
         *          在steps，asserts中不一定会使用
         *      4) xxx-page的po方法的params在xxx-case.steps中调用该po方法时传入，params定义的参数都需要从case中传入
         */

        Map.Entry<String, CaseMethodModel> entry = testCase.entrySet().iterator().next();
        String caseMethodName = entry.getKey();
        CaseMethodModel caseMethodModel = entry.getValue();

        //校验参数
        verifyCaseMethodStepsParams(caseFileName, caseMethodName, caseMethodModel.getParams(),
                caseMethodModel.getSteps());
        verifyCaseMethodAssertsParams(caseFileName, caseMethodName, caseMethodModel.getParams(),
                caseMethodModel.getAsserts());

        CaseMethod caseMethod = transformCaseMethod(caseFileName, caseMethodName, caseMethodModel);
        caseMethod.setName(caseMethodName); //存储case方法名

        //3、处理参数,从xxx-data中读取参数来生成完整的测试用例
        //3-1、处理用例的参数
        List<CaseMethod> caseMethods = replaceCaseParam(caseFileName, caseMethod);

        return CaseStructure.builder().cases(caseMethods).build();
    }

    /**
     * 解析CaseMethodModel
     * @param caseMethodModel
     */
    private static CaseMethod transformCaseMethod(String caseFileName, String caseMethodName, CaseMethodModel caseMethodModel) {
        List<String> params = caseMethodModel.getParams();
        List<String> caseSteps = caseMethodModel.getSteps();
        List<CaseAssertModel> asserts = caseMethodModel.getAsserts();
        /**
         * 1、获取steps中引用的全部参数
         * 2、获取asserts中的expected引用的全部参数
         * 3、处理steps
         * 4、处理asserts
         * 5、分别组装steps和asserts中引用的参数
         */
        Set<String> caseParams = caseSteps.stream().flatMap(caseStep -> splitMethodParam(caseStep).stream()).
                collect(Collectors.toSet());
        Set<String> assertParams = asserts.stream().map(a -> splitParam(a.getExpected())).collect(Collectors.toSet());

        List<PageObjectStructure> pageObjectStructures = caseSteps.stream().
                map(caseStep -> transformCaseStep(caseFileName, caseMethodName, caseStep)).
                collect(Collectors.toList());

        List<Assert> assertList = asserts.stream().map(CaseParse::transformCaseAssert).collect(Collectors.toList());

        CaseInsensitiveMap<String, Object> caseTrueData = new CaseInsensitiveMap<>();
        caseParams.forEach(p -> caseTrueData.put(p, PRESENT));
        CaseInsensitiveMap<String, Object> assertTrueData = new CaseInsensitiveMap<>();
        assertParams.forEach(p -> assertTrueData.put(p, PRESENT));

        return CaseMethod.builder().params(params).
                caseParams(caseParams).caseTrueData(caseTrueData).
                assertParams(assertParams).assertTrueData(assertTrueData).
                caseSteps(pageObjectStructures).
                asserts(assertList).build();
    }

    /**
     * 传入的每个step都是调用PO的方法，暂时不支持不写前缀
     * 注意：传入的格式有以下形式
     * ${message-page.to-search}
     * ${search-page.search(${keyword})}
     * ${search-page.search(${keyword}, ${replace})}
     * @param caseFileName
     * @param caseMethodName
     * @param step
     */
    private static PageObjectStructure transformCaseStep(String caseFileName, String caseMethodName, String step) {
        /**
         * 1、替换step中调用的PO方法
         * 2、读取PO方法体，校验case传递给PO的参数是否和PO中定义的参数格个数相同
         * 3、校验PO方法内部引用的变量是否在params中定义
         * 4、PO方法体转成ElementStructure结构
         */
        List<String> poMethods = splitFileAndMethod(step);
        String poFileName = poMethods.get(0);
        String poMethodName = poMethods.get(1);
        //case传给po的参数
        List<String> caseToPOData = splitMethodParam(step);

        /**
         * 先从缓存PageCache中找，找不到再读取文件
         */
        PageModel pageModel = Model.getModel(poFileName, PageModel.class);
        MethodModel methodModel = pageModel.getMethod(poMethodName);

        //po中定义的参数
        List<String> params = methodModel.getParams();
        verifyCallPOMethodParams(caseFileName, caseMethodName, poFileName, poMethodName, params, caseToPOData);

        verifyPOMethodParams(poFileName, poMethodName, params, methodModel.getSteps());

        List<ElementStructure> elementStructures = methodModel.getSteps().stream().map(CaseParse::transformPOStep).
                collect(Collectors.toList());
        return PageObjectStructure.builder().
                pageFileName(poFileName).name(poMethodName).
                params(params).caseToPOParams(caseToPOData).
                poSteps(elementStructures).build();
    }

    /**
     * 将PO方法中每一个元素定位和操作转成ElementStructure
     * @param poStep
     * @return
     */
    private static ElementStructure transformPOStep(ElementModel poStep) {
        /**
         * 1、处理selector，从xxx-selector.yaml中读取全部平台的定位符，等到具体运行时再根据平台选择某一个定位符
         * 2、处理action
         * 3、处理data
         */
        CaseInsensitiveMap<String, ElementSelector> elementSelectorMap = transformSelector(poStep.getSelector());
        String action = poStep.getAction();
        List<String> data = poStep.getData();

        List<String> dataTemp = null;
        if (Objects.nonNull(data))
            dataTemp = data.stream().map(CaseParse::splitParam).collect(Collectors.toList());
        else
            dataTemp = Collections.emptyList();
        return ElementStructure.builder().selectors(elementSelectorMap).action(action).data(dataTemp).build();
    }

    /**
     * 加载xxx-page.yaml中指定的定位符
     * @param selector
     * @return
     */
    private static CaseInsensitiveMap<String, ElementSelector> transformSelector(String selector) {
        CaseInsensitiveMap<String, ElementSelector> elementSelectorMap = new CaseInsensitiveMap<>();
        List<String> poMethods = splitFileAndMethod(selector);
        String selectorFileName = poMethods.get(0);
        String selectorName = poMethods.get(1);
        /**
         * 先从缓存SelectorCache中找，找不到再读取文件
         */
        SelectorModel selectorModel = Model.getModel(selectorFileName, SelectorModel.class);
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

    private static Assert transformCaseAssert(CaseAssertModel caseAssertModel) {
        return null;
    }

    /**
     * 将参数替换case、中的模板参数，有多组参数则生成多个CaseMethod返回
     * @param caseFileName
     * @param caseMethod
     * @return
     */
    private static List<CaseMethod> replaceCaseParam(String caseFileName, CaseMethod caseMethod) {
        String dataFileName = null;
                //findCaseDependFile(caseFileName, DATA);
        List<CaseMethod> caseMethods = new ArrayList<>();
        /**
         * 先从缓存DataCache中找，找不到再读取文件
         */
        DataModel dataModel = Model.getModel(dataFileName, DataModel.class);
        Map<String, List<Object>> methodData = dataModel.getMethodData(caseMethod.getName());

        /**
         * 1、校验xxx-data.yaml中定义的参数个数和xxx-case.yaml中params定义的参数个数、名称是否相同(顺序不要求相同)
         * 2、获取caseParams(这里已经排除了断言的参数)，在xxx-data.yaml中定义的最短长度，用来最后生成List<CaseMethod>
         *     如果case中需要多个参数，且这多个参数在xxx-data.yaml中定义的实际值长度不同，为了保证生成的case参数正确，只会使用最短长度，
         *     举例：
         *         case: 需要参数keyword,replace
         *         data: 定义keyword=[123,456,789],replace=[true,false]，那么最后只会取前两组的参数值
         * 3、根据最短长度，生成多个CaseMethod，分别替换CaseMethod内部的caseTrueData
         * 4、再次替换List<CaseMethod>内部的assertTrueData
         */
        Set<String> caseParams = caseMethod.getCaseParams();
        List<String> params = caseMethod.getParams();

        verifyCallCaseMethodParams(caseFileName, caseMethod.getName(), dataFileName, params, methodData);

        int dataMinLength = caseParams.stream().map(p -> methodData.get(p).size()).sorted().findFirst().orElse(0);
        Stream.iterate(0, index -> index + 1).limit(dataMinLength).forEach(
                index -> {
                    CaseMethod temp = CLONER.deepClone(caseMethod);
                    CaseInsensitiveMap<String, Object> caseTrueData = temp.getCaseTrueData();
                    caseTrueData.keySet().forEach(
                         key -> {
                             Object obj = methodData.get(key).get(index);
                             caseTrueData.put(key, obj);
                         }
                    );
                    CaseInsensitiveMap<String, Object> assertTrueData = temp.getAssertTrueData();
                    assertTrueData.keySet().forEach(
                            key -> {
                                Object obj = methodData.get(key).get(index);
                                assertTrueData.put(key, obj);
                            }
                    );
                    caseMethods.add(temp);
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
    private static List<String> splitFileAndMethod(String input) {
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
    private static String splitParam(String input) {
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
    private static List<String> splitMethodParam(String input) {
        String params = StringUtils.matcher(input, PARAM_REGEX);
        if (params.isBlank())
            return Collections.emptyList();
        return Arrays.stream(params.split(",\\s*")).map(
                CaseParse::splitParam
        ).collect(Collectors.toList());
    }

    /**
     * 校验case方法内的steps引用的变量是否在params部分定义，任一个不满足抛异常
     */
    private static void verifyCaseMethodStepsParams(String caseFileName, String caseMethodName,
                                        List<String> params, List<String> caseSteps) {
        if (!isTrueCaseMethodStepsParams(params, caseSteps))
            ExceptionUtils.throwAsUncheckedException(
                    new NotMatchParameterException(caseFileName, caseMethodName)
            );
    }

    /**
     * 校验case方法内的asserts引用的变量是否在params部分定义，任一个不满足则抛异常
     * 注意：目前只支持期望值判断
     */
    private static void verifyCaseMethodAssertsParams(String caseFileName, String caseMethodName,
                                               List<String> params, List<CaseAssertModel> asserts) {
        if (!isTrueCaseMethodAssertsParams(params, asserts))
            ExceptionUtils.throwAsUncheckedException(
                    new NotMatchParameterException(caseFileName, caseMethodName)
            );
    }

    /**
     * 校验po方法内引用的变量是否在params部分定义，任一个不满足抛异常
     */
    private static void verifyPOMethodParams(String poFileName, String poMethodName,
                                      List<String> params, List<ElementModel> poSteps) {
        if (!isTruePOMethodParams(params, poSteps))
            ExceptionUtils.throwAsUncheckedException(
                    new NotMatchParameterException(poFileName, poMethodName)
            );
    }

    /**
     * 校验case调用po方法时，传入的参数是否和po的params定义的参数个数相同，不满足则抛异常
     */
    private static void verifyCallPOMethodParams(String caseFileName, String caseName,
                                          String poFileName, String poMethodName,
                                          List<String> params, List<String> caseToPOParams) {
        if (Objects.nonNull(params) && caseToPOParams.size() != params.size())
            ExceptionUtils.throwAsUncheckedException(new CallPOMethodException(caseFileName, caseName, poFileName, poMethodName));
    }

    /**
     * 校验xxx-data.yaml文件中传给case的参数个数、参数名是否一致
     */
    private static void verifyCallCaseMethodParams(String caseFileName, String caseMethodName,
                                            String dataFileName,
                                            List<String> caseParams, Map<String, List<Object>> dataParams) {
        boolean paramTotal = caseParams.size() == dataParams.keySet().size();
        boolean paramName = caseParams.stream().allMatch(
                p -> Objects.nonNull(dataParams.get(p))
        );
        if (!(paramTotal && paramName))
            ExceptionUtils.throwAsUncheckedException(
                    new CallCaseMethodException(caseFileName, caseMethodName, dataFileName)
            );
    }

    /**
     * 判断case步骤中引用的参数在params是否有定义
     * @param params
     * @param caseSteps
     * @return
     */
    private static boolean isTrueCaseMethodStepsParams(List<String> params, List<String> caseSteps) {
        return caseSteps.stream().allMatch(
                s -> {
                    List<String> methodParam = splitMethodParam(s);
                    return params.containsAll(methodParam);
                }
        );
    }

    /**
     * 校验case中的asserts部分引用的参数在params是否有定义
     * @param params
     * @param asserts
     * @return
     */
    private static boolean isTrueCaseMethodAssertsParams(List<String> params, List<CaseAssertModel> asserts) {
        return asserts.stream().allMatch(
                a -> {
                    String param = splitParam(a.getExpected());
                    return params.contains(param);
                }
        );
    }

    /**
     * 判断PO方法中引用的参数在params是否有定义
     * @param params
     * @param poSteps
     * @return
     */
    private static boolean isTruePOMethodParams(List<String> params, List<ElementModel> poSteps) {
        return poSteps.stream().allMatch(
                e -> {
                    if (Objects.isNull(e.getData()))
                        return true;
                    List<String> dataList = e.getData().stream().map(CaseParse::splitParam).collect(Collectors.toList());
                    return params.containsAll(dataList);
                }
        );
    }

    /**
     * 转换case依赖的xxx-data、xxx-page、xxx-selector的文件名
     * @return
     */
    private static String findCaseDependFile(UrlConfig config, String caseFileName, CaseDependFile caseDependFile) {
        int endIndex = caseFileName.indexOf("-");
        String dependBaseUrl = getCaseDependUrl(config, caseDependFile);
        if (Objects.isNull(dependBaseUrl)) {
            //从global-config中找dependUrl
        }
        String prefix = caseFileName.substring(0, endIndex + 1);
        String dependFile = prefix + "-" + caseDependFile.getSuffix();
        /**
         * 1、先判断文件这个目录下能不能找到这个文件名的路径
         * 2、如果能找到，则使用
         * 3、找不到则使用global-config.yaml中对于目录的配置，再继续找这个文件名
         */
        //怎么找到相对路径
        String dependUrl = FileUtils.getFilePathFromDirectory(dependBaseUrl, dependFile);
        if (org.apache.commons.lang3.StringUtils.isBlank(dependUrl)) {
            //从global-config中找dependUrl
        }
        return prefix + caseDependFile.getSuffix();
    }

    /**
     * 通过反射获取依赖文件路径
     * @param config
     * @param caseDependFile
     * @return
     */
    private static String getCaseDependUrl(UrlConfig config, CaseDependFile caseDependFile) {
        Field field = ReflectUtils.findField(config.getClass(), caseDependFile.getFieldName()).orElseThrow();
        return ReflectUtils.getFieldValue(config, field).toString();
    }

    enum CaseDependFile {
        DATA("data", "pageUrl"),
        PAGE("page", "selectorUrl"),
        SELECTOR("selector", "dataUrl"),
        ;
        String[] aliases;
        CaseDependFile(String... alias) {
            this.aliases = alias;
        }

        public String getSuffix() {
            return aliases[0];
        }

        public String getFieldName() {
            return aliases[1];
        }
    }
}
