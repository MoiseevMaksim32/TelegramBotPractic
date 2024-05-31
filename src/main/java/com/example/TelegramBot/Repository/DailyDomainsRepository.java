package com.example.TelegramBot.Repository;

import com.example.TelegramBot.Model.DailyDomains;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyDomainsRepository extends JpaRepository<DailyDomains, Long> {
    @Query(value = "SELECT id FROM public.daily_domains order by id desc limit 1", nativeQuery = true)
    public Long findByGetLastDailyDomains();
}
