package com.example.TelegramBot;

import com.example.TelegramBot.Model.DailyDomains;
import com.example.TelegramBot.Service.DailyDomainsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@EnableScheduling
@Service
public class DailyEventBot {
    final CloseableHttpClient httpClient = HttpClients.createDefault();
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    DailyDomainsService dailyDomainsService;
    @Autowired
    private MessageDailyEventBot messageDailyEventBot;

    @Scheduled(fixedDelayString = "PT04M")
    public void getDaily_domains() {
        List<Object> list = new ArrayList<>();
        final HttpUriRequest httpUriRequest = new HttpGet("https://backorder.ru/json/?order=desc&expired=1&by=hotness&page=1&items=50");
        try (CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
            final HttpEntity entity = response.getEntity();
            String jsonString = EntityUtils.toString(entity);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<DailyDomains> dailyDomainsList = mapper.readValue(jsonString, new TypeReference<List<DailyDomains>>() {
            });
            dailyDomainsService.deleteAll();
            /*Изночально ключ был автоинкрементируемым, но тогда hibernate
             * кидает исключение на повторяющиеся клучи, при том что в аннотациях
             * есть явное указание на шаблон создания ключа из postger
             * так что делаю вывод что шаблон кидает повторяющееся ключи
             * т.к мы удаляем записи каждый день можно просто присвоить
             * первчиные ключи самостоятльно*/
            long id = 0;
            for (var i : dailyDomainsList) {
                i.setId(++id);
            }
            dailyDomainsService.createAll(dailyDomainsList);
            messageDailyEventBot.messageUsers();
            log.info("Записи новых доменов на сегодня были добавлены, общее количество этих записей составило: " + dailyDomainsService.countItems());
        } catch (ClientProtocolException ex) {
            log.info("Произошла ошибка при запросе на получение доменов - " + ex.getMessage());
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            log.info("Произошла ошибка в функции получения доменов - " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
