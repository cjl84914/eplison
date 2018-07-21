package com.geetion.epsilon.core.service.impl;

import com.geetion.epsilon.core.dao.RedisModelDAO;
import com.geetion.epsilon.core.entity.EpsilonModel;
import com.geetion.epsilon.core.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModelServiceImpl implements ModelService {
    @Autowired
    private RedisModelDAO redisModelDAO;

    @Override
    public void saveByRedis(EpsilonModel model) {
        redisModelDAO.saveModel(model);
    }

    @Override
    public EpsilonModel loadByRedis(String name) {
        return redisModelDAO.doReadModel(name);
    }

    @Override
    public List<EpsilonModel> getList() {
        return redisModelDAO.getActiveModel();
    }
}