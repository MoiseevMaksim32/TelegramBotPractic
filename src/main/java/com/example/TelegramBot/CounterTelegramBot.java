package com.example.TelegramBot;

import com.example.TelegramBot.DTO.MessagesDTO;
import com.example.TelegramBot.Model.DailyDomains;
import com.example.TelegramBot.Model.Users;
import com.example.TelegramBot.Service.DailyDomainsService;
import com.example.TelegramBot.Service.MessagesService;
import com.example.TelegramBot.Service.UsersService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

@Slf4j
@Component
public class CounterTelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    final CloseableHttpClient httpClient = HttpClients.createDefault();
    @Autowired
    private UsersService usersService;
    @Autowired
    private MessagesService messagesService;
    @Autowired
    private DailyDomainsService dailyDomainsService;

    public CounterTelegramBot(BotConfig botConfig){
        this.config = botConfig;
        DailyEventBot dailyEventBot = new DailyEventBot();
        dailyEventBot.bot = this;
        ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
        scheduled.scheduleAtFixedRate(dailyEventBot,0,24, TimeUnit.HOURS);
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    public String getBotToken(){
        return config.getBotToken();
    }
    @Override
    public void onUpdateReceived(@NotNull Update update) {
        long chatId = 0;
        long userId = 0;
        String userName = null;
        String receivedMessage;
        log.info("Пользователь написал сообщение");
      if(update.hasMessage()){
          String messageText = update.getMessage().getText();
          chatId = update.getMessage().getChatId();
          userId = update.getMessage().getFrom().getId();
          userName = update.getMessage().getFrom().getFirstName();

          if(update.getMessage().hasText()){
              receivedMessage = update.getMessage().getText();
              botAnswerUtils(receivedMessage,chatId,userId,userName);
          }
      }
    }

    private void botAnswerUtils(String mess, long chatId,long id_user, String userName){
        Users users = new Users(id_user,userName,mess);
        MessagesDTO messagesDTO = new MessagesDTO(users.getId(),mess);
        switch (mess){
            case "/start":
                log.info("Пользователь использовал \"старт\"");
                startBot(chatId,userName,id_user);
                createUser(users);
                messagesService.create(messagesDTO);
                break;
            default:
                writeMessage(chatId,userName,id_user);
                usersService.update(users);
                messagesService.create(messagesDTO);
                break;
        }
    }

    private void startBot(long chatId, String userName,long id_user){
        log.info("Пользователь с id - "+id_user+" начал диалог с ботом");
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет, "+userName+" я собираю все ваши сообщения и отправляю их на локальный сервер");
        try{
            execute(message);
            log.info("Запуск бота");
        }catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void writeMessage(long chatId,String userName,long userId){
        log.info("Пользователь с id - "+userId+" написал новое сообщение");
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Мы приняли ваше сообщение, "+userName);
        try{
            execute(message);
            log.info("Анализ записи");
        }catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void createUser(Users newUser) {
        try {
            if(usersService.readById(newUser.getId()) == null);
            usersService.create(newUser);
            log.info("Создан новый пользователь с id - " + newUser.getId());
        } catch (Exception e) {
            usersService.update(newUser);
            log.info("Пользовательс с id был обновлён: " + newUser.getId());
        }
    }
    public void getDaily_domains(){
        List<Object> list = new ArrayList<>();
        final HttpUriRequest httpUriRequest = new HttpGet("https://backorder.ru/json/?order=desc&expired=1&by=hotness&page=1&items=50");
        try (CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
            final HttpEntity entity = response.getEntity();
            String jsonString = EntityUtils.toString(entity);
            Gson g = new Gson();
            Type listType = new TypeToken<ArrayList<Object>>() {}.getType();
            list = g.fromJson(jsonString, listType);
            DailyDomains dailyDomains;
            List<DailyDomains> dailyDomainsList = new ArrayList<>();
            for(var i: list){
                String[] masStr = i.toString().split(",");
                switch (masStr.length) {
                    case 9:
                        dailyDomains = new DailyDomains(
                                (masStr[0].substring(masStr[0].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[1].substring(masStr[1].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[2].substring(masStr[2].lastIndexOf("=")+1)),
                                0,
                                0,
                                0,
                                0,
                                (masStr[3].substring(masStr[3].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[4].substring(masStr[4].lastIndexOf("=")+1)),
                                (masStr[5].substring(masStr[5].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[6].substring(masStr[6].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[7].substring(masStr[7].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[8].substring(masStr[8].lastIndexOf("=")+1,masStr[8].length()-1))
                        );
                        dailyDomainsList.add(dailyDomains);
                        break;
                    case 10:
                        dailyDomains = new DailyDomains(
                                (masStr[0].substring(masStr[0].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[1].substring(masStr[1].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[2].substring(masStr[2].lastIndexOf("=")+1)),
                                0,
                                0,
                                (int)Double.parseDouble(masStr[3].substring(masStr[3].lastIndexOf("=")+1)),
                                0,
                                (masStr[4].substring(masStr[4].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[5].substring(masStr[5].lastIndexOf("=")+1)),
                                (masStr[6].substring(masStr[6].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[7].substring(masStr[7].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[8].substring(masStr[8].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[9].substring(masStr[9].lastIndexOf("=")+1,masStr[9].length()-1))
                        );
                        dailyDomainsList.add(dailyDomains);
                        break;
                    case 11:
                        dailyDomains = new DailyDomains(
                                (masStr[0].substring(masStr[0].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[1].substring(masStr[1].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[2].substring(masStr[2].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[3].substring(masStr[3].lastIndexOf("=")+1)),
                                0,
                                0,
                                (int)Double.parseDouble(masStr[4].substring(masStr[4].lastIndexOf("=")+1)),
                                (masStr[5].substring(masStr[5].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[6].substring(masStr[6].lastIndexOf("=")+1)),
                                (masStr[7].substring(masStr[7].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[8].substring(masStr[8].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[9].substring(masStr[9].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[10].substring(masStr[10].lastIndexOf("=")+1,masStr[10].length()-1))
                        );
                        dailyDomainsList.add(dailyDomains);
                        break;
                    case 12:
                        dailyDomains = new DailyDomains(
                                (masStr[0].substring(masStr[0].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[1].substring(masStr[1].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[2].substring(masStr[2].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[3].substring(masStr[3].lastIndexOf("=")+1)),
                                0,
                                (int)Double.parseDouble(masStr[4].substring(masStr[4].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[5].substring(masStr[5].lastIndexOf("=")+1)),
                                (masStr[6].substring(masStr[6].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[7].substring(masStr[7].lastIndexOf("=")+1)),
                                (masStr[8].substring(masStr[8].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[9].substring(masStr[9].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[10].substring(masStr[10].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[11].substring(masStr[11].lastIndexOf("=")+1,masStr[11].length()-1))
                        );
                        dailyDomainsList.add(dailyDomains);
                        break;
                    case 13:
                        dailyDomains = new DailyDomains(
                                (masStr[0].substring(masStr[0].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[1].substring(masStr[1].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[2].substring(masStr[2].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[3].substring(masStr[3].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[4].substring(masStr[4].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[5].substring(masStr[5].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[6].substring(masStr[6].lastIndexOf("=")+1)),
                                (masStr[7].substring(masStr[7].lastIndexOf("=")+1)),
                                (int)Double.parseDouble(masStr[8].substring(masStr[8].lastIndexOf("=")+1)),
                                (masStr[9].substring(masStr[9].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[10].substring(masStr[10].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[11].substring(masStr[11].lastIndexOf("=")+1)),
                                Boolean.parseBoolean(masStr[12].substring(masStr[12].lastIndexOf("=")+1,masStr[12].length()-1))
                        );
                        dailyDomainsList.add(dailyDomains);
                        break;
                    default:
                        log.error("Не удаёться десериализовать стороку т.к. она имеен не стандартный вид");
                        break;
                }
            }
            dailyDomainsService.deleteAll();
            dailyDomainsList.forEach(x -> dailyDomainsService.create(x));
            log.info("Записи новых доменов на сегодня были добавлены, общее количество этих записей составило: "+ dailyDomainsService.countItems());
        } catch (ClientProtocolException ex) {
            log.info("Произошла ошибка при запросе на получение доменов - "+ ex.getMessage());
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            log.info("Произошла ошибка в функции получения доменов - "+ ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
    public void messageUsers(){
        List<Users> usersList = usersService.readAll();
        for(var i: usersList){
            SendMessage message = new SendMessage();
            message.setChatId(i.getId());
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            message.setText(localDateTime.format(formatter)+". Собранно "+ dailyDomainsService.countItems()+ " доменов");
            try{
                execute(message);
            }catch (TelegramApiException e){
                log.error("Ошибка при отправке пользователю с id - "+i.getId()+"сообщения о количестве собранных доменов: "+e.getMessage());
            }
        }
    }
}
