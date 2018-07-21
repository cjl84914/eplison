package com.geetion.epsilon.core.service;

import com.geetion.epsilon.core.entity.Article;
import com.geetion.epsilon.core.entity.JDBCTable;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public interface NlpTopicService {


    public PipelineModel train(Dataset<Row> trainDF);

    public double test(Model model, Dataset<Row> testDF);

    public Object predictByTable(Model model, JDBCTable jdbcTable);

    public Object predictOne(Model model, String content);
}
