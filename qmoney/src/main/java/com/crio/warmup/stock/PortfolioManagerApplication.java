package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.crio.warmup.stock.portfolio.PortfolioManagerImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  // TODO: CRIO_TASK_MODULE_REST_API
  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>

  // static void curl(String url, File filename) throws IOException
  // {
  // FileWriter fw = new FileWriter(filename,true);
  // fw.append(url + "\n");
  // fw.close();
  // }

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    ObjectMapper objectMapper = getObjectMapper();
    File file1 = resolveFileFromResources(args[0]);// "trades.json")//new File("qmoney/src/main/resources/trades.json");
    PortfolioTrade[] trades = objectMapper.readValue(file1, PortfolioTrade[].class);
    Map<Double, String> map = new HashMap<>();

    for (PortfolioTrade trade : trades) {
      String url = new String("https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate="
          + trade.getPurchaseDate() + "&endDate=" + args[1] + "&token=dd4fbd0603076422786b55c847564b1f4aaea0ef");
      // File filename =new File("src/main/java/com/crio/warmup/stock/tingo_curl.sh");
      // curl(url,filename);
      RestTemplate rest = new RestTemplate();
      TiingoCandle[] tingo = rest.getForObject(url, TiingoCandle[].class);

      map.put(tingo[0].getClose(), trade.getSymbol());
    }

    Map<Double, String> treeMap = new TreeMap<>(map);
    List<String> ans = new ArrayList<>();
    for (Map.Entry<Double, String> entry : treeMap.entrySet()) {
      ans.add(entry.getValue());
    }

    return ans;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Read the json file provided in the argument[0]. The file will be available in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. Extract stock symbols from the json file with ObjectMapper provided by
  // #getObjectMapper.
  // 3. Return the list of all symbols in the same order as provided in json.

  // Note:
  // 1. There can be few unused imports, you will need to fix them to make the
  // build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    ObjectMapper objectMapper = getObjectMapper();

    File file1 = resolveFileFromResources(args[0]);
    PortfolioTrade[] trades = objectMapper.readValue(file1, PortfolioTrade[].class);

    List<String> ans = new ArrayList<String>();

    for (PortfolioTrade trade : trades) {
      ans.add(trade.getSymbol());
    }
    return ans;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/adityakumaarvishwakarma-ME_QMONEY/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@707194ba";
    String functionNameFromTestFileInStackTrace = "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "21";

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }

  // Note:
  // Remember to confirm that you are getting same results for annualized returns
  // as in Module 3.

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Once you are done with the implementation inside PortfolioManagerImpl and
  // PortfolioManagerFactory, create PortfolioManager using
  // PortfolioManagerFactory.
  // Refer to the code from previous modules to get the List<PortfolioTrades> and
  // endDate, and
  // call the newly implemented method in PortfolioManager to calculate the
  // annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns
  // as in Module 3.

  public static RestTemplate restTemplate = new RestTemplate();
  public static PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate) ; 

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args) throws Exception {
    String file = resolveFileFromResources(args[0]).toString();
    LocalDate endDate = LocalDate.parse(args[1]);
    String contents = readFileAsString(file);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
    return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }

  public static String convertStreamToString(InputStream is) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
      sb.append(line).append("\n");
    }
    reader.close();
    return sb.toString();
  }

  private static String readFileAsString(String file) throws Exception {
    File fl = new File(file);
    FileInputStream fin = new FileInputStream(fl);
    String ret = convertStreamToString(fin);
    fin.close();        
    return ret;
  }

  public static void main(String[] args) throws Exception {

    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    
   //  printJsonObject(mainCalculateSingleReturn(args));
    printJsonObject( mainCalculateReturnsAfterRefactor(args));
  }



// TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.
  
  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
        
    ObjectMapper objectMapper = getObjectMapper();
    File file1 = resolveFileFromResources(args[0]);
    PortfolioTrade[] trades = objectMapper.readValue(file1, PortfolioTrade[].class);
    List<AnnualizedReturn> list = new ArrayList<>();

    for (PortfolioTrade trade : trades) {
      String url=new String("https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+
      "/prices?startDate="+trade.getPurchaseDate()+"&endDate="+args[1]+
      "&token=dd4fbd0603076422786b55c847564b1f4aaea0ef");
      TiingoCandle[] tingo = new RestTemplate().getForObject(url, TiingoCandle[].class); 

      LocalDate date =LocalDate.parse(args[1]);
      AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(date,
      trade, tingo[0].getOpen(), tingo[tingo.length-1].getClose());
      
      list.add(annualizedReturn);
    }
    
    Comparator<AnnualizedReturn> SortByAnnReturn =Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
    Collections.sort(list,SortByAnnReturn);     
    //Collections.sort(list,Collections.reverseOrder());
    return list;
  }

  


  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
  PortfolioTrade trade, Double buyPrice, Double sellPrice) {

   Double totalReturns = (sellPrice - buyPrice) / buyPrice;
   Double total_num_years = trade.getPurchaseDate().until(endDate, ChronoUnit.DAYS)/365.24;
   Double annualized_returns = Math.pow(1 + totalReturns, 1.0/total_num_years) - 1;
   
  return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturns);
}

}

    



