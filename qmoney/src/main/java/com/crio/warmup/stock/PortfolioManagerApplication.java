package com.crio.warmup.stock;

//import com.crio.warmup.stock.dto.AnnualizedReturn;
//import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;

import java.io.IOException;
import java.net.URISyntaxException;

// import java.nio.file.Files;
import java.lang.Object;
// import java.io.FileOutputStream; 


import java.io.BufferedReader;

//import java.io.IOException;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.nio.file.Paths;
import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;
import java.util.UUID;
//import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
//import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  static void curl(String symbol,String fileString) throws MalformedURLException, ProtocolException, IOException
  {
    System.out.println(symbol);
   
    LocalDate date =LocalDate.now();

    String urlString=new String("https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?endDate="+date+"&token=dd4fbd0603076422786b55c847564b1f4aaea0ef");

   // Files.write(Paths.get(pathString), urlString.getBytes(), StandardOpenOption.APPEND);

   PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileString, true)));
   out.println(urlString);
   out.close();

    //create url
    URL url = new URL(urlString);
    
		// Send Get request and fetch data
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		// Read data line-by-line from buffer & print it out

    String output;
    
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
    
		conn.disconnect();

  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Read the json file provided in the argument[0]. The file will be available in the classpath.
  //    1. Use #resolveFileFromResources to get actual file from classpath.
  //    2. Extract stock symbols from the json file with ObjectMapper provided by #getObjectMapper.
  //    3. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    ObjectMapper objectMapper = getObjectMapper();
     
    File file1 = resolveFileFromResources(args[0]);
    Trade[] trades = objectMapper.readValue(file1, Trade[].class);
     
    List<String> ans=new ArrayList<String>();

    File file = new File("qmoney/src/main/java/com/crio/warmup/stock/tingo_curl.sh");

    for (Trade trade : trades) {
         ans.add(trade.symbol);
         //System.out.println(trade.symbol);
         curl(trade.symbol, file.toString());
          
    //      String filename= "MyFile.txt";
    // FileWriter fw = new FileWriter(filename,true); //the true will append the new data
    // fw.write("add a line\n");//appends the string to the file
    //    fw.close();

    }
    return ans;

  }


  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.





  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));

   //   System.out.print(object);

  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

      String valueOfArgument0 = "trades.json";
      String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/adityakumaarvishwakarma-ME_QMONEY/qmoney/bin/main/trades.json";
      String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@707194ba";
      String functionNameFromTestFileInStackTrace = "mainReadFile";
      String lineNumberFromTestFileInStackTrace = "21";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.



  public static void main(String[] args) throws Exception {

    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadFile(args));//

  }
}

