package com.geetion.epsilon.core.dao;

import com.geetion.epsilon.core.entity.EpsilonModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class RedisModelDAO {

    private static Logger logger = LoggerFactory.getLogger(RedisModelDAO.class);

    @Autowired
    private RedisManager redisManager;
    private String keyPrefix = "epsilon_model:";

    public void update(EpsilonModel model) {
        this.saveModel(model);
    }

    public void saveModel(EpsilonModel model) {
        if (model == null) {
            logger.error("model id is null");
            return;
        }
        byte[] key = getByteKey(model.getId());
        byte[] value = model.getModel();
        this.redisManager.set(key, value);
    }

    public void delete(String id) {
        if (id == null) {
            logger.error("model id is null");
            return;
        }
        redisManager.del(this.getByteKey(id));

    }

    public List<EpsilonModel> getActiveModel() {
        List<EpsilonModel> list = new ArrayList();
        Set<byte[]> keys = redisManager.keys(this.keyPrefix + "*");
        if (keys != null && keys.size() > 0) {
            for (byte[] key : keys) {
                list.add(new EpsilonModel(key.toString(), redisManager.get(key)));
            }
        }
        return list;
    }

    public EpsilonModel doReadModel(String id) {
        if (id == null) {
            logger.error("model id is null");
            return null;
        }
        return new EpsilonModel(id, redisManager.get(this.getByteKey(id)));
    }

    private byte[] getByteKey(Serializable id) {
        String preKey = this.keyPrefix + id;
        return preKey.getBytes();
    }


    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}