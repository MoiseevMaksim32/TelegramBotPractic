package com.example.TelegramBot.Service;

import com.example.TelegramBot.Model.DailyDomains;
import com.example.TelegramBot.Model.Users;
import com.example.TelegramBot.Repository.DailyDomainsRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DailyDomainsService {
    private DailyDomainsRepository dailyDomainsRepository;

    public DailyDomains create(DailyDomains model) {
        DailyDomains dailyDomains = DailyDomains.builder().
                domainName(model.getDomainName()).
                hotness(model.getHotness()).
                price(model.getPrice()).
                xValue(model.getXValue()).
                yandexTic(model.getYandexTic()).
                links(model.getLinks()).
                visitors(model.getVisitors()).
                registrar(model.getRegistrar()).
                old(model.getOld()).
                deleteDate(model.getDeleteDate()).
                rkn(model.isRkn()).
                judicial(model.isJudicial()).
                block(model.isBlock())
                .build();
        return dailyDomainsRepository.save(dailyDomains);
    }

    public void createAll(List<DailyDomains> list) {
        dailyDomainsRepository.saveAllAndFlush(list);
    }


    public DailyDomains update(DailyDomains dailyDomains) {
        return dailyDomainsRepository.save(dailyDomains);
    }

    public DailyDomains readById(Long id) {
        return dailyDomainsRepository.findById(id).orElseThrow(() ->
                new RuntimeException("В таблицы Roles нет значения с таким: " + id));
    }

    public void delete(Long id) {
        dailyDomainsRepository.deleteById(id);
    }

    public void deleteAll() {
        dailyDomainsRepository.deleteAll();
    }

    public Long countItems() {
        return dailyDomainsRepository.count();
    }
}
