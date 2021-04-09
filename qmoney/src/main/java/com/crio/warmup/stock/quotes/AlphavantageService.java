
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {
  public static final String TOKEN = "1QNC8M68HIHAMTF6";
  public static final String FUNCTION = "TIME_SERIES_DAILY";

  private RestTemplate restTemplate;

  protected AlphavantageService(RestTemplate restTemplate)
  {
    this.restTemplate = restTemplate;
  }
  protected String buildUrl(String symbol)
  {
     String uriTemplate = String.format("https://www.alphavantage.co/query?function=%s&symbol=%s&output=full&apikey=%s"
     , FUNCTION, symbol, TOKEN);
      return uriTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
     String url = buildUrl(symbol);
    //  String apiResponse = restTemplate.getForObject(url, String.class);

    //  ObjectMapper mapper = getObjectMapper();
     
    //  Map<LocalDate, AlphavantageCandle> dailyResponses = mapper.readValue(apiResponse, AlphavantageDailyResponse.class).getCandles();
     
     Map<LocalDate, AlphavantageCandle> dailyResponses = restTemplate.getForObject(url,AlphavantageDailyResponse.class).getCandles();

     List<Candle> stocks = new ArrayList<>();

     for(LocalDate date = from; !date.isAfter(to); date = date.plusDays(1))
     {
       AlphavantageCandle candle = dailyResponses.get(date);
       if(candle!=null)
       {
         candle.setDate(date);
         stocks.add(candle);
       }
     }
    return stocks;
  }
   
  protected ObjectMapper getObjectMapper(){
    return new ObjectMapper();
  }
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  // Note:
  // 1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  // 2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.

}

