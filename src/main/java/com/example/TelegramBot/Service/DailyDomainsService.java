package com.example.TelegramBot.Service;

import com.example.TelegramBot.DTO.DailyDomainsDTO;
import com.example.TelegramBot.Model.DailyDomains;
import com.example.TelegramBot.Model.Users;
import com.example.TelegramBot.Repository.DailyDomainsRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DailyDomainsService {
    private DailyDomainsRepository dailyDomainsRepository;

    public DailyDomains create(DailyDomains model) {
        Long id = dailyDomainsRepository.findByGetLastDailyDomains();
        DailyDomains dailyDomains = DailyDomains.builder().
                id(++id).
                domainName(model.getDomainName()).
                hotness(model.getHotness()).
                price(model.getPrice()).
                registrar(model.getRegistrar()).
                old(model.getOld()).
                deleteDate(model.getDeleteDate()).
                rkn(model.isRkn()).
                judicial(model.isJudicial()).
                block(model.isBlock())
                .build();
        return dailyDomainsRepository.save(dailyDomains);
    }

    public void createAll(List<DailyDomainsDTO> list) {
        List<DailyDomains> dailyDomainsList = new ArrayList<>();
        long id = 1;
        for(var i: list){
            DailyDomains dailyDomains = DailyDomains.builder().id(id).
                    domainName(i.getDomainName()).
                    hotness(i.getHotness()).
                    price(i.getPrice()).
                    registrar(i.getRegistrar()).
                    old(i.getOld()).
                    deleteDate(i.getDeleteDate()).
                    rkn(i.isRkn()).
                    judicial(i.isJudicial()).
                    block(i.isBlock()).
                    build();
            id++;
            dailyDomainsList.add(dailyDomains);
        }
        dailyDomainsRepository.saveAll(dailyDomainsList);
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
