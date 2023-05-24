package com.smiley.smileybackend.service;

import com.smiley.smileybackend.domain.TotalExpStastics;
import com.smiley.smileybackend.domain.User;
import com.smiley.smileybackend.dto.response.TotalExpStasticsInfoDto;
import com.smiley.smileybackend.dto.user.DailyWearTimeDto;
import com.smiley.smileybackend.dto.user.ExpJsonDto;
import com.smiley.smileybackend.repository.TierRepositoory;
import com.smiley.smileybackend.repository.TotalExpStasticsRepository;
import com.smiley.smileybackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TotalExpStasticsService {
    private final TotalExpStasticsRepository totalExpStasticsRepository;
    private final UserRepository userRepository;
    private final TierRepositoory tierRepositoory;

    public TotalExpStasticsService(TotalExpStasticsRepository totalExpStasticsRepository, UserRepository userRepository, TierRepositoory tierRepositoory) {
        this.totalExpStasticsRepository = totalExpStasticsRepository;
        this.userRepository = userRepository;
        this.tierRepositoory = tierRepositoory;
    }
    public TotalExpStasticsInfoDto saveTotalExp(DailyWearTimeDto dailyWearTimeDto) {
        User user = userRepository.findById(dailyWearTimeDto.getUserId()).orElseThrow(
                ()-> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );
        List<ExpJsonDto> totalExps = new ArrayList<>();
        totalExps.add(new ExpJsonDto("일일 착용 경험치",dailyWearTimeDto.getTotalWearTime()*10));
        TotalExpStastics totalExpStastics= totalExpStasticsRepository.findById(dailyWearTimeDto.getUserId()).orElse(
                new TotalExpStastics(null,dailyWearTimeDto.getTotalWearTime()*10,null, "BRONZE",user)
        );
        if (totalExpStastics.getTotalExpStastics() != null) {
            totalExps.addAll(totalExpStastics.getTotalExpStastics());
            totalExpStastics.setTotalExp(totalExpStastics.getTotalExp()+dailyWearTimeDto.getTotalWearTime()*10);
        }
        totalExpStastics.setTotalExpStastics(totalExps);
        totalExpStasticsRepository.save(totalExpStastics);
        log.info(getTire(totalExpStastics.getTotalExp()));
        return new TotalExpStasticsInfoDto(totalExpStastics);
    }
    public String getTire(Integer exp){
        return tierRepositoory.findByExpStartAfterBefore(exp).toString();
    }
}
