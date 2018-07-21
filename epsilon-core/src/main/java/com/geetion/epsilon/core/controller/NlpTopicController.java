package com.geetion.epsilon.core.controller;

import com.geetion.epsilon.core.entity.EpsilonModel;
import com.geetion.epsilon.core.entity.JDBCTable;
import com.geetion.epsilon.core.service.ModelService;
import com.geetion.epsilon.core.service.NlpTopicService;
import com.geetion.epsilon.core.utils.SerializeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.ml.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(description = "文本分类")
public class NlpTopicController {

    @Autowired
    private NlpTopicService nlpTopicService;
    @Autowired
    private ModelService modelService;

    @PostMapping(value = "/npl/topic")
    @ApiOperation(value = "查询表的文本分类")
    public Object getTopic(@RequestBody JDBCTable jdbcTable) {
        EpsilonModel epsilonModel = modelService.loadByRedis("nlp_model");
        Model model = (Model) SerializeUtil.unserialize(epsilonModel.getModel());
        return nlpTopicService.predictByTable(model, jdbcTable);
    }

    @PostMapping(value = "/npl/content")
    @ApiOperation(value = "单个文本分类")
    public Object getonetopic(@RequestParam String content) {
        EpsilonModel epsilonModel = modelService.loadByRedis("nlp_model");
        Model model = (Model) SerializeUtil.unserialize(epsilonModel.getModel());
        return nlpTopicService.predictOne(model, content);
    }
}