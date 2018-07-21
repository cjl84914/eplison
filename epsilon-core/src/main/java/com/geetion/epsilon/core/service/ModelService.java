package com.geetion.epsilon.core.service;

import com.geetion.epsilon.core.entity.EpsilonModel;

import java.util.List;

public interface ModelService {
    public void saveByRedis(EpsilonModel model);
    public EpsilonModel loadByRedis(String name);
    public List<EpsilonModel> getList();
}
